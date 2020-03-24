package com.example.autoground;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.PriorityQueue;

import static android.graphics.Bitmap.createBitmap;

public class historySurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable, View.OnTouchListener{
    private Point moveOrigin = new Point(0,0);
    private Point Moved = new Point(0,0);
    private boolean ismoving=false;
    private float scale = 1.0f;
    private Point pointA = new Point(0,0);
    private Point pointB = new Point(0,0);
    private float carDerection;
    public String taskname;
    private int width=1024;
    private int height=600;
    private int mapWidth = width*3;
    private int mapHeight = height*3;
    public int bufferstate;
    private RecordInfor recordInfor;
    public boolean refresh = true;
    public String[] mapbufferFile = new String[9];
    public String[] mapbufferFileBack = new String[9];
    public Bitmap Trace;
    public Bitmap TraceBack;
    public Canvas canvas;
    public Canvas canvasBack;
    private String CurName;
    private String NameBack;
    private int moveDerection;
    private Point location = new Point(0,0);

    public historySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.getHolder().addCallback(this);
        this.setOnTouchListener(this);
        Trace = createBitmap(mapWidth,mapHeight, Bitmap.Config.ARGB_8888);
        TraceBack = createBitmap(mapWidth,mapHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        canvas.setBitmap(Trace);
        canvasBack = new Canvas();
        canvasBack.setBitmap(TraceBack);

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
        getTask();
        if (recordInfor.isA)
            pointA = recordInfor.pointA;
        if (recordInfor.isB) {
            pointB = recordInfor.pointB;
            initmapBuffer();
        }

        Canvas canvas;
        long t = 0 ;

        while(refresh){
            canvas = this.getHolder().lockCanvas();
            if (canvas == null)
                return;
            canvas.drawColor(Color.WHITE);
            drawBg(canvas,width,height);

            if (recordInfor.isB) {
                drawMAP(canvas);
                drawA(canvas);
                drawB(canvas);
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
        int jianju = 30;
        int startX = -jianju+Moved.x%jianju;
        int startY = -jianju+Moved.y%jianju;

        for(int i=startX;i<width+jianju;i+=jianju)
            canvas.drawLine(i,startY,i,height+jianju,paint);
        for (int i=startY;i<height+jianju;i+=jianju)
            canvas.drawLine(startX,i,width+jianju,i,paint);

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
                        int x = (int) (motionEvent.getX() - moveOrigin.x);
                        int y = (int) (motionEvent.getY() - moveOrigin.y);

                        Moved.x+=x;
                        Moved.y+=y;


                        moveOrigin.x = (int) motionEvent.getX();
                        moveOrigin.y = (int) motionEvent.getY();

                    }
                }


                break;
            case MotionEvent.ACTION_UP:
                moveDerection();
                if ((Math.abs(Moved.x)>width/2)||(Math.abs(Moved.y)>height/2)) {
                    switch (moveDerection) {
                        case 0:
                            Moved.x -= width;
                            Moved.y -= height;
                            location.x -= width;
                            location.y += height;
                            break;
                        case 1:
                            Moved.y -= height;
                            location.y += height;
                            break;
                        case 2:
                            Moved.x += width;
                            Moved.y -= height;
                            location.x += width;
                            location.y += height;
                            break;
                        case 3:
                            Moved.x -= width;
                            location.x -= width;
                            break;
                        case 5:
                            Moved.x += width;
                            location.x += width;
                            break;
                        case 6:
                            Moved.x -= width;
                            Moved.y += height;
                            location.x -= width;
                            location.y -= height;
                            break;
                        case 7:
                            Moved.y += height;
                            location.y -= height;
                            break;
                        case 8:
                            Moved.x += width;
                            Moved.y += height;
                            location.x += width;
                            location.y -= height;
                            break;
                    }
                    if (bufferstate == 1) {
                        CurName = "";
                        bufferstate = 2;
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    }
                    else {
                        NameBack = "";
                        bufferstate = 1;
                        canvasBack.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    }
                }
                ismoving = false;

                break;

            default:
                break;
        }

