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
    private int ChanWidth = 30;

    public boolean isTask = false;
    private Point startP = new Point(0,0);
    private Point endP = new Point(0,0);
    public MysurfaceView(Context context, AttributeSet attrs){
        super(context,attrs);
        this.getHolder().addCallback(this);
        this.setOnTouchListener(this);
        CurPoint.x = width/2;
        CurPoint.y = height/2;
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


            if(isA)
                drawA(canvas);
            if (isB) {
                drawB(canvas);
                drawABlines(canvas);
            }
            if (isTask)
                drawTask(canvas);
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
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(1.0f);
        int startX = (int) (Moved.x/scale);
        int startY = (int) (Moved.y/scale);
        int widthscale = (int) (width/scale);
        int heightscale = (int) (height/scale);
        for(int i=startX%20-20-startX;i<widthscale-startX;i+=20)
            canvas.drawLine(i,-startY,i,heightscale-startY,paint);
        for (int i=startY%20-20-startY;i<heightscale-startY;i+=20)
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
//        paint.setColor(Color.YELLOW);
//        paint.setStrokeWidth(100.0f);
//        canvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paint);
        double jiaodu = Math.atan2(pointA.y-pointB.y, pointB.x-pointA.x);
        int x = (int) (pointB.x+(pointB.y+Moved.y)/Math.tan(jiaodu));
        int y = (int) (pointA.y+(pointA.x+Moved.x)*Math.tan(jiaodu));
        Log.e("ABLine--角度", String.valueOf(jiaodu));
        paint.setColor(Color.RED);
        paint.setStrokeWidth(1.0f);

        if ((pointA.x-pointB.x)*(pointA.x-pointB.x)>(pointA.y-pointB.y)*(pointA.y-pointB.y))
        {

            //TODO y方向做等分
            int jianju = (int) Math.abs(ChanWidth/Math.sin(jiaodu));

            for (int j=y;j>-Moved.y;j-=jianju)
                canvas.drawLine(-Moved.x, j, -Moved.x+width, (float) (j-width*Math.tan(jiaodu)), paint);

            for (int j=y+jianju;j<-Moved.y+height+width*Math.tan(jiaodu);j+=jianju)
                canvas.drawLine(-Moved.x, j, -Moved.x+width, (float) (j-width*Math.tan(jiaodu)), paint);


        }
        else
        {
            //TODO x方向做等分
            int jianju = (int) Math.abs(ChanWidth/Math.cos(jiaodu));

             for (int j=x;j>-Moved.x;j-=jianju)
                 canvas.drawLine(j, -Moved.y, (float) (j-height/Math.tan(jiaodu)), -Moved.y+height, paint);

             for (int j=x+jianju;(float) (j-height/Math.tan(jiaodu))<-Moved.x+width+height/Math.tan(jiaodu);j+=jianju)
                 canvas.drawLine(j, -Moved.y, (float) (j-height/Math.tan(jiaodu)), -Moved.y+height, paint);


        }


    }
    public void drawCar(Canvas canvas)
    {
        //TODO 绘制车辆当前方向
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setFilterBitmap(true);

        paint.setDither(true);
        Rect mSrcRect = new Rect(0, 0, 200, 200);
        Rect mDestRect = new Rect(CurPoint.x-40, CurPoint.y-40, CurPoint.x+40, CurPoint.y+40);

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
        Rect mDestRect = new Rect(pointA.x-40, pointA.y-40, pointA.x+40, pointA.y+40);

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
        Rect mDestRect = new Rect(pointB.x-40, pointB.y-40, pointB.x+40, pointB.y+40);

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
        curPoint.x = (int) (distance*Math.cos(Math.toRadians(jiaodu)));
        curPoint.y = (int)(distance*Math.sin(Math.toRadians(jiaodu)));
        result.x = curPoint.x;
        result.y = - curPoint.y;


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
        CurPoint = result;
        if (isTask)
            endP = CurPoint;
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
            pointA = transform(pointcur);
    }
    public void setB(Point pointcur)
    {
        isB = true;
        pointB = transform(pointcur);
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
            startP = CurPoint;
        }
    }
    public void drawTask(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(30.0f);
        canvas.drawLine(startP.x, startP.y, endP.x, endP.y, paint);
    }


}
