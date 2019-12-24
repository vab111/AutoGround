package com.example.autoground;

import android.annotation.SuppressLint;
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
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Bitmap.*;


public class MysurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable, View.OnTouchListener {

    private Point moveOrigin = new Point(0,0);
    private Point Moved = new Point(0,0);
    private boolean isScaling = false;
    private float xMid=0.0f;
    private float yMid=0.0f;
    private double beginDistance;
    public float scale = 2.0f;
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
    private Bitmap TraceBack;
    private Paint pain;
    private Canvas canvas;
    private Canvas canvasBack;
    private int mapWidth = width*3;
    private int mapHeight = height*3;
    private int CNM = 5;
    public String CurrentTask;
    private String[] mapbufferFile = new String[9];
    private String[] mapbufferFileBack = new String[9];
    private String CurName;
    private String NameBack;
    private int bufferstate;
    private Point ex_Point=new Point(0,0);
    private boolean Change = false;
    private int moveDerection;
    private Point traceleft = new Point(0,0);
    private Point traceright = new Point(0,0);
    public MysurfaceView(Context context, AttributeSet attrs){
        super(context,attrs);
        this.getHolder().addCallback(this);
        this.setOnTouchListener(this);
        Trace = createBitmap(mapWidth,mapHeight, Config.ARGB_8888);
        TraceBack = createBitmap(mapWidth,mapHeight, Config.ARGB_8888);
        canvas = new Canvas();
        canvas.setBitmap(Trace);
        canvasBack = new Canvas();
        canvasBack.setBitmap(TraceBack);
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


            drawTrace(canvas);

            if (isB) {

                drawABlines(canvas);
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
        int jianju = 30;
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
        //Log.e("ABLine-角度", String.valueOf(jiaodu));

        Point cur = transform(CurPoint);
        if ((ap.x-bp.x)*(ap.x-bp.x)>(ap.y-bp.y)*(ap.y-bp.y))
        {
            paint.setColor(Color.RED);
            paint.setStrokeWidth(1.0f);
            //TODO y方向做等分
            int jianju = (int) Math.abs(ChanWidth/Math.cos(jiaodu));
            int cury = (int) (cur.y+Math.tan(jiaodu)*(width/2));
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

            int cury = (int) (cur.x+(height/2)/Math.tan(jiaodu));
            //TODO x方向做等分
            int jianju = (int) Math.abs(ChanWidth/Math.sin(jiaodu));

                    for (int j = x; j > -Moved.x-Math.abs(height/Math.tan(jiaodu)); j -= jianju) {
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
        Rect mDestRect = new Rect(car.x-15, car.y-15, car.x+15, car.y+15);

        Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.daohangjiantou);		// 设置canvas画布背景为白色	// 定义矩阵对象
        bmp = rotaingImageView((int) carDerection-90, bmp);
        Matrix matrix = new Matrix();		// 缩放原图
        matrix.postScale(0.8f, 0.8f);		//bmp.getWidth(), bmp.getHeight()分别表示缩放后的位图宽高
        Bitmap dstbmp = createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),				matrix, true);

        canvas.drawBitmap(dstbmp,mSrcRect,mDestRect,paint);

    }
    public void drawA(Canvas canvas)
    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setFilterBitmap(true);

