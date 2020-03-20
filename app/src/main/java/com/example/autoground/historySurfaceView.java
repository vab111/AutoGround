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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
    private RecordInfor recordInfor;
    public boolean refresh = true;

    public historySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.getHolder().addCallback(this);
        this.setOnTouchListener(this);

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
        if (recordInfor.isB)
            pointB = recordInfor.pointB;

        Canvas canvas;
        long t = 0 ;

        while(refresh){
            canvas = this.getHolder().lockCanvas();
            if (canvas == null)
                return;
            canvas.drawColor(Color.WHITE);
            canvas.translate(Moved.x,Moved.y);

            drawBg(canvas,width,height);
            loadBitmap(canvas);

            if (recordInfor.isB) {
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
        Point ap = transform(pointA);
        Rect mDestRect = new Rect(ap.x-15, ap.y-15, ap.x+65, ap.y+65);

        Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.adian);		// 设置canvas画布背景为白色	// 定义矩阵对象
        bmp = rotaingImageView((int) carDerection-90, bmp);
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
        Point ap = transform(pointB);
        Rect mDestRect = new Rect(ap.x-15, ap.y-15, ap.x+65, ap.y+65);

        Bitmap bmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.bdian);		// 设置canvas画布背景为白色	// 定义矩阵对象
        bmp = rotaingImageView((int) carDerection-90, bmp);
        Matrix matrix = new Matrix();		// 缩放原图
        matrix.postScale(0.4f, 0.4f);		//bmp.getWidth(), bmp.getHeight()分别表示缩放后的位图宽高
        Bitmap dstbmp = createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),				matrix, true);

        canvas.drawBitmap(dstbmp,mSrcRect,mDestRect,paint);
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
    public void loadBitmap(Canvas canvas)
    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setFilterBitmap(true);
        File appDir = new File(Environment.getExternalStorageDirectory() + "/AutoGround/" + taskname);
