package com.example.autoground;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class MysurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable, View.OnTouchListener {
    private Point start = new Point(0,0);
    private Point moveOrigin;
    private Point Moved = new Point(0,0);
    private boolean isScaling = false;
    private float xMid=0.0f;
    private float yMid=0.0f;
    private double beginDistance;
    private float scale = 1.0f;
    private float zoomscale = 1.0f;

    private boolean ismoving=false;
    public MysurfaceView(Context context, AttributeSet attrs){
        super(context,attrs);
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
        for(int i=-startX%20-20-startX;i<widthscale-startX;i+=20)
            canvas.drawLine(i,-startY,i,heightscale-startY,paint);
        for (int i=-startY%20-20-startY;i<heightscale-startY;i+=20)
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
                        Moved.x -= (int) (motionEvent.getX() - moveOrigin.x) * scale;
                        Moved.y -= (int) (motionEvent.getY() - moveOrigin.y) * scale;
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
}
