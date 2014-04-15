package com.neusoft.neespad.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 画一个矩形框
 * 
 * @ClassName: SVDraw
 * @Description: TODO
 * @author yangchj
 * @date 2014-4-15 上午10:46:32
 */
public class SVDraw extends SurfaceView implements SurfaceHolder.Callback {

	
	private SurfaceHolder surfaceHolder;
	private int mWidth; // 宽度
	private int mHeight;// 高度
	private static int paint_color=Color.RED;//取景框颜色
	private static float paint_width=6f;
	Canvas canvas;
	public SVDraw(Context context, AttributeSet attrs) {
		super(context, attrs);
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
		setZOrderOnTop(true);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		draw(holder);
	}

	/**
	 * 
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		mWidth = width;
		mHeight = height;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//clearDraw(holder);
	}
	
	public void clearDraw(SurfaceHolder holder)
    {
        canvas =holder.lockCanvas();
        canvas.drawColor(Color.RED);
        holder.unlockCanvasAndPost(canvas);
    }
	
	public void draw(SurfaceHolder holder)
    {
        canvas = holder.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT);
        Paint p = new Paint();
        p.setAntiAlias(true);   
        p.setColor(paint_color);  
        p.setStyle(Style.STROKE);
        p.setStrokeWidth(paint_width);//设置线宽
        canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), p);
        holder.unlockCanvasAndPost(canvas);
    }
	
	/**
	  * 设置取景框颜色
	  * setPaintColor(这里用一句话描述这个方法的作用)
	  * @Title: setPaintColor
	  * @Description: TODO
	  * @param @param color    设定文件
	  * @return void    返回类型
	  * @throws
	  */
	public static void setPaintColor(int color){
		paint_color=color;
	}
	/**
	  * 设置画笔的粗细
	  * setPaintWidth(这里用一句话描述这个方法的作用)
	  * @Title: setPaintWidth
	  * @Description: TODO
	  * @param @param width    设定文件
	  * @return void    返回类型
	  * @throws
	  */
	public static void setPaintWidth(float width){
		paint_width=width;
	}
}