//        int centx = -Moved.x+width/2;
//        int centy = Moved.y-height/2;
//        List<String> sdf = new ArrayList();
//        sdf.add(caculateFileName(new Point(centx,centy)));
//        if (centx<0)
//        {
//            if (Math.abs(centx%width)<width/2)
//            {
//                sdf.add(caculateFileName(new Point(centx+width,centy)));
//                if (centy<0)
//                {
//                    if (Math.abs(centy%height)<height/2)
//                    {
//                        sdf.add(caculateFileName(new Point(centx,centy+height)));
//                        sdf.add(caculateFileName(new Point(centx+width,centy+height)));
//                    }
//                    else
//                    {
//                        sdf.add(caculateFileName(new Point(centx,centy-height)));
//                        sdf.add(caculateFileName(new Point(centx+width,centy-height)));
//                    }
//                }
//                else
//                {
//                    if (Math.abs(centy%height)<height/2)
//                    {
//                        sdf.add(caculateFileName(new Point(centx,centy-height)));
//                        sdf.add(caculateFileName(new Point(centx+width,centy-height)));
//                    }
//                    else
//                    {
//                        sdf.add(caculateFileName(new Point(centx,centy+height)));
//                        sdf.add(caculateFileName(new Point(centx+width,centy+height)));
//                    }
//                }
//
//            }
//            else
//            {
//                sdf.add(caculateFileName(new Point(centx-width,centy)));
//                if (centy<0)
//                {
//                    if (Math.abs(centy%height)<height/2)
//                    {
//                        sdf.add(caculateFileName(new Point(centx,centy+height)));
//                        sdf.add(caculateFileName(new Point(centx-width,centy+height)));
//                    }
//                    else
//                    {
//                        sdf.add(caculateFileName(new Point(centx,centy-height)));
//                        sdf.add(caculateFileName(new Point(centx-width,centy-height)));
//                    }
//                }
//                else
//                {
//                    if (Math.abs(centy%height)<height/2)
//                    {
//                        sdf.add(caculateFileName(new Point(centx,centy-height)));
//                        sdf.add(caculateFileName(new Point(centx-width,centy-height)));
//                    }
//                    else
//                    {
//                        sdf.add(caculateFileName(new Point(centx,centy+height)));
//                        sdf.add(caculateFileName(new Point(centx-width,centy+height)));
//                    }
//                }
//            }
//        }
//        else
//        {
//            if (Math.abs(centx%width)<width/2)
//            {
//                sdf.add(caculateFileName(new Point(centx-width,centy)));
//                if (centy<0)
//                {
//                    if (Math.abs(centy%height)<height/2)
//                    {
//                        sdf.add(caculateFileName(new Point(centx,centy+height)));
//                        sdf.add(caculateFileName(new Point(centx-width,centy+height)));
//                    }
//                    else
//                    {
//                        sdf.add(caculateFileName(new Point(centx,centy-height)));
//                        sdf.add(caculateFileName(new Point(centx-width,centy-height)));
//                    }
//                }
//                else
//                {
//                    if (Math.abs(centy%height)<height/2)
//                    {
//                        sdf.add(caculateFileName(new Point(centx,centy-height)));
//                        sdf.add(caculateFileName(new Point(centx-width,centy-height)));
//                    }
//                    else
//                    {
//                        sdf.add(caculateFileName(new Point(centx,centy+height)));
//                        sdf.add(caculateFileName(new Point(centx-width,centy+height)));
//                    }
//                }
//            }
//            else
//            {
//                sdf.add(caculateFileName(new Point(centx+width,centy)));
//                if (centy<0)
//                {
//                    if (Math.abs(centy%height)<height/2)
//                    {
//                        sdf.add(caculateFileName(new Point(centx,centy+height)));
//                        sdf.add(caculateFileName(new Point(centx+width,centy+height)));
//                    }
//                    else
//                    {
//                        sdf.add(caculateFileName(new Point(centx,centy-height)));
//                        sdf.add(caculateFileName(new Point(centx+width,centy-height)));
//                    }
//                }
//                else
//                {
//                    if (Math.abs(centy%height)<height/2)
//                    {
//                        sdf.add(caculateFileName(new Point(centx,centy-height)));
//                        sdf.add(caculateFileName(new Point(centx+width,centy-height)));
//                    }
//                    else
//                    {
//                        sdf.add(caculateFileName(new Point(centx,centy+height)));
//                        sdf.add(caculateFileName(new Point(centx+width,centy+height)));
//                    }
//                }
//            }
//
//        }

        if (appDir.exists()) {
            File[] files = appDir.listFiles();
            int x;
            int y;
            Rect mSrcRect ;//第一个Rect 代表要绘制的bitmap 区域
            Rect mDestRect ;//第二个 Rect 代表的是要将bitmap 绘制在屏幕的什么地方
            boolean flag = true;
            for (int i = 0; i < files.length; i++) {
                // 删除子文件

                if (files[i].isFile()) {
                    //TODO 获取bitmap加载到canvas
                    String item = files[i].getName();
                    String name = item.substring(0, item.length()-4);
                    String[] strArr = name.split("-");
                    int xiangxian = Integer.parseInt(strArr[0]);
                    int hor = Integer.parseInt(strArr[1]);
                    int ver = Integer.parseInt(strArr[2]);
                    Bitmap bitmap = BitmapFactory.decodeFile(files[i].getAbsolutePath());
                    switch (xiangxian)
                    {
                        case 1:
                            x = hor*width;
                            y = -(ver+1)*height;
                            mSrcRect = new Rect(0, 0, width, height);//第一个Rect 代表要绘制的bitmap 区域
                            mDestRect = new Rect(x, y, width+x, height+y);//第二个 Rect 代表的是要将bitmap 绘制在屏幕的什么地方
                            canvas.drawBitmap(bitmap,mSrcRect,mDestRect,paint);
                            break;
                        case 2:
                            x = -(hor+1)*width;
                            y = -(ver+1)*height;
                            mSrcRect = new Rect(0, 0, width, height);//第一个Rect 代表要绘制的bitmap 区域
                            mDestRect = new Rect(x, y, width+x, height+y);//第二个 Rect 代表的是要将bitmap 绘制在屏幕的什么地方
                            canvas.drawBitmap(bitmap,mSrcRect,mDestRect,paint);
                            break;
                        case 3:
                            x = -(hor+1)*width;
                            y = ver*height;
                            mSrcRect = new Rect(0, 0, width, height);//第一个Rect 代表要绘制的bitmap 区域
                            mDestRect = new Rect(x, y, width+x, height+y);//第二个 Rect 代表的是要将bitmap 绘制在屏幕的什么地方
                            canvas.drawBitmap(bitmap,mSrcRect,mDestRect,paint);
                            break;
                        case 4:
                            x = hor*width;
                            y = ver*height;
                            mSrcRect = new Rect(0, 0, width, height);//第一个Rect 代表要绘制的bitmap 区域
                            mDestRect = new Rect(x, y, width+x, height+y);//第二个 Rect 代表的是要将bitmap 绘制在屏幕的什么地方
                            canvas.drawBitmap(bitmap,mSrcRect,mDestRect,paint);
                            break;
                    }

                }

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
        hor = Math.abs(point.x/width);
        ver = Math.abs(point.y/height);
        String name = String.format("%d-%d-%d", xiangxian,hor,ver);
        Log.e("中心BITMAP", name);
        return name;
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
}

