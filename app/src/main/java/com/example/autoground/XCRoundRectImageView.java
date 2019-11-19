package com.example.autoground;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.ImageView;



@SuppressLint("AppCompatCustomView")
public class XCRoundRectImageView extends ImageView{



    public XCRoundRectImageView(Context context) {

        this(context,null);

    }



    public XCRoundRectImageView(Context context, AttributeSet attrs) {

        this(context, attrs,0);

    }



    public XCRoundRectImageView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

    }

    /**

     * 绘制圆形图片

     * @author caizhiming

     */

    @Override

    protected void onDraw(Canvas canvas) {

        Path mPath = new Path();

        int width = getMeasuredWidth();

        int height = getMeasuredHeight();

        mPath.addCircle(width/2, height/2, height/2, Path.Direction.CW);

        canvas.clipPath(mPath);//将画布裁剪成圆形

        super.onDraw(canvas);



        //画上圆形边框

        Paint paint=new Paint();

        paint.setStyle(Paint.Style.STROKE);

        paint.setStrokeWidth(1);

        paint.setColor(Color.WHITE);

        canvas.drawPath(mPath,paint);

    }



}
