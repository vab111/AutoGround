package com.example.autoground;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.print.PrinterId;
import android.util.AttributeSet;
import android.util.Log;
import android.view.CollapsibleActionView;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.lang.annotation.Target;
import java.lang.reflect.Modifier;

public class MysurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable, View.OnTouchListener {
    private Point start = new Point(0,0);
    private Point moveOrigin = new Point(0,0);
    private Point Moved = new Point(0,0);
    private boolean isScaling = false;
    private float xMid=0.0f;
    private float yMid=0.0f;
    private double beginDistance;
    private float scale = 1.0f;
    private float zoomscale = 1.0f;
    private float mapDerection = 0.0f;//地图本身方向角，正北为0，顺时针增长
    private boolean ismoving=false;
    private int width=1024;
    private int height=552;
    public boolean isA = false;
    public boolean isB = false;
    private Point pointA = new Point(0,0);
    private Point pointB = new Point(0,0);
    private Point CurPoint = new Point(0,0);
    private float carDerection;
    private int ChanWidth = 300;

    public boolean isTask = false;
    private Point startP = new Point(0,0);
    private Point endP = new Point(0,0);
    private Bitmap Trace;
    private Paint pain;
    private Canvas canvas;
    private int mapWidth = width*3;
    private int mapHeight = height*3;
    private int CNM = 5;


