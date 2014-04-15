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
 * ��һ�����ο�
 * 
 * @ClassName: SVDraw
 * @Description: TODO
 * @author yangchj
 * @date 2014-4-15 ����10:46:32
 */
public class SVDraw extends SurfaceView implements SurfaceHolder.Callback {

	
	private SurfaceHolder surfaceHolder;
	private int mWidth; // ���
	private int mHeight;// �߶�
	private static int paint_color=Color.RED;//ȡ������ɫ
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
        p.setStrokeWidth(paint_width);//�����߿�
        canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), p);
        holder.unlockCanvasAndPost(canvas);
    }
	
	/**
	  * ����ȡ������ɫ
	  * setPaintColor(������һ�仰�����������������)
	  * @Title: setPaintColor
	  * @Description: TODO
	  * @param @param color    �趨�ļ�
	  * @return void    ��������
	  * @throws
	  */
	public static void setPaintColor(int color){
		paint_color=color;
	}
	/**
	  * ���û��ʵĴ�ϸ
	  * setPaintWidth(������һ�仰�����������������)
	  * @Title: setPaintWidth
	  * @Description: TODO
	  * @param @param width    �趨�ļ�
	  * @return void    ��������
	  * @throws
	  */
	public static void setPaintWidth(float width){
		paint_width=width;
	}
}