        return true;
    }
    public Point transform(Point curPoint)
    {
        //TODO 坐标变换
        Point result = new Point(0,0);

        result.x  = -curPoint.y;
        result.y = curPoint.x;


        return result;
    }
    public void drawA(Canvas canvas)
    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setFilterBitmap(true);

        paint.setDither(true);
        Rect mSrcRect = new Rect(0, 0, 200, 200);
        Point ap = new Point();
        ap.x = -recordInfor.pointB.y;
        ap.y = -recordInfor.pointB.x;
        ap.x = -(ap.x%width);
        ap.y = -(ap.y%height);
        ap.x+=(recordInfor.pointB.y-recordInfor.pointA.y);
        ap.y+=(recordInfor.pointA.x-recordInfor.pointB.x);
        ap.x = ap.x+(-recordInfor.pointB.y-location.x)+Moved.x;
        ap.y = ap.y+(location.y+recordInfor.pointB.x)+Moved.y;
        Rect mDestRect = new Rect(ap.x-15, ap.y-15, ap.x+65, ap.y+65);

        Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.adian);		// 设置canvas画布背景为白色	// 定义矩阵对象

        Matrix matrix = new Matrix();		// 缩放原图
        matrix.postScale(0.4f, 0.4f);		//bmp.getWidth(), bmp.getHeight()分别表示缩放后的位图宽高
        Bitmap dstbmp = createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),				matrix, true);

        canvas.drawBitmap(dstbmp,mSrcRect,mDestRect,paint);
    }
    public void drawB(Canvas canvas)
    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setFilterBitmap(true);

        paint.setDither(true);
        Rect mSrcRect = new Rect(0, 0, 200, 200);
        Point ap = new Point();
        ap.x = -recordInfor.pointB.y;
        ap.y = -recordInfor.pointB.x;
        ap.x = -ap.x%width;
        ap.y = -ap.y%height;
        ap.x = ap.x+(-recordInfor.pointB.y-location.x)+Moved.x;
        ap.y = ap.y+(location.y+recordInfor.pointB.x)+Moved.y;

        Rect mDestRect = new Rect(ap.x-15, ap.y-15, ap.x+65, ap.y+65);

        Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.bdian);		// 设置canvas画布背景为白色	// 定义矩阵对象

        Matrix matrix = new Matrix();		// 缩放原图
        matrix.postScale(0.4f, 0.4f);		//bmp.getWidth(), bmp.getHeight()分别表示缩放后的位图宽高
        Bitmap dstbmp = createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),				matrix, true);

        canvas.drawBitmap(dstbmp,mSrcRect,mDestRect,paint);
    }


    public void getTask() {
        List taskList = new ArrayList();
        File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/"+taskname+".json");
        if (fs.exists()) {
            String result = "";
            try {
                FileInputStream f = new FileInputStream(fs);
                BufferedReader bis = new BufferedReader(new InputStreamReader(f));
                String line = "";
                while ((line = bis.readLine()) != null) {
                    result += line;
                }
                bis.close();
                f.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (result.length()>0) {
                Gson gson = new Gson();
                taskList = gson.fromJson(result, new TypeToken<List<RecordInfor>>() {
                }.getType());
                recordInfor = (RecordInfor) taskList.get(0);
            }
        }
    }
    public void getSize() {
        //TODO 获取屏幕尺寸
        this.getWidth();
        this.getHeight();
    }
    public void initmapBuffer()
    {
        Point bpoint = new Point();
        bpoint.x = -recordInfor.pointB.y;
        bpoint.y = -recordInfor.pointB.x;
        CurName = caculateFileName(bpoint);
        updateTrace(recordInfor.pointB);
        loadBuffer();
        bufferstate = 1;
        location.x = bpoint.x;
        location.y = bpoint.y;
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
        hor = Math.abs(point.x/width);
        ver = Math.abs(point.y/height);
        String name = String.format("%d-%d-%d", xiangxian,hor,ver);
        Log.e("中心BITMAP", name);
        return name;
    }
    public void updateTrace(Point point)    {
        //TODO 装载缓存图片
        int xiangxian;
        switch (bufferstate)
        {
            case 1:

                break;
            case 2:
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                break;
        }


        int hor = Math.abs(point.y/width);
        int ver = Math.abs(point.x/height);
        if (-point.y>0)
        {
            if (-point.x>0)
                xiangxian =1;
            else
                xiangxian = 4;
        }
        else
        {
            if (-point.x>0)
                xiangxian = 2;
            else
                xiangxian = 3;
        }
        switch (xiangxian)
        {
            case 1:
                if (hor == 0)
                {
                    if (ver==0)
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
                    if (ver == 0)
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

                if (hor==0)
                {
                    if (ver==0)
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
                    if (ver == 0)
                    {//TODO 二、三象限

                        mapbufferFile[0] = String.format("2-%d-1", hor+1);
                        mapbufferFile[1] = String.format("2-%d-1", hor);
                        mapbufferFile[2] = String.format("2-%d-1", hor-1);
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
                if (hor == 0)
                {
                    if (ver==0)
                    {
                        //TODO 第三象限 （0，0）块，9宫格全4象限
                        mapbufferFile[0] ="2-1-0";
                        mapbufferFile[1] ="2-0-0";
                        mapbufferFile[2] ="1-0-0";
                        mapbufferFile[3] ="3-1-0";
                        mapbufferFile[4] ="3-0-0";
                        mapbufferFile[5] ="4-0-0";
                        mapbufferFile[6] ="3-1-1";
                        mapbufferFile[7] ="3-0-1";
                        mapbufferFile[8] ="4-0-1";
                    }
                    else
                    {
                        //TODO 第三象限，三、四象限
                        mapbufferFile[0] = String.format("3-1-%d",ver-1 );
                        mapbufferFile[1] = String.format("3-0-%d",ver-1);
                        mapbufferFile[2] = String.format("4-0-%d",ver-1 );
                        mapbufferFile[3] = String.format("3-1-%d",ver );
                        mapbufferFile[4] = String.format("3-0-%d",ver );
                        mapbufferFile[5] = String.format("4-0-%d",ver );
                        mapbufferFile[6] = String.format("3-1-%d",ver+1 );
                        mapbufferFile[7] = String.format("3-0-%d",ver+1 );
                        mapbufferFile[8] = String.format("4-0-%d",ver+1 );
                    }
                }
                else
                {
                    if (ver == 0)
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
                if (hor == 0)
                {
                    if (ver==0)
                    {
                        //TODO 第四象限 （0，0）块，9宫格全4象限
                        mapbufferFile[0] = "2-0-0";
                        mapbufferFile[0] = "1-0-0";
                        mapbufferFile[0] = "1-1-0";
                        mapbufferFile[0] = "3-0-0";
                        mapbufferFile[0] = "4-0-0";
                        mapbufferFile[0] = "4-1-0";
                        mapbufferFile[0] = "3-0-1";
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
                    if (ver == 0)
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
    public void updateTraceBack(Point point)    {
        //TODO 装载缓存图片
        int xiangxian;

        int hor = Math.abs(point.y/width);
        int ver = Math.abs(point.x/height);
        if (-point.y>0)
        {
            if (-point.x>0)
                xiangxian =1;
            else
                xiangxian = 4;
        }
        else
        {
            if (-point.x>0)
                xiangxian = 2;
            else
                xiangxian = 3;
        }
        switch (xiangxian)
        {
            case 1:
                if (hor == 0)
                {
                    if (ver==0)
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
                    if (ver == 0)
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
                if (hor == 0)
                {
                    if (ver==0)
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
                    if (ver == 0)
                    {//TODO 二、三象限

                        mapbufferFileBack[0] = String.format("2-%d-1", hor+1);
                        mapbufferFileBack[1] = String.format("2-%d-1", hor);
                        mapbufferFileBack[2] = String.format("2-%d-1", hor-1);
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
                if (hor== 0)
                {
                    if (ver==0)
                    {
                        //TODO 第三象限 （0，0）块，9宫格全4象限
                        mapbufferFileBack[0] ="2-1-0";
                        mapbufferFileBack[1] ="2-0-0";
                        mapbufferFileBack[2] ="1-0-0";
                        mapbufferFileBack[3] ="3-1-0";
                        mapbufferFileBack[4] ="3-0-0";
                        mapbufferFileBack[5] ="4-0-0";
                        mapbufferFileBack[6] ="3-1-1";
                        mapbufferFileBack[7] ="3-0-1";
                        mapbufferFileBack[8] ="4-0-1";
                    }
                    else
                    {
                        //TODO 第三象限，三、四象限
                        mapbufferFileBack[0] = String.format("3-1-%d",ver-1 );
                        mapbufferFileBack[1] = String.format("3-0-%d",ver-1);
                        mapbufferFileBack[2] = String.format("4-0-%d",ver-1 );
                        mapbufferFileBack[3] = String.format("3-1-%d",ver );
                        mapbufferFileBack[4] = String.format("3-0-%d",ver );
                        mapbufferFileBack[5] = String.format("4-0-%d",ver );
                        mapbufferFileBack[6] = String.format("3-1-%d",ver+1 );
                        mapbufferFileBack[7] = String.format("3-0-%d",ver+1 );
                        mapbufferFileBack[8] = String.format("4-0-%d",ver+1 );
                    }
                }
                else
                {
                    if (ver == 0)
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
                if (hor == 0)
                {
                    if (ver==0)
                    {
                        //TODO 第四象限 （0，0）块，9宫格全4象限
                        mapbufferFileBack[0] = "2-0-0";
                        mapbufferFileBack[0] = "1-0-0";
                        mapbufferFileBack[0] = "1-1-0";
                        mapbufferFileBack[0] = "3-0-0";
                        mapbufferFileBack[0] = "4-0-0";
                        mapbufferFileBack[0] = "4-1-0";
                        mapbufferFileBack[0] = "3-0-1";
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
                    if (ver == 0)
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
    public void loadBuffer()    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int x = 0,y = 0;
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        paint.setFilterBitmap(true);
        for (int i=0;i<9;i++) {
            String filename = Environment.getExternalStorageDirectory()+"/AutoGround/"+taskname+"/"+mapbufferFile[i]+".png";
            File fs = new File(Environment.getExternalStorageDirectory()+"/AutoGround/"+taskname+"/"+mapbufferFile[i]+".png");
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
    public void drawMAP(Canvas canvas)
    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setFilterBitmap(true);

        paint.setDither(true);
        Rect mSrcRect = new Rect(0, 0, width*3, height*3);//第一个Rect 代表要绘制的bitmap 区域
        Point location = new Point();
        location.x = -width+Moved.x;
        location.y = -height+Moved.y;

        //Log.e("轨迹:", String.format("%d,%dCUR点：%d,%d", ex_Point.x,ex_Point.y,CurPoint.x,CurPoint.y));
        Rect mDestRect = new Rect(location.x, location.y,location.x+width*3, location.y+height*3);//第二个 Rect 代表的是要将bitmap 绘制在屏幕的什么地方
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
    public void moveDerection()    {
        Point newLocation = new Point(0,0);
        newLocation.x = location.x;
        newLocation.y = location.y;
        if (Moved.x<-width/2)
        {
            //TODO 右移
            if(Moved.y>height/2){
                moveDerection = 2;
                newLocation.x = location.x+width;
                newLocation.y = location.y+height;
            }
            else
            {
                if (Moved.y<-height/2) {
                    moveDerection = 8;
                    newLocation.x = location.x+width;
                    newLocation.y = location.y-height;
                }
                else {
                    moveDerection = 5;
                    newLocation.x = location.x+width;
                }
            }
        }
        else
        {
            if (Moved.x>width/2)
            {//TODO 左移
                if(Moved.y>height/2) {
                    moveDerection = 0;
                    newLocation.x = location.x-width;
                    newLocation.y = location.y+height;
                }
                else
                {
                    if (Moved.y<-height/2) {
                        moveDerection = 6;
                        newLocation.x = location.x-width;
                        newLocation.y = location.y-height;
                    }
                    else {
                        moveDerection = 3;
                        newLocation.x = location.x-width;
                    }
                }
            }
            else
            {
                if(Moved.y>height/2) {
                    moveDerection = 1;
                    newLocation.y = location.y+height;
                }
                else
                {
                    if (Moved.y<-height/2){
                        moveDerection = 7;
                        newLocation.y = location.y-height;
                    }
                }
            }
        }
        String name = caculateFileName(newLocation);
        Point point = new Point();
        point.x = -newLocation.y;
        point.y = -newLocation.x;
        if (bufferstate == 1)
        {
            if (name.equals(CurName))
            {

            }
            else
            {
                if (name.equals(NameBack))
                {

                }
                else
                {
                    //TODO 更新内存
                    NameBack = name;
                    updateTraceBack(point);
                    updateBuffer();
                    copyBuffer();
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

                }
                else
                {
                    //TODO 更新内存
                    CurName = name;
                    updateTrace(point);
                    updateBuffer();
                    copyBuffer();
                }
            }
        }

    }
    public void updateBuffer()
    {
        int x = 0,y = 0;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setFilterBitmap(true);
        ArrayList bufferIndex = new ArrayList();
        switch (moveDerection)
        {
            case 0:
                bufferIndex.add(0);
                bufferIndex.add(1);
                bufferIndex.add(2);
                bufferIndex.add(3);
                bufferIndex.add(6);
                break;
            case 1:
                bufferIndex.add(0);
                bufferIndex.add(1);
                bufferIndex.add(2);
                break;
            case 2:
                bufferIndex.add(0);
                bufferIndex.add(1);
                bufferIndex.add(2);
                bufferIndex.add(5);
                bufferIndex.add(8);
                break;
            case 3:
                bufferIndex.add(0);
                bufferIndex.add(3);
                bufferIndex.add(6);
                break;
            case 5:
                bufferIndex.add(2);
                bufferIndex.add(5);
                bufferIndex.add(8);
                break;
            case 6:
                bufferIndex.add(0);
                bufferIndex.add(3);
                bufferIndex.add(6);
                bufferIndex.add(7);
                bufferIndex.add(8);
                break;
            case 7:
                bufferIndex.add(6);
                bufferIndex.add(7);
                bufferIndex.add(8);
                break;
            case 8:
                bufferIndex.add(2);
                bufferIndex.add(5);
                bufferIndex.add(6);
                bufferIndex.add(7);
                bufferIndex.add(8);
                break;
        }
        for (int j=0;j<bufferIndex.size();j++) {
            int i = (int) bufferIndex.get(j);
            String fileName = null;
            switch (bufferstate)
            {
                case 2:
                    fileName = Environment.getExternalStorageDirectory()+"/AutoGround/"+taskname+"/"+mapbufferFile[i]+".png";
                    break;
                case 1:
                    fileName = Environment.getExternalStorageDirectory()+"/AutoGround/"+taskname+"/"+mapbufferFileBack[i]+".png";
            }

            File fs = new File(fileName);
            if (fs.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(fileName);
                Rect mSrcRect = new Rect(0, 0, width, height);//第一个Rect 代表要绘制的bitmap 区域
                switch (i) {
                    case 0:
                        x = 0;
                        y = 0;
                        break;
                    case 1:
                        x = width;
                        y = 0;
                        break;
                    case 2:
                        x = width * 2;
                        y = 0;
                        break;
                    case 3:
                        x = 0;
                        y = height;
                        break;
                    case 4:
                        x = width;
                        y = height;
                        break;
                    case 5:
                        x = width * 2;
                        y = height;
                        break;
                    case 6:
                        x = 0;
                        y = height * 2;
                        break;
                    case 7:
                        x = width;
                        y = height * 2;
                        break;
                    case 8:
                        x = width * 2;
                        y = height * 2;
                        break;

                }
                Rect mDestRect = new Rect(x, y, width + x, height + y);//第二个 Rect 代表的是要将bitmap 绘制在屏幕的什么地方
                switch (bufferstate) {
                    case 1:
                        canvasBack.drawBitmap(bitmap, mSrcRect, mDestRect, paint);
                        break;
                    case 2:
                        canvas.drawBitmap(bitmap, mSrcRect, mDestRect, paint);
                        break;
                }
            }
        }

    }
    public void copyBuffer()    {
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

                break;
            case 1:
                mSrcRect = new Rect(0, 0, width*3, height*2);
                mDestRect = new Rect(0, height,width*3, height*3);

                break;
            case 2:
                mSrcRect = new Rect(width, 0, width*3, height*2);
                mDestRect = new Rect(0, height,width*2, height*3);

                break;
            case 3:
                mSrcRect = new Rect(0, 0, width*2, height*3);
                mDestRect = new Rect(width, 0,width*3, height*3);

                break;
            case 5:
                mSrcRect = new Rect(width, 0, width*3, height*3);
                mDestRect = new Rect(0, 0,width*2, height*3);

                break;
            case 6:
                mSrcRect = new Rect(0, height, width*2, height*3);
                mDestRect = new Rect(width, 0,width*3, height*2);

                break;
            case 7:
                mSrcRect = new Rect(0, height, width*3, height*3);
                mDestRect = new Rect(0, 0,width*3, height*2);

                break;
            case 8:
                mSrcRect = new Rect(width, height, width*3, height*3);
                mDestRect = new Rect(0, 0,width*2, height*2);

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

}