    public MysurfaceView(Context context, AttributeSet attrs){
        super(context,attrs);
        this.getHolder().addCallback(this);
        this.setOnTouchListener(this);
        Trace = Bitmap.createBitmap(mapWidth,mapHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        canvas.setBitmap(Trace);

        CurPoint.x = 0;
        CurPoint.y = 0;

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }



    @Override
    public void run() {

        long t = 0 ;
        Canvas canvas;

        while (true) {
            canvas = this.getHolder().lockCanvas();
            if (canvas == null)
                return;

            canvas.drawColor(Color.WHITE);
            canvas.translate(Moved.x,Moved.y);
            canvas.scale(scale,scale);

            drawBg(canvas, this.getWidth(), this.getHeight());
            drawCar(canvas);



            if (isB) {
                drawABlines(canvas);
                drawHistory(canvas);
                drawA(canvas);
                drawB(canvas);
            }
            else
            {
                if(isA)
                    drawA(canvas);
            }

            this.getHolder().unlockCanvasAndPost(canvas);
            try {
                Thread.sleep(Math.max(0, 50-(System.currentTimeMillis()-t)));
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block					e.printStackTrace();
                //
            }
        }
    }
    public void drawBg(Canvas canvas, int width, int height)
    {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(1.0f);
        int startX = (int) (Moved.x/scale);
        int startY = (int) (Moved.y/scale);
        int widthscale = (int) (width/scale);
        int heightscale = (int) (height/scale);
        int jianju = 100;
        for(int i=startX%jianju-jianju-startX;i<widthscale-startX;i+=jianju)
            canvas.drawLine(i,-startY,i,heightscale-startY,paint);
        for (int i=startY%jianju-jianju-startY;i<heightscale-startY;i+=jianju)
            canvas.drawLine(-startX,i,widthscale-startX,i,paint);

    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                //TODO 添加item选中判断



                moveOrigin = new Point((int)motionEvent.getX(),(int)motionEvent.getY());
                ismoving = true;


                break;
            case MotionEvent.ACTION_MOVE:
                //绘制墙壁
                if (motionEvent.getPointerCount()==1) {


                    //TODO 添加平移操作
                    if (ismoving) {
                        Moved.x += (int) (motionEvent.getX() - moveOrigin.x) * scale;
                        Moved.y += (int) (motionEvent.getY() - moveOrigin.y) * scale;
                        moveOrigin.x = (int) motionEvent.getX();
                        moveOrigin.y = (int) motionEvent.getY();

                    }

                }


                break;
            case MotionEvent.ACTION_UP:


                ismoving = false;

                break;

            default:
                break;
        }

        return true;
    }
    public void drawABlines(Canvas canvas)
    {
        //TODO 绘制AB线，红色
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        Point ap = transform(pointA);
        Point bp = transform(pointB);

        double jiaodu = Math.atan2(ap.y-bp.y, bp.x-ap.x);
        int x = (int) (bp.x+(bp.y+Moved.y)/Math.tan(jiaodu));
        int y = (int) (ap.y+(ap.x+Moved.x)*Math.tan(jiaodu));
        Log.e("ABLine--角度", String.valueOf(jiaodu));

        Point cur = transform(CurPoint);
        if ((ap.x-bp.x)*(ap.x-bp.x)>(ap.y-bp.y)*(ap.y-bp.y))
        {
            paint.setColor(Color.RED);
            paint.setStrokeWidth(1.0f);
            //TODO y方向做等分
            int jianju = (int) Math.abs(ChanWidth/Math.sin(jiaodu));
            int cury = (int) (cur.y-Math.tan(jiaodu)*width/2);
            for (int j=y;j>-Moved.y;j-=jianju) {
                if (Math.abs(cury-j)<=ChanWidth/2)
                {
                    paint.setColor(Color.GRAY);
                    paint.setStrokeWidth(ChanWidth);
                    canvas.drawLine((float) (-Moved.x-Math.abs(Math.sin(jiaodu)*ChanWidth/2)), (float) (j-Math.abs(Math.cos(jiaodu)*ChanWidth/2)), -Moved.x + width, (float) (j - width * Math.tan(jiaodu)), paint);
                }
                paint.setColor(Color.RED);
                paint.setStrokeWidth(2.0f);
                canvas.drawLine(-Moved.x, j, -Moved.x + width, (float) (j - width * Math.tan(jiaodu)), paint);
            }
            for (int j=y+jianju;j<-Moved.y+height+width*Math.tan(jiaodu);j+=jianju)
            {
                if (Math.abs(cury-j)<=ChanWidth/2)
                {
                    paint.setColor(Color.GRAY);
                    paint.setStrokeWidth(ChanWidth);
                    canvas.drawLine((float) (-Moved.x-Math.abs(Math.sin(jiaodu)*ChanWidth/2)), (float) (j-Math.abs(Math.cos(jiaodu)*ChanWidth/2)), -Moved.x + width, (float) (j - width * Math.tan(jiaodu)), paint);
                }
                paint.setColor(Color.RED);
                paint.setStrokeWidth(2.0f);
                canvas.drawLine(-Moved.x, j, -Moved.x + width, (float) (j - width * Math.tan(jiaodu)), paint);
            }


        }
        else
        {
            paint.setColor(Color.RED);
            paint.setStrokeWidth(1.0f);

            int cury = (int) (cur.x+Math.tan(jiaodu)*height/2);
            //TODO x方向做等分
            int jianju = (int) Math.abs(ChanWidth/Math.cos(jiaodu));

             for (int j=x;j>-Moved.x;j-=jianju){
                 if (Math.abs(cury-j)<=ChanWidth/2)
                 {
                     paint.setColor(Color.GRAY);
                     paint.setStrokeWidth(ChanWidth);

                     canvas.drawLine((float) (j-Math.abs(Math.sin(jiaodu)*ChanWidth/2)), (float) (-Moved.y-Math.abs(Math.cos(jiaodu)*ChanWidth/2)), (float) (j-height/Math.tan(jiaodu)-Math.abs(Math.sin(jiaodu)*ChanWidth/2)), -Moved.y+height, paint);
                 }
                paint.setColor(Color.RED);
                paint.setStrokeWidth(2.0f);
                canvas.drawLine(j, -Moved.y, (float) (j-height/Math.tan(jiaodu)), -Moved.y+height, paint);
             }

             for (int j=x+jianju;(float) (j-height/Math.tan(jiaodu))<-Moved.x+width+height/Math.tan(jiaodu);j+=jianju) {
                 if (Math.abs(cury - j) <= ChanWidth / 2) {
                     paint.setColor(Color.GRAY);
                     paint.setStrokeWidth(ChanWidth);

                     canvas.drawLine((float) (j - Math.abs(Math.sin(jiaodu) * ChanWidth / 2)), (float) (-Moved.y - Math.abs(Math.cos(jiaodu) * ChanWidth / 2)), (float) (j - height / Math.tan(jiaodu) - Math.abs(Math.sin(jiaodu) * ChanWidth / 2)), -Moved.y + height, paint);
                 }
                 paint.setColor(Color.RED);
                 paint.setStrokeWidth(2.0f);
                 canvas.drawLine(j, -Moved.y, (float) (j - height / Math.tan(jiaodu)), -Moved.y + height, paint);

             }
        }


    }
    public void drawCar(Canvas canvas)
    {
        //TODO 绘制车辆当前方向
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setFilterBitmap(true);

        paint.setDither(true);
        Point car = transform(CurPoint);
        Rect mSrcRect = new Rect(0, 0, 200, 200);
        Rect mDestRect = new Rect(car.x-40, car.y-40, car.x+40, car.y+40);

        Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.daohangjiantou);		// 设置canvas画布背景为白色	// 定义矩阵对象
        bmp = rotaingImageView((int) carDerection-90, bmp);
        Matrix matrix = new Matrix();		// 缩放原图
        matrix.postScale(0.4f, 0.4f);		//bmp.getWidth(), bmp.getHeight()分别表示缩放后的位图宽高
        Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),				matrix, true);

        canvas.drawBitmap(dstbmp,mSrcRect,mDestRect,paint);

    }
    public void drawA(Canvas canvas)
    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setFilterBitmap(true);

        paint.setDither(true);
        Rect mSrcRect = new Rect(0, 0, 200, 200);
        Point ap = transform(pointA);
        Rect mDestRect = new Rect(ap.x-40, ap.y-40, ap.x+40, ap.y+40);

        Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.adian);		// 设置canvas画布背景为白色	// 定义矩阵对象
        bmp = rotaingImageView((int) carDerection-90, bmp);
        Matrix matrix = new Matrix();		// 缩放原图
        matrix.postScale(0.4f, 0.4f);		//bmp.getWidth(), bmp.getHeight()分别表示缩放后的位图宽高
        Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),				matrix, true);

        canvas.drawBitmap(dstbmp,mSrcRect,mDestRect,paint);
    }
    public void drawB(Canvas canvas)
    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setFilterBitmap(true);

        paint.setDither(true);
        Rect mSrcRect = new Rect(0, 0, 200, 200);
        Point ap = transform(pointB);
        Rect mDestRect = new Rect(ap.x-40, ap.y-40, ap.x+40, ap.y+40);

        Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.bdian);		// 设置canvas画布背景为白色	// 定义矩阵对象
        bmp = rotaingImageView((int) carDerection-90, bmp);
        Matrix matrix = new Matrix();		// 缩放原图
        matrix.postScale(0.4f, 0.4f);		//bmp.getWidth(), bmp.getHeight()分别表示缩放后的位图宽高
        Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),				matrix, true);

        canvas.drawBitmap(dstbmp,mSrcRect,mDestRect,paint);
    }
    public Point transform(Point curPoint)
    {
        //TODO 坐标变换
        Point result = new Point(0,0);
        double distance = (double) Math.sqrt(curPoint.x*curPoint.x+curPoint.y*curPoint.y);
        double jiaodu = Math.toDegrees(Math.atan2(curPoint.x,curPoint.y));
        jiaodu-=mapDerection;
        result.x  = (int) (distance*Math.cos(Math.toRadians(jiaodu)));
        result.y = - (int)(distance*Math.sin(Math.toRadians(jiaodu)));


        return result;
    }
    public void getSize()
    {
        //TODO 获取屏幕尺寸
        this.getWidth();
        this.getHeight();
    }
    public void handleData(Point curPoint,double jiaodu)
    {
//移动画布，使中心显示
        Point result = transform(curPoint);
        Moved.x = width/2-result.x;
        Moved.y = height/2-result.y;
        CurPoint = curPoint;
        if (isTask) {
            endP.x = CurPoint.x;
            endP.y = CurPoint.y;
            updateTask();
        }
        carDerection = (float) jiaodu;
    }
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap)
    {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (resizedBitmap != bitmap && bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
        }
        return resizedBitmap;
    }
    public void setA(Point pointcur)
    {

            isA = true;
            pointA.x = pointcur.x;
            pointA.y = pointcur.y;
    }
    public void setB(Point pointcur)
    {
        isB = true;

        pointB.x = pointcur.x;
        pointB.y = pointcur.y;

    }
    public void setTaskOn()
    {
        if (isTask)
        {
            isTask = false;
        }
        else
        {
            isTask = true;
            startP.x = CurPoint.x;
            startP.y = CurPoint.y;
        }
    }
    public void updateTask()
    {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(30.0f);
        canvas.drawLine(startP.x+mapWidth/2, mapHeight/2-startP.y, endP.x+mapWidth/2, mapHeight/2-endP.y, paint);
        startP.x = endP.x;
        startP.y = endP.y;
    }
    public void drawHistory(Canvas canvas)
    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setFilterBitmap(true);

        paint.setDither(true);
        Rect mSrcRect = new Rect(0, 0, width, height);//第一个Rect 代表要绘制的bitmap 区域

        Rect mDestRect = new Rect(Moved.x, Moved.y, width+Moved.x, height+Moved.y);//第二个 Rect 代表的是要将bitmap 绘制在屏幕的什么地方

        	// 设置canvas画布背景为白色	// 定义矩阵对象
        //Trace = rotaingImageView((int) carDerection-90, Trace);
//        Matrix matrix = new Matrix();		// 缩放原图
//        matrix.postScale(10.0f, 10.0f);		//bmp.getWidth(), bmp.getHeight()分别表示缩放后的位图宽高
//        Bitmap dstbmp = Bitmap.createBitmap(Trace, CurPoint.x+width/2-width/20, height/2-CurPoint.y-height/20, width/10, height/10,				matrix, true);

        canvas.drawBitmap(Trace,mSrcRect,mDestRect,paint);

    }


}
