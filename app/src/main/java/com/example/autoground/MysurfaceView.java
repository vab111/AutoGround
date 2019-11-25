package com.example.autoground;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MysurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable, View.OnTouchListener {

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
    private Bitmap Trace;
    private Paint pain;
    private Canvas canvas;
    private int mapWidth = width*3;
    private int mapHeight = height*3;
    private int CNM = 5;
    private String CurrentTask;
    private String[] mapbufferFile = new String[9];
    private String CurName;
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
            ScreenCaculate();
            canvas.drawColor(Color.WHITE);
            canvas.translate(Moved.x,Moved.y);
            canvas.scale(scale,scale);

            drawBg(canvas, this.getWidth(), this.getHeight());




            if (isB) {
                drawABlines(canvas);
                drawHistory(canvas);
                drawA(canvas);
                drawB(canvas);
                drawTrace(canvas);
            }
            else
            {
                if(isA)
                    drawA(canvas);
            }
            drawCar(canvas);
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
        paint.setStyle(Paint.Style.FILL);
        Point ap = transform(pointA);
        Point bp = transform(pointB);

        double jiaodu = Math.atan2(ap.y-bp.y, bp.x-ap.x);
        int x = (int) (bp.x+(bp.y+Moved.y)/Math.tan(jiaodu));
        int y = (int) (ap.y+(ap.x+Moved.x)*Math.tan(jiaodu));
        Log.e("ABLine-角度", String.valueOf(jiaodu));

        Point cur = transform(CurPoint);
        if ((ap.x-bp.x)*(ap.x-bp.x)>(ap.y-bp.y)*(ap.y-bp.y))
        {
            paint.setColor(Color.RED);
            paint.setStrokeWidth(1.0f);
            //TODO y方向做等分
            int jianju = (int) Math.abs(ChanWidth/Math.cos(jiaodu));
            int cury = (int) (cur.y+Math.tan(jiaodu)*width/2);
            for (int j=y;j>-Moved.y-Math.abs(Math.tan(jiaodu)*width);j-=jianju) {
                if (Math.abs(cury-j)<=jianju/2)
                {
                    paint.setColor(Color.GRAY);
                    Path curPath = new Path();
                    curPath.moveTo(-Moved.x, j-jianju/2);
                    curPath.lineTo(-Moved.x, j+jianju/2);
                    curPath.lineTo(-Moved.x + width,(float) (j - width * Math.tan(jiaodu)+jianju/2));
                    curPath.lineTo(-Moved.x+width, (float) (j - width * Math.tan(jiaodu)-jianju/2));
                    curPath.close();
                    canvas.drawPath(curPath, paint);

                    paint.setColor(Color.RED);
                    paint.setStrokeWidth(2.0f);
                    canvas.drawLine(-Moved.x, j, -Moved.x + width, (float) (j - width * Math.tan(jiaodu)), paint);
                }
                paint.setColor(Color.RED);
                paint.setStrokeWidth(1.0f);
                canvas.drawLine(-Moved.x, j, -Moved.x + width, (float) (j - width * Math.tan(jiaodu)), paint);
            }
            for (int j=y+jianju;j<-Moved.y+height+Math.abs(width*Math.tan(jiaodu));j+=jianju)
            {
                if (Math.abs(cury-j)<=jianju/2)
                {
                    paint.setColor(Color.GRAY);
                    Path curPath = new Path();
                    curPath.moveTo(-Moved.x, j-jianju/2);
                    curPath.lineTo(-Moved.x, j+jianju/2);
                    curPath.lineTo(-Moved.x + width,(float) (j - width * Math.tan(jiaodu)+jianju/2));
                    curPath.lineTo(-Moved.x+width, (float) (j - width * Math.tan(jiaodu)-jianju/2));
                    curPath.close();
                    canvas.drawPath(curPath, paint);

                    paint.setColor(Color.RED);
                    paint.setStrokeWidth(2.0f);
                    canvas.drawLine(-Moved.x, j, -Moved.x + width, (float) (j - width * Math.tan(jiaodu)), paint);
                }
                paint.setColor(Color.RED);
                paint.setStrokeWidth(1.0f);
                canvas.drawLine(-Moved.x, j, -Moved.x + width, (float) (j - width * Math.tan(jiaodu)), paint);
            }


        }
        else
        {
            paint.setColor(Color.RED);
            paint.setStrokeWidth(1.0f);

            int cury = (int) (cur.x+height/(2*Math.tan(jiaodu)));
            //TODO x方向做等分
            int jianju = (int) Math.abs(ChanWidth/Math.sin(jiaodu));

                    for (int j = x; j > -Moved.x+height/Math.tan(jiaodu); j -= jianju) {
                        if (Math.abs(cury - j) <= jianju/2) {
                            paint.setColor(Color.GRAY);
                            Path curPath = new Path();
                            curPath.moveTo(j - jianju / 2, -Moved.y);
                            curPath.lineTo(j + jianju / 2, -Moved.y);
                            curPath.lineTo((float) (j - height / Math.tan(jiaodu)) + jianju / 2, -Moved.y + height);
                            curPath.lineTo((float) (j - height / Math.tan(jiaodu)) - jianju / 2, -Moved.y + height);
                            curPath.close();
                            canvas.drawPath(curPath, paint);

                            paint.setColor(Color.RED);
                            paint.setStrokeWidth(2.0f);
                            canvas.drawLine(j, -Moved.y, (float) (j - height / Math.tan(jiaodu)), -Moved.y + height, paint);
                        } else {
                            paint.setColor(Color.RED);
                            paint.setStrokeWidth(1.0f);
                            canvas.drawLine(j, -Moved.y, (float) (j - height / Math.tan(jiaodu)), -Moved.y + height, paint);
                        }
                    }

                    for (int j = x + jianju; (float) (j - height / Math.tan(jiaodu)) < -Moved.x + width + height / Math.tan(jiaodu); j += jianju) {
                        if (Math.abs(cury - j) <= jianju/2) {
                            paint.setColor(Color.GRAY);

                            Path curPath = new Path();
                            curPath.moveTo(j - jianju / 2, -Moved.y);
                            curPath.lineTo(j + jianju / 2, -Moved.y);
                            curPath.lineTo((float) (j - height / Math.tan(jiaodu)) + jianju / 2, -Moved.y + height);
                            curPath.lineTo((float) (j - height / Math.tan(jiaodu)) - jianju / 2, -Moved.y + height);
                            curPath.close();
                            canvas.drawPath(curPath, paint);

                            paint.setColor(Color.RED);
                            paint.setStrokeWidth(2.0f);
                            canvas.drawLine(j, -Moved.y, (float) (j - height / Math.tan(jiaodu)), -Moved.y + height, paint);
                        } else {
                            paint.setColor(Color.RED);
                            paint.setStrokeWidth(1.0f);
                            canvas.drawLine(j, -Moved.y, (float) (j - height / Math.tan(jiaodu)), -Moved.y + height, paint);
                        }
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
        Rect mDestRect = new Rect(car.x-15, car.y-15, car.x+65, car.y+65);

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
        Rect mDestRect = new Rect(ap.x-15, ap.y-15, ap.x+65, ap.y+65);

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
        Rect mDestRect = new Rect(ap.x-15, ap.y-15, ap.x+65, ap.y+65);

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
            endP.x = CurPoint.x;
            endP.y = CurPoint.y;
        }
    }
    public void updateTask()
    {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(30.0f);
        canvas.drawLine(0 , 0, width, height, paint);

    }
    public void drawHistory(Canvas canvas)
    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setFilterBitmap(true);

        paint.setDither(true);
        Rect mSrcRect = new Rect(0, 0, width, height);//第一个Rect 代表要绘制的bitmap 区域

        Rect mDestRect = new Rect(-Moved.x, -Moved.y,-Moved.x+ width, -Moved.y+height);//第二个 Rect 代表的是要将bitmap 绘制在屏幕的什么地方



        canvas.drawBitmap(Trace,mSrcRect,mDestRect,paint);

    }
    public void updateTrace(Point point)
    {
        //TODO 装载缓存图片
        int xiangxian;

        int hor = point.x/width;
        int ver = point.y/height;
        if (point.x>0)
        {
            if (point.y>0)
                xiangxian =1;
            else
                xiangxian = 4;
        }
        else
        {
            if (point.y>0)
                xiangxian = 2;
            else
                xiangxian = 3;
        }
        switch (xiangxian)
        {
            case 1:
                if (point.x/width == 0)
                {
                    if (point.y/height==0)
                    {
                        //TODO 第一象限 （0，0）块，9宫格全4象限
                        mapbufferFile[0]= "2-0-1";
                        mapbufferFile[1]="1-0-1";
                        mapbufferFile[2]= "1-1-1";
                        mapbufferFile[3]="2-0-0";
                        mapbufferFile[4] ="1-0-0";
                        mapbufferFile[5]="1-1-0";
                        mapbufferFile[6]="3-0-0";
                        mapbufferFile[7]="4-0-0";
                        mapbufferFile[8]="4-1-0";

                    }
                    else
                    {
                        //TODO 第一象限，一、二象限
                        mapbufferFile[0] = String.format("2-0-%d",ver+1 );
                        mapbufferFile[1] = String.format("1-0-%d",ver+1 );
                        mapbufferFile[2] = String.format("1-1-%d",ver+1 );
                        mapbufferFile[3] = String.format("2-0-%d",ver );
                        mapbufferFile[4] = String.format("1-0-%d",ver );
                        mapbufferFile[5] = String.format("1-1-%d",ver );
                        mapbufferFile[6] = String.format("2-0-%d",ver-1 );
                        mapbufferFile[7] = String.format("1-0-%d",ver-1 );
                        mapbufferFile[8] = String.format("1-1-%d",ver-1 );

                    }
                }
                else
                {
                    if (point.y/height == 0)
                    {//TODO 一、四象限

                        mapbufferFile[0] = String.format("1-%d-1",hor-1 );
                        mapbufferFile[1] = String.format("1-%d-1",hor );
                        mapbufferFile[2] = String.format("1-%d-1",hor+1 );
                        mapbufferFile[3] = String.format("1-%d-0",hor-1 );
                        mapbufferFile[4] = String.format("1-%d-0",hor );
                        mapbufferFile[5] = String.format("1-%d-0",hor+1 );
                        mapbufferFile[6] = String.format("4-%d-0",hor-1 );
                        mapbufferFile[7] = String.format("4-%d-0",hor );
                        mapbufferFile[8] = String.format("4-%d-0",hor+1);
                    }
                    else
                    {
                        //TODO，一象限解决

                        mapbufferFile[0] = String.format("1-%d-%d",hor-1,ver+1 );
                        mapbufferFile[1] = String.format("1-%d-%d",hor ,ver+1);
                        mapbufferFile[2] = String.format("1-%d-%d",hor+1,ver+1 );
                        mapbufferFile[3] = String.format("1-%d-%d",hor-1,ver );
                        mapbufferFile[4] = String.format("1-%d-%d",hor,ver );
                        mapbufferFile[5] = String.format("1-%d-%d",hor+1,ver );
                        mapbufferFile[6] = String.format("1-%d-%d",hor-1,ver-1 );
                        mapbufferFile[7] = String.format("1-%d-%d",hor,ver-1 );
                        mapbufferFile[8] = String.format("1-%d-%d",hor+1,ver-1 );
                    }
                }
                break;
            case 2:
                if (point.x/width == 0)
                {
                    if (point.y/height==0)
                    {
                        //TODO 第二象限 （0，0）块，9宫格全4象限
                        mapbufferFile[0] = "2-1-1";
                        mapbufferFile[1] = "2-0-1";
                        mapbufferFile[2] = "1-0-1";
                        mapbufferFile[3] = "2-1-0";
                        mapbufferFile[4] = "2-0-0";
                        mapbufferFile[5] = "1-0-0";
                        mapbufferFile[6] = "3-1-0";
                        mapbufferFile[7] = "3-0-0";
                        mapbufferFile[8] = "4-0-0";
                    }
                    else
                    {
                        //TODO 第一象限，一、二象限
                        mapbufferFile[0] = String.format("2-1-%d", ver+1);
                        mapbufferFile[1] = String.format("2-0-%d", ver+1);
                        mapbufferFile[2] = String.format("1-0-%d", ver+1);
                        mapbufferFile[3] = String.format("2-1-%d", ver);
                        mapbufferFile[4] = String.format("2-0-%d", ver);
                        mapbufferFile[5] = String.format("1-0-%d", ver);
                        mapbufferFile[6] = String.format("2-1-%d", ver-1);
                        mapbufferFile[7] = String.format("2-0-%d", ver-1);
                        mapbufferFile[8] = String.format("1-0-%d", ver-1);
                    }
                }
                else
                {
                    if (point.y/height == 0)
                    {//TODO 二、三象限

                        mapbufferFile[0] = String.format("2-%d-1", hor+1);
                        mapbufferFile[1] = String.format("2-%d-1", hor);
                        mapbufferFile[2] = String.format("2-%d-1", hor+1);
                        mapbufferFile[3] = String.format("2-%d-0", hor+1);
                        mapbufferFile[4] = String.format("2-%d-0", hor);
                        mapbufferFile[5] = String.format("2-%d-0", hor-1);
                        mapbufferFile[6] = String.format("3-%d-0", hor+1);
                        mapbufferFile[7] = String.format("3-%d-0", hor);
                        mapbufferFile[8] = String.format("3-%d-0", hor-1);
                    }
                    else
                    {
                        //TODO，二象限解决
                        mapbufferFile[0] = String.format("2-%d-%d",hor+1,ver+1 );
                        mapbufferFile[1] = String.format("2-%d-%d",hor ,ver+1);
                        mapbufferFile[2] = String.format("2-%d-%d",hor-1,ver+1 );
                        mapbufferFile[3] = String.format("2-%d-%d",hor+1,ver );
                        mapbufferFile[4] = String.format("2-%d-%d",hor,ver );
                        mapbufferFile[5] = String.format("2-%d-%d",hor-1,ver );
                        mapbufferFile[6] = String.format("2-%d-%d",hor+1,ver-1 );
                        mapbufferFile[7] = String.format("2-%d-%d",hor,ver-1 );
                        mapbufferFile[8] = String.format("2-%d-%d",hor-1,ver-1 );
                    }
                }
                break;
            case 3:
                if (point.x/width == 0)
                {
                    if (point.y/height==0)
                    {
                        //TODO 第三象限 （0，0）块，9宫格全4象限
                        mapbufferFile[0] ="2-1-0";
                        mapbufferFile[1] ="2-0-0";
                        mapbufferFile[2] ="1-0-0";
                        mapbufferFile[3] ="3-1-0";
                        mapbufferFile[4] ="3-0-0";
                        mapbufferFile[5] ="4-0-0";
                        mapbufferFile[6] ="3-1-1";
                        mapbufferFile[7] ="4-0-1";
                        mapbufferFile[8] ="4-0-1";
                    }
                    else
                    {
                        //TODO 第三象限，三、四象限
                        mapbufferFile[0] = String.format("3-0-%d",ver-1 );
                        mapbufferFile[1] = String.format("3-0-%d",ver-1);
                        mapbufferFile[2] = String.format("4-1-%d",ver-1 );
                        mapbufferFile[3] = String.format("3-0-%d",ver );
                        mapbufferFile[4] = String.format("3-0-%d",ver );
                        mapbufferFile[5] = String.format("4-1-%d",ver );
                        mapbufferFile[6] = String.format("3-0-%d",ver+1 );
                        mapbufferFile[7] = String.format("3-0-%d",ver+1 );
                        mapbufferFile[8] = String.format("4-1-%d",ver+1 );
                    }
                }
                else
                {
                    if (point.y/height == 0)
                    {//TODO 二、三象限
                        mapbufferFile[0] = String.format("2-%d-0",hor+1 );
                        mapbufferFile[1] = String.format("2-%d-0",hor );
                        mapbufferFile[2] = String.format("2-%d-0",hor-1 );
                        mapbufferFile[3] = String.format("3-%d-0",hor+1 );
                        mapbufferFile[4] = String.format("3-%d-0",hor );
                        mapbufferFile[5] = String.format("3-%d-0",hor-1 );
                        mapbufferFile[6] = String.format("3-%d-1",hor+1 );
                        mapbufferFile[7] = String.format("3-%d-1",hor );
                        mapbufferFile[8] = String.format("3-%d-1",hor-1 );
                    }
                    else
                    {
                        //TODO，三象限解决

                        mapbufferFile[0] = String.format("3-%d-%d",hor+1,ver-1 );
                        mapbufferFile[1] = String.format("3-%d-%d",hor ,ver-1);
                        mapbufferFile[2] = String.format("3-%d-%d",hor-1,ver-1 );
                        mapbufferFile[3] = String.format("3-%d-%d",hor+1,ver );
                        mapbufferFile[4] = String.format("3-%d-%d",hor,ver );
                        mapbufferFile[5] = String.format("3-%d-%d",hor-1,ver );
                        mapbufferFile[6] = String.format("3-%d-%d",hor+1,ver+1 );
                        mapbufferFile[7] = String.format("3-%d-%d",hor,ver+1 );
                        mapbufferFile[8] = String.format("3-%d-%d",hor-1,ver+1 );
                    }
                }
                break;
            case 4:
                if (point.x/width == 0)
                {
                    if (point.y/height==0)
                    {
                        //TODO 第四象限 （0，0）块，9宫格全4象限
                        mapbufferFile[0] = "2-0-0";
                        mapbufferFile[0] = "1-0-0";
                        mapbufferFile[0] = "1-1-0";
                        mapbufferFile[0] = "3-0-0";
                        mapbufferFile[0] = "4-0-0";
                        mapbufferFile[0] = "4-1-0";
                        mapbufferFile[0] = "3-1-1";
                        mapbufferFile[0] = "4-0-1";
                        mapbufferFile[0] = "4-1-1";
                    }
                    else
                    {
                        //TODO 第一象限，三、四象限

                        mapbufferFile[0] = String.format("3-0-%d",ver-1 );
                        mapbufferFile[1] = String.format("4-0-%d",ver-1 );
                        mapbufferFile[2] = String.format("4-1-%d",ver-1 );
                        mapbufferFile[3] = String.format("3-0-%d",ver );
                        mapbufferFile[4] = String.format("4-0-%d",ver );
                        mapbufferFile[5] = String.format("4-1-%d",ver );
                        mapbufferFile[6] = String.format("3-0-%d",ver+1 );
                        mapbufferFile[7] = String.format("4-0-%d",ver+1 );
                        mapbufferFile[8] = String.format("4-1-%d",ver+1);
                    }
                }
                else
                {
                    if (point.y/height == 0)
                    {//TODO 一、四象限

                        mapbufferFile[0] = String.format("1-%d-0",hor-1 );
                        mapbufferFile[1] = String.format("1-%d-0",hor );
                        mapbufferFile[2] = String.format("1-%d-0",hor+1 );
                        mapbufferFile[3] = String.format("4-%d-0",hor-1 );
                        mapbufferFile[4] = String.format("4-%d-0",hor );
                        mapbufferFile[5] = String.format("4-%d-0",hor+1 );
                        mapbufferFile[6] = String.format("4-%d-1",hor-1 );
                        mapbufferFile[7] = String.format("4-%d-1",hor );
                        mapbufferFile[8] = String.format("4-%d-1",hor+1 );
                    }
                    else
                    {
                        //TODO，四象限解决
                        mapbufferFile[0] = String.format("4-%d-%d",hor-1,ver-1 );
                        mapbufferFile[1] = String.format("4-%d-%d",hor ,ver-1);
                        mapbufferFile[2] = String.format("4-%d-%d",hor+1,ver-1 );
                        mapbufferFile[3] = String.format("4-%d-%d",hor-1,ver );
                        mapbufferFile[4] = String.format("4-%d-%d",hor,ver );
                        mapbufferFile[5] = String.format("4-%d-%d",hor+1,ver );
                        mapbufferFile[6] = String.format("4-%d-%d",hor-1,ver+1 );
                        mapbufferFile[7] = String.format("4-%d-%d",hor,ver+1 );
                        mapbufferFile[8] = String.format("4-%d-%d",hor+1,ver+1 );
                    }
                }
                break;
                default:
                    break;
        }

    }
    public void loadBuffer()
    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int x = 0,y = 0;
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        paint.setFilterBitmap(true);
        for (int i=0;i<9;i++) {
            String filename = Environment.getExternalStorageDirectory()+"/AutoGround/"+CurrentTask+"/"+mapbufferFile[i];
            File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/"+CurrentTask+"/"+mapbufferFile[i]);
            if (fs.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(filename);
                Rect mSrcRect = new Rect(0, 0, width, height);//第一个Rect 代表要绘制的bitmap 区域
                switch (i)
                {
                    case 0:
                        x=0;
                        y=0;
                        break;
                    case 1:
                        x=width;
                        y=0;
                        break;
                    case 2:
                        x=width*2;
                        y=0;
                        break;
                    case 3:
                        x=0;
                        y=height;
                        break;
                    case 4:
                        x=width;
                        y=height;
                        break;
                    case 5:
                        x=width*2;
                        y=height;
                        break;
                    case 6:
                        x=0;
                        y=height*2;
                        break;
                    case 7:
                        x=width;
                        y=height*2;
                        break;
                    case 8:
                        x=width*2;
                        y=height*2;
                        break;

                }
                Rect mDestRect = new Rect(x, y, width+x, height+y);//第二个 Rect 代表的是要将bitmap 绘制在屏幕的什么地方
                canvas.drawBitmap(bitmap,mSrcRect,mDestRect,paint);
            }
        }
    }

    public String caculateFileName(Point point)
    {
        int xiangxian=0;
        int hor = 0;
        int ver = 0;
        if (point.x>0)
        {
            if (point.y>0)
                xiangxian =1;
            else
                xiangxian = 4;
        }
        else
        {
            if (point.y>0)
                xiangxian = 2;
            else
                xiangxian = 3;
        }
        hor = point.x/width;
        ver = point.y/height;
        String name = String.format("%d-%d-%d", xiangxian,hor,ver);
        return name;
    }

    public void ScreenCaculate()
    {
        Point actrul = new Point();
        actrul.x = width/2-Moved.x;
        actrul.y = -(height/2-Moved.y);
        String name = caculateFileName(actrul);
        if (name.equals(CurName))
        {
            Log.e("中心BITMAP", "相同");
        }
        else
        {
            Log.e("中心BITMAP", "相同");
            updateTrace(actrul);
            loadBuffer();
        }
        if ((isTask)&&((startP.x!=endP.x)||(startP.y!=endP.y)))
            drawBuffer();
    }
    public void drawBuffer()//绘制轨迹缓存
    {
        Point st = new Point();
        Point endp = new Point();
        st.x = startP.y;
        st.y = startP.x;
        endp.x = endP.y;
        endp.y = endP.x;
        Point start = new Point();
        Point end = new Point();
        int xiangxian=0;

        if (endp.x>0)
        {
            if (endp.y>0)
                xiangxian =1;
            else
                xiangxian = 4;
        }
        else
        {
            if (endp.y>0)
                xiangxian = 2;
            else
                xiangxian = 3;
        }
        switch (xiangxian)
        {
            case 1:
                if (endp.x%width == 0){
                    end.x = width*2;
                }
                else {
                    end.x = endp.x % width + width;
                }
                if (endp.y%height==0)
                {
                    end.y = height;
                }
                else
                {
                    end.y = height*2-endp.y%height;
                }
                start.x = end.x-(endp.x-st.x);
                start.y = end.y-(endp.y-st.y);

                break;
            case 2:
                if (endp.x%width == 0){
                    end.x = width*2;
                }
                else {
                    end.x = endp.x % width + 2*width;
                }
                if (endp.y%height==0)
                {
                    end.y = height;
                }
                else
                {
                    end.y = 2*height-endp.y%height;
                }
                start.x = end.x-(endp.x-st.x);
                start.y = end.y-(endp.y-st.y);
                break;
            case 3:
                if (endp.x%width == 0){
                    end.x = 2*width;
                }
                else {
                    end.x = endp.x % width + 2*width;
                }
                if (endp.y%height==0)
                {
                    end.y = height;
                }
                else
                {
                    end.y = height-endp.y%height;
                }
                start.x = end.x-(endp.x-st.x);
                start.y = end.y-(endp.y-st.y);
                break;
            case 4:
                if (endp.x%width == 0){
                    end.x = 2*width;
                }
                else {
                    end.x = endp.x % width + width;
                }
                if (endp.y%height==0)
                {
                    end.y = height;
                }
                else
                {
                    end.y = height-endp.y%height;
                }
                start.x = end.x-(endp.x-st.x);
                start.y = end.y-(endp.y-st.y);
                break;
        }

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(1.0f);
        double jiaodu = Math.toDegrees(Math.atan2(end.x-start.x,end.y-start.y));

        Path curPath = new Path();
        curPath.moveTo((float) (start.x+Math.sin(jiaodu)*ChanWidth/2), (float) (start.y+Math.cos(jiaodu)*ChanWidth/2));
        curPath.lineTo((float)(start.x-Math.sin(jiaodu)*ChanWidth/2), (float) (start.y-Math.cos(jiaodu)*ChanWidth/2));
        curPath.lineTo((float)(end.x-Math.sin(jiaodu)*ChanWidth/2), (float) (end.y-Math.cos(jiaodu)*ChanWidth/2));
        curPath.lineTo((float)(end.x+Math.sin(jiaodu)*ChanWidth/2), (float) (end.y+Math.cos(jiaodu)*ChanWidth/2));
        curPath.close();
        canvas.drawPath(curPath, paint);


        startP.x = endP.x;
        startP.y = endP.y;



    }
    public void drawTrace(Canvas canvas)
    {
        Point actrul = new Point();
        actrul.x = width/2-Moved.x;
        actrul.y = -(height/2-Moved.y);
        int xiangxian=0;
        Point endp = new Point();
        endp.x = actrul.x;
        endp.y = actrul.y;
        Point end = new Point();
        if (endp.x>0)
        {
            if (endp.y>0)
                xiangxian =1;
            else
                xiangxian = 4;
        }
        else
        {
            if (endp.y>0)
                xiangxian = 2;
            else
                xiangxian = 3;
        }
        switch (xiangxian)
        {
            case 1:
                if (endp.x%width == 0){
                    end.x = 3*width/2;
                }
                else {
                    end.x = endp.x % width + width/2;
                }
                if (endp.y%height==0)
                {
                    end.y = height/2;
                }
                else
                {
                    end.y = 3*height/2-endp.y%height;
                }

                break;
            case 2:
                if (endp.x%width == 0){
                    end.x = 3*width/2;
                }
                else {
                    end.x = endp.x % width + 3*width/2;
                }
                if (endp.y%height==0)
                {
                    end.y = height/2;
                }
                else
                {
                    end.y = 3*height/2-endp.y%height;
                }
                break;
            case 3:
                if (endp.x%width == 0){
                    end.x = 3*width/2;
                }
                else {
                    end.x = endp.x % width + 3*width/2;
                }
                if (endp.y%height==0)
                {
                    end.y = height/2;
                }
                else
                {
                    end.y = height/2-endp.y%height;
                }
                break;
            case 4:
                if (endp.x%width == 0){
                    end.x = 3*width/2;
                }
                else {
                    end.x = endp.x % width + width/2;
                }
                if (endp.y%height==0)
                {
                    end.y = height/2;
                }
                else
                {
                    end.y = height/2-endp.y%height;
                }
                break;
        }
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setFilterBitmap(true);

        paint.setDither(true);
        Rect mSrcRect = new Rect(endp.x, endp.y, endp.x+width, endp.y+height);//第一个Rect 代表要绘制的bitmap 区域

        Rect mDestRect = new Rect(-Moved.x, -Moved.y,-Moved.x+ width, -Moved.y+height);//第二个 Rect 代表的是要将bitmap 绘制在屏幕的什么地方



        canvas.drawBitmap(Trace,mSrcRect,mDestRect,paint);


    }
}