        paint.setDither(true);
        Rect mSrcRect = new Rect(0, 0, 200, 200);
        Point ap = transform(pointA);
        Rect mDestRect = new Rect(ap.x-15, ap.y-15, ap.x+15, ap.y+15);

        Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.adian);		// 设置canvas画布背景为白色	// 定义矩阵对象


        canvas.drawBitmap(bmp,mSrcRect,mDestRect,paint);
    }
    public void drawB(Canvas canvas)
    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setFilterBitmap(true);

        paint.setDither(true);
        Rect mSrcRect = new Rect(0, 0, 200, 200);
        Point ap = transform(pointB);
        Rect mDestRect = new Rect(ap.x-15, ap.y-15, ap.x+15, ap.y+15);

        Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.bdian);		// 设置canvas画布背景为白色	// 定义矩阵对象


        canvas.drawBitmap(bmp,mSrcRect,mDestRect,paint);
    }
    public Point transform(Point curPoint)
    {
        //TODO 坐标变换
        Point result = new Point(0,0);

        result.x  = -curPoint.y;
        result.y = curPoint.x;


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
        Moved.x = (int) (width/(2*scale)-result.x);
        Moved.y = (int) (height/(2*scale)-result.y);
        CurPoint = curPoint;
        if (isTask) {
            endP.x = CurPoint.x;
            endP.y = CurPoint.y;
            drawBuffer();
        }
        if (isB)
            ScreenCaculate(curPoint);
        carDerection = (float) jiaodu;
    }
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap)
    {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = createBitmap(bitmap, 0, 0,
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
        updateTrace(pointB);
        bufferstate = 1;
        updateEXP(pointcur);
        Point dika = new Point(-pointcur.y,-pointcur.x);
        CurName = caculateFileName(dika);




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
            Point start = new Point();
            start.x = -CurPoint.y-ex_Point.x;
            start.y = ex_Point.y+CurPoint.x;

            double jiaodu = Math.atan2(pointA.x-pointB.x,pointA.y-pointB.y);
            traceleft.x = (int) (start.x-Math.sin(jiaodu)*ChanWidth/2);
            traceleft.y = (int) (start.y-Math.cos(jiaodu)*ChanWidth/2);
            traceright.x = (int) (start.x+Math.sin(jiaodu)*ChanWidth/2);
            traceright.y = (int) (start.y+Math.sin(jiaodu)*ChanWidth/2);
        }
    }

    public void updateTrace(Point point)
    {
        //TODO 装载缓存图片
        int xiangxian;

        int hor = -point.y/width;
        int ver = -point.x/height;
        if (-point.x>0)
        {
            if (-point.y>0)
                xiangxian =1;
            else
                xiangxian = 4;
        }
        else
        {
            if (-point.y>0)
                xiangxian = 2;
            else
                xiangxian = 3;
        }
        switch (xiangxian)
        {
            case 1:
                if (-point.x/width == 0)
                {
                    if (-point.y/height==0)
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
                    if (-point.y/height == 0)
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
    public void updateTraceBack(Point point)
    {
        //TODO 装载缓存图片
        int xiangxian;

        int hor = -point.y/width;
        int ver = -point.x/height;
        if (-point.x>0)
        {
            if (-point.y>0)
                xiangxian =1;
            else
                xiangxian = 4;
        }
        else
        {
            if (-point.y>0)
                xiangxian = 2;
            else
                xiangxian = 3;
        }
        switch (xiangxian)
        {
            case 1:
                if (-point.x/width == 0)
                {
                    if (-point.y/height==0)
                    {
                        //TODO 第一象限 （0，0）块，9宫格全4象限
                        mapbufferFileBack[0]= "2-0-1";
                        mapbufferFileBack[1]="1-0-1";
                        mapbufferFileBack[2]= "1-1-1";
                        mapbufferFileBack[3]="2-0-0";
                        mapbufferFileBack[4] ="1-0-0";
                        mapbufferFileBack[5]="1-1-0";
                        mapbufferFileBack[6]="3-0-0";
                        mapbufferFileBack[7]="4-0-0";
                        mapbufferFileBack[8]="4-1-0";

                    }
                    else
                    {
                        //TODO 第一象限，一、二象限
                        mapbufferFileBack[0] = String.format("2-0-%d",ver+1 );
                        mapbufferFileBack[1] = String.format("1-0-%d",ver+1 );
                        mapbufferFileBack[2] = String.format("1-1-%d",ver+1 );
                        mapbufferFileBack[3] = String.format("2-0-%d",ver );
                        mapbufferFileBack[4] = String.format("1-0-%d",ver );
                        mapbufferFileBack[5] = String.format("1-1-%d",ver );
                        mapbufferFileBack[6] = String.format("2-0-%d",ver-1 );
                        mapbufferFileBack[7] = String.format("1-0-%d",ver-1 );
                        mapbufferFileBack[8] = String.format("1-1-%d",ver-1 );

                    }
                }
                else
                {
                    if (-point.y/height == 0)
                    {//TODO 一、四象限

                        mapbufferFileBack[0] = String.format("1-%d-1",hor-1 );
                        mapbufferFileBack[1] = String.format("1-%d-1",hor );
                        mapbufferFileBack[2] = String.format("1-%d-1",hor+1 );
                        mapbufferFileBack[3] = String.format("1-%d-0",hor-1 );
                        mapbufferFileBack[4] = String.format("1-%d-0",hor );
                        mapbufferFileBack[5] = String.format("1-%d-0",hor+1 );
                        mapbufferFileBack[6] = String.format("4-%d-0",hor-1 );
                        mapbufferFileBack[7] = String.format("4-%d-0",hor );
                        mapbufferFileBack[8] = String.format("4-%d-0",hor+1);
                    }
                    else
                    {
                        //TODO，一象限解决

                        mapbufferFileBack[0] = String.format("1-%d-%d",hor-1,ver+1 );
                        mapbufferFileBack[1] = String.format("1-%d-%d",hor ,ver+1);
                        mapbufferFileBack[2] = String.format("1-%d-%d",hor+1,ver+1 );
                        mapbufferFileBack[3] = String.format("1-%d-%d",hor-1,ver );
                        mapbufferFileBack[4] = String.format("1-%d-%d",hor,ver );
                        mapbufferFileBack[5] = String.format("1-%d-%d",hor+1,ver );
                        mapbufferFileBack[6] = String.format("1-%d-%d",hor-1,ver-1 );
                        mapbufferFileBack[7] = String.format("1-%d-%d",hor,ver-1 );
                        mapbufferFileBack[8] = String.format("1-%d-%d",hor+1,ver-1 );
                    }
                }
                break;
            case 2:
                if (point.x/width == 0)
                {
                    if (point.y/height==0)
                    {
                        //TODO 第二象限 （0，0）块，9宫格全4象限
                        mapbufferFileBack[0] = "2-1-1";
                        mapbufferFileBack[1] = "2-0-1";
                        mapbufferFileBack[2] = "1-0-1";
                        mapbufferFileBack[3] = "2-1-0";
                        mapbufferFileBack[4] = "2-0-0";
                        mapbufferFileBack[5] = "1-0-0";
                        mapbufferFileBack[6] = "3-1-0";
                        mapbufferFileBack[7] = "3-0-0";
                        mapbufferFileBack[8] = "4-0-0";
                    }
                    else
                    {
                        //TODO 第一象限，一、二象限
                        mapbufferFileBack[0] = String.format("2-1-%d", ver+1);
                        mapbufferFileBack[1] = String.format("2-0-%d", ver+1);
                        mapbufferFileBack[2] = String.format("1-0-%d", ver+1);
                        mapbufferFileBack[3] = String.format("2-1-%d", ver);
                        mapbufferFileBack[4] = String.format("2-0-%d", ver);
                        mapbufferFileBack[5] = String.format("1-0-%d", ver);
                        mapbufferFileBack[6] = String.format("2-1-%d", ver-1);
                        mapbufferFileBack[7] = String.format("2-0-%d", ver-1);
                        mapbufferFileBack[8] = String.format("1-0-%d", ver-1);
                    }
                }
                else
                {
                    if (point.y/height == 0)
                    {//TODO 二、三象限

                        mapbufferFileBack[0] = String.format("2-%d-1", hor+1);
                        mapbufferFileBack[1] = String.format("2-%d-1", hor);
                        mapbufferFileBack[2] = String.format("2-%d-1", hor+1);
                        mapbufferFileBack[3] = String.format("2-%d-0", hor+1);
                        mapbufferFileBack[4] = String.format("2-%d-0", hor);
                        mapbufferFileBack[5] = String.format("2-%d-0", hor-1);
                        mapbufferFileBack[6] = String.format("3-%d-0", hor+1);
                        mapbufferFileBack[7] = String.format("3-%d-0", hor);
                        mapbufferFileBack[8] = String.format("3-%d-0", hor-1);
                    }
                    else
                    {
                        //TODO，二象限解决
                        mapbufferFileBack[0] = String.format("2-%d-%d",hor+1,ver+1 );
                        mapbufferFileBack[1] = String.format("2-%d-%d",hor ,ver+1);
                        mapbufferFileBack[2] = String.format("2-%d-%d",hor-1,ver+1 );
                        mapbufferFileBack[3] = String.format("2-%d-%d",hor+1,ver );
                        mapbufferFileBack[4] = String.format("2-%d-%d",hor,ver );
                        mapbufferFileBack[5] = String.format("2-%d-%d",hor-1,ver );
                        mapbufferFileBack[6] = String.format("2-%d-%d",hor+1,ver-1 );
                        mapbufferFileBack[7] = String.format("2-%d-%d",hor,ver-1 );
                        mapbufferFileBack[8] = String.format("2-%d-%d",hor-1,ver-1 );
                    }
                }
                break;
            case 3:
                if (point.x/width == 0)
                {
                    if (point.y/height==0)
                    {
                        //TODO 第三象限 （0，0）块，9宫格全4象限
                        mapbufferFileBack[0] ="2-1-0";
                        mapbufferFileBack[1] ="2-0-0";
                        mapbufferFileBack[2] ="1-0-0";
                        mapbufferFileBack[3] ="3-1-0";
                        mapbufferFileBack[4] ="3-0-0";
                        mapbufferFileBack[5] ="4-0-0";
                        mapbufferFileBack[6] ="3-1-1";
                        mapbufferFileBack[7] ="4-0-1";
                        mapbufferFileBack[8] ="4-0-1";
                    }
                    else
                    {
                        //TODO 第三象限，三、四象限
                        mapbufferFileBack[0] = String.format("3-0-%d",ver-1 );
                        mapbufferFileBack[1] = String.format("3-0-%d",ver-1);
                        mapbufferFileBack[2] = String.format("4-1-%d",ver-1 );
                        mapbufferFileBack[3] = String.format("3-0-%d",ver );
                        mapbufferFileBack[4] = String.format("3-0-%d",ver );
                        mapbufferFileBack[5] = String.format("4-1-%d",ver );
                        mapbufferFileBack[6] = String.format("3-0-%d",ver+1 );
                        mapbufferFileBack[7] = String.format("3-0-%d",ver+1 );
                        mapbufferFileBack[8] = String.format("4-1-%d",ver+1 );
                    }
                }
                else
                {
                    if (point.y/height == 0)
                    {//TODO 二、三象限
                        mapbufferFileBack[0] = String.format("2-%d-0",hor+1 );
                        mapbufferFileBack[1] = String.format("2-%d-0",hor );
                        mapbufferFileBack[2] = String.format("2-%d-0",hor-1 );
                        mapbufferFileBack[3] = String.format("3-%d-0",hor+1 );
                        mapbufferFileBack[4] = String.format("3-%d-0",hor );
                        mapbufferFileBack[5] = String.format("3-%d-0",hor-1 );
                        mapbufferFileBack[6] = String.format("3-%d-1",hor+1 );
                        mapbufferFileBack[7] = String.format("3-%d-1",hor );
                        mapbufferFileBack[8] = String.format("3-%d-1",hor-1 );
                    }
                    else
                    {
                        //TODO，三象限解决

                        mapbufferFileBack[0] = String.format("3-%d-%d",hor+1,ver-1 );
                        mapbufferFileBack[1] = String.format("3-%d-%d",hor ,ver-1);
                        mapbufferFileBack[2] = String.format("3-%d-%d",hor-1,ver-1 );
                        mapbufferFileBack[3] = String.format("3-%d-%d",hor+1,ver );
                        mapbufferFileBack[4] = String.format("3-%d-%d",hor,ver );
                        mapbufferFileBack[5] = String.format("3-%d-%d",hor-1,ver );
                        mapbufferFileBack[6] = String.format("3-%d-%d",hor+1,ver+1 );
                        mapbufferFileBack[7] = String.format("3-%d-%d",hor,ver+1 );
                        mapbufferFileBack[8] = String.format("3-%d-%d",hor-1,ver+1 );
                    }
                }
                break;
            case 4:
                if (point.x/width == 0)
                {
                    if (point.y/height==0)
                    {
                        //TODO 第四象限 （0，0）块，9宫格全4象限
                        mapbufferFileBack[0] = "2-0-0";
                        mapbufferFileBack[0] = "1-0-0";
                        mapbufferFileBack[0] = "1-1-0";
                        mapbufferFileBack[0] = "3-0-0";
                        mapbufferFileBack[0] = "4-0-0";
                        mapbufferFileBack[0] = "4-1-0";
                        mapbufferFileBack[0] = "3-1-1";
                        mapbufferFileBack[0] = "4-0-1";
                        mapbufferFileBack[0] = "4-1-1";
                    }
                    else
                    {
                        //TODO 第一象限，三、四象限

                        mapbufferFileBack[0] = String.format("3-0-%d",ver-1 );
                        mapbufferFileBack[1] = String.format("4-0-%d",ver-1 );
                        mapbufferFileBack[2] = String.format("4-1-%d",ver-1 );
                        mapbufferFileBack[3] = String.format("3-0-%d",ver );
                        mapbufferFileBack[4] = String.format("4-0-%d",ver );
                        mapbufferFileBack[5] = String.format("4-1-%d",ver );
                        mapbufferFileBack[6] = String.format("3-0-%d",ver+1 );
                        mapbufferFileBack[7] = String.format("4-0-%d",ver+1 );
                        mapbufferFileBack[8] = String.format("4-1-%d",ver+1);
                    }
                }
                else
                {
                    if (point.y/height == 0)
                    {//TODO 一、四象限

                        mapbufferFileBack[0] = String.format("1-%d-0",hor-1 );
                        mapbufferFileBack[1] = String.format("1-%d-0",hor );
                        mapbufferFileBack[2] = String.format("1-%d-0",hor+1 );
                        mapbufferFileBack[3] = String.format("4-%d-0",hor-1 );
                        mapbufferFileBack[4] = String.format("4-%d-0",hor );
                        mapbufferFileBack[5] = String.format("4-%d-0",hor+1 );
                        mapbufferFileBack[6] = String.format("4-%d-1",hor-1 );
                        mapbufferFileBack[7] = String.format("4-%d-1",hor );
                        mapbufferFileBack[8] = String.format("4-%d-1",hor+1 );
                    }
                    else
                    {
                        //TODO，四象限解决
                        mapbufferFileBack[0] = String.format("4-%d-%d",hor-1,ver-1 );
                        mapbufferFileBack[1] = String.format("4-%d-%d",hor ,ver-1);
                        mapbufferFileBack[2] = String.format("4-%d-%d",hor+1,ver-1 );
                        mapbufferFileBack[3] = String.format("4-%d-%d",hor-1,ver );
                        mapbufferFileBack[4] = String.format("4-%d-%d",hor,ver );
                        mapbufferFileBack[5] = String.format("4-%d-%d",hor+1,ver );
                        mapbufferFileBack[6] = String.format("4-%d-%d",hor-1,ver+1 );
                        mapbufferFileBack[7] = String.format("4-%d-%d",hor,ver+1 );
                        mapbufferFileBack[8] = String.format("4-%d-%d",hor+1,ver+1 );
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
            String filename = Environment.getExternalStorageDirectory()+"/AutoGround/"+CurrentTask+"/"+mapbufferFile[i]+".png";
            File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/"+CurrentTask+"/"+mapbufferFile[i]+".png");
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
    public void loadBufferBack()
    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int x = 0,y = 0;
        canvasBack.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        paint.setFilterBitmap(true);
        for (int i=0;i<9;i++) {
            String filename = Environment.getExternalStorageDirectory()+"/AutoGround/"+CurrentTask+"/"+mapbufferFileBack[i]+".png";
            File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/"+CurrentTask+"/"+mapbufferFileBack[i]+".png");
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
                canvasBack.drawBitmap(bitmap,mSrcRect,mDestRect,paint);
            }
        }

    }
    public String caculateFileName(Point point)//笛卡尔坐标系
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
        Log.e("中心BITMAP", name);
        return name;
    }

    public void ScreenCaculate(Point point)
    {
        Point actrul = new Point();
        actrul.x = -point.y;
        actrul.y = -point.x;
        String name = caculateFileName(actrul);
        Log.e("绘制轨迹：", String.valueOf(bufferstate));
        if (bufferstate == 1)
        {
            if (name.equals(CurName))
            {


            }
            else
            {
                if (name.equals(NameBack))
                {
                    Log.e("绘制轨迹：", String.format("ex_Point(%d,%d),actrul(%d,%d),x方向=%d，y方向%d" , ex_Point.x,ex_Point.y,actrul.x,actrul.y,actrul.x-ex_Point.x,ex_Point.y-actrul.y));
                    if (((actrul.x-ex_Point.x)>width*5/2)||((actrul.x-ex_Point.x)<width/2)||((ex_Point.y-actrul.y)>height*5/2)||((ex_Point.y-actrul.y)<height/2))
                    {
                        Log.e("绘制轨迹：", "切换缓存");
                        copyBuffer();
                        updateEXP(point);
                        bufferstate = 2;
                        saveBitmap(1);



                    }
                }
                else
                {
                    NameBack = name;
                    moveDerection(actrul);
                    updateTraceBack(point);
                    loadBufferBack();
                }
            }
        }
        else
        {
            if (name.equals(NameBack))
            {


            }
            else
            {
                if (name.equals(CurName))
                {
                    Log.e("绘制轨迹：", String.format("ex_Point(%d,%d),actrul(%d,%d),x方向=%d，y方向%d" , ex_Point.x,ex_Point.y,actrul.x,actrul.y,actrul.x-ex_Point.x,ex_Point.y-actrul.y));
                    if (((actrul.x-ex_Point.x)>width*5/2)||((actrul.x-ex_Point.x)<width/2)||((ex_Point.y-actrul.y)>height*5/2)||((ex_Point.y-actrul.y)<height/2))
                    {

                        copyBuffer();
                        updateEXP(point);
                        bufferstate = 1;
                        saveBitmap(2);
                    }

                }
                else {
                    CurName = name;
                    moveDerection(actrul);
                    updateTrace(point);
                    loadBuffer();
                }
            }
        }

    }

    public void drawBuffer()//绘制轨迹缓存
    {
        Point st = new Point();
        Point endp = new Point();
        st.x = -startP.y;
        st.y = -startP.x;
        endp.x = -endP.y;
        endp.y = -endP.x;
        Point start = new Point();
        Point end = new Point();

        start.x = st.x-ex_Point.x;
        start.y = ex_Point.y-st.y;


        end.x = endp.x - ex_Point.x;
        end.y = ex_Point.y-endp.y;



        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(1.0f);

        double jiaodu = Math.atan2(endp.y-st.y,endp.x-st.x);



        Log.e("路径角度", String.format("%f",jiaodu));
        Path curPath = new Path();
        curPath.moveTo(traceleft.x,traceleft.y);
        curPath.lineTo(traceright.x,traceright.y);
        curPath.lineTo((float)(end.x+Math.sin(jiaodu)*ChanWidth/2), (float) (end.y+Math.cos(jiaodu)*ChanWidth/2));
        curPath.lineTo((float)(end.x-Math.sin(jiaodu)*ChanWidth/2), (float) (end.y-Math.cos(jiaodu)*ChanWidth/2));
        curPath.close();
        switch (bufferstate) {
            case 1:
                canvas.drawPath(curPath, paint);
                break;
            case 2:
                canvasBack.drawPath(curPath, paint);

        }
        startP.x = endP.x;
        startP.y = endP.y;

        traceleft.x = (int) (end.x-Math.sin(jiaodu)*ChanWidth/2);
        traceleft.y = (int) (end.y-Math.cos(jiaodu)*ChanWidth/2);
        traceright.x = (int) (end.x+Math.sin(jiaodu)*ChanWidth/2);
        traceright.y = (int) (end.y+Math.cos(jiaodu)*ChanWidth/2);


    }
    public void drawTrace(Canvas canvas)
    {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setFilterBitmap(true);

        paint.setDither(true);
        Rect mSrcRect = new Rect(0, 0, width*3, height*3);//第一个Rect 代表要绘制的bitmap 区域

        Rect mDestRect = new Rect(ex_Point.x, -ex_Point.y,ex_Point.x+width*3, -ex_Point.y+height*3);//第二个 Rect 代表的是要将bitmap 绘制在屏幕的什么地方
        switch (bufferstate)
        {
            case 1:

                canvas.drawBitmap(Trace,mSrcRect,mDestRect,paint);
                break;
            case 2:

                canvas.drawBitmap(TraceBack,mSrcRect,mDestRect,paint);
                break;
        }


    }

    @SuppressLint("WrongThread")
    public void saveBitmap(int state)
    {

        int x = 0,y = 0;
        Canvas canvas = new Canvas();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setFilterBitmap(true);
        int[] saved = new int[0];
        switch (moveDerection)
        {
            case 0:
                saved = new int[]{2, 5, 6, 7, 8};
                break;
            case 1:
                saved = new int[]{ 6, 7, 8};
                break;
            case 2:
                saved = new int[]{0,3, 6, 7, 8};
                break;
            case 3:
                saved = new int[]{2, 5,  8};
                break;
            case 5:
                saved = new int[]{0,3,6};
                break;
            case 6:
                saved = new int[]{0,1,2,5,8};
                break;
            case 7:
                saved = new int[]{0,1,2};
                break;
            case 8:
                saved = new int[]{0,1,2,3,6};
                break;
        }

        for (int i=0;i<saved.length;i++) {
            String fileName = Environment.getExternalStorageDirectory()+"/AutoGround/"+CurrentTask+"/"+mapbufferFile[i]+".png";
            Bitmap mBitmap = createBitmap(width,height, Config.ARGB_8888);
            canvas.setBitmap(mBitmap);
            switch (saved[i])
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
            Rect mDestRect = new Rect(0, 0, width, height);//第一个Rect 代表要绘制的bitmap 区域
            Rect mSrcRect = new Rect(x, y, width+x, height+y);//第二个 Rect 代表的是要将bitmap 绘制在屏幕的什么地方
            switch (state)
            {
                case 1:
                    canvas.drawBitmap(Trace,mSrcRect,mDestRect,paint);
                    break;
                case 2:
                    canvas.drawBitmap(TraceBack,mSrcRect,mDestRect,paint);
                    break;
            }
            canvas.drawBitmap(Trace,mSrcRect,mDestRect,paint);
            try {
                File file = new File(fileName);
                FileOutputStream out = new FileOutputStream(file);
                mBitmap.compress(CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @SuppressLint("WrongThread")
    public void saveTask()
    {
        saveABLine();
        int x = 0,y = 0;
        Canvas canvas = new Canvas();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setFilterBitmap(true);

        for (int i=0;i<9;i++) {
            String fileName = null;
            switch (bufferstate)
            {
                case 1:
                    fileName = Environment.getExternalStorageDirectory()+"/AutoGround/"+CurrentTask+"/"+mapbufferFile[i]+".png";
                    break;
                case 2:
                    fileName = Environment.getExternalStorageDirectory()+"/AutoGround/"+CurrentTask+"/"+mapbufferFileBack[i]+".png";
            }
            Bitmap mBitmap = createBitmap(width,height, Config.ARGB_8888);
            canvas.setBitmap(mBitmap);
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
            Rect mDestRect = new Rect(0, 0, width, height);//第一个Rect 代表要绘制的bitmap 区域
            Rect mSrcRect = new Rect(x, y, width+x, height+y);//第二个 Rect 代表的是要将bitmap 绘制在屏幕的什么地方
            switch (bufferstate)
            {
                case 1:
                    canvas.drawBitmap(Trace,mSrcRect,mDestRect,paint);
                    break;
                case 2:
                    canvas.drawBitmap(TraceBack,mSrcRect,mDestRect,paint);
                    break;
            }
            canvas.drawBitmap(Trace,mSrcRect,mDestRect,paint);
            try {
                File file = new File(fileName);
                FileOutputStream out = new FileOutputStream(file);
                mBitmap.compress(CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
       this.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        this.canvasBack.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }
    public void copyBuffer()
    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);



        paint.setFilterBitmap(true);

        paint.setDither(true);
        Rect mSrcRect = new Rect(0, 0, width*3, height*3);//第一个Rect 代表要绘制的bitmap 区域

        Rect mDestRect = new Rect(0, 0,width*3, height*3);//第二个 Rect 代表的是要将bitmap 绘制在屏幕的什么地方
        switch (moveDerection)
        {
            case 0:
                mSrcRect = new Rect(0, 0, width*2, height*2);
                mDestRect = new Rect(width, height,width*3, height*3);
                traceleft.x+=width;
                traceleft.y+=height;
                traceright.x +=width;
                traceright.y +=height;
                break;
            case 1:
                mSrcRect = new Rect(0, 0, width*3, height*2);
                mDestRect = new Rect(0, height,width*3, height*3);
                traceright.y+=height;
                traceleft.y +=height;
                break;
            case 2:
                mSrcRect = new Rect(width, 0, width*3, height*2);
                mDestRect = new Rect(0, height,width*2, height*3);
                traceleft.x-=width;
                traceleft.y+=height;
                traceright.x-=width;
                traceright.y+=height;
                break;
            case 3:
                mSrcRect = new Rect(0, 0, width*2, height*3);
                mDestRect = new Rect(width, 0,width*3, height*3);
                traceright.x+=width;
                traceleft.x+=width;
                break;
            case 5:
                mSrcRect = new Rect(width, 0, width*3, height*3);
                mDestRect = new Rect(0, 0,width*2, height*3);
                traceleft.x-=width;
                traceright.x-=width;
                break;
            case 6:
                mSrcRect = new Rect(0, height, width*2, height*3);
                mDestRect = new Rect(width, 0,width*3, height*2);
                traceright.x+=width;
                traceright.y-=height;
                traceleft.x+=width;
                traceleft.y-=height;
                break;
            case 7:
                mSrcRect = new Rect(0, height, width*3, height*3);
                mDestRect = new Rect(0, 0,width*3, height*2);
                traceleft.y-=height;
                traceright.y-=height;
                break;
            case 8:
                mSrcRect = new Rect(width, height, width*3, height*3);
                mDestRect = new Rect(0, 0,width*2, height*2);
                traceright.x-=width;
                traceright.y-=height;
                traceleft.x-=width;
                traceleft.y-=height;
                break;
        }

        switch (bufferstate)
        {
            case 1:
                canvasBack.drawBitmap(Trace,mSrcRect,mDestRect,paint);
                break;
            case 2:
                canvas.drawBitmap(TraceBack,mSrcRect,mDestRect,paint);
                break;
        }


    }
    public void moveDerection(Point point)
    {

        moveDerection = (point.x-ex_Point.x)/width+(ex_Point.y-point.y)/height*3;
    }
    public void updateEXP(Point point)
    {
        Point diker = new Point(-point.y,-point.x);
        if (diker.x>0)
            ex_Point.x = (diker.x/width-1)*width;
        else
            ex_Point.x = (diker.x/width-2)*width;
        if (diker.y>0)
            ex_Point.y = (diker.y/height+2)*height;
        else
            ex_Point.y = (diker.y/height+1)*height;
    }
    public void saveABLine()
    {
        RecordInfor recordInfor = new RecordInfor();
        recordInfor.isA = isA;
        recordInfor.isB = isB;
        if (isA)
            recordInfor.pointA = pointA;
        if (isB)
            recordInfor.pointB = pointB;
        recordInfor.Kuan = ChanWidth;
        File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/"+CurrentTask+".json");
        List fileList = new ArrayList();
        try {
            FileOutputStream outputStream =new FileOutputStream(fs);
            OutputStreamWriter outStream = new OutputStreamWriter(outputStream);


            fileList.add(recordInfor);
            Gson gson = new Gson();
            String jsonString = gson.toJson(fileList);
            outStream.write(jsonString);

            outputStream.flush();
            outStream.flush();
            outputStream.close();
            outputStream.close();



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
