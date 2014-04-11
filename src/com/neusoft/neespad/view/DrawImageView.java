package com.neusoft.neespad.view;
import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class DrawImageView extends ImageView {

	private static int scr_width;//��Ļ�Ŀ��
	private static int scr_height;//��Ļ�ĸ߶�
	private static int begin_left=200;//ȡ�������Ե
	private static int begin_top=100;//ȡ�����ұ�Ե
	private static int end_right;
	private static int end_bottom;
	private static int paint_color=Color.RED;//ȡ������ɫ
	private static float paint_width=4.5f;
	private static int other_left=0;
	public DrawImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		DisplayMetrics dm=getResources().getDisplayMetrics();
		scr_width=dm.widthPixels;
		scr_height=dm.heightPixels;
	}

	Paint paint=new Paint();
	{
		paint.setAntiAlias(true);
		paint.setColor(paint_color);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(paint_width);//�����߿�
		paint.setAlpha(100);
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//ע��һ��Ҫ left<scr_width-left,top<scr_height-top
		end_right=scr_width-begin_left-other_left;
		end_bottom=scr_height-begin_top;
		canvas.drawRect(new Rect(begin_left, begin_top, end_right, end_bottom), paint);
	}
	/**
	  * ����ȡ��������Ե��ʼλ��
	  * setBeginLeft(������һ�仰�����������������)
	  * @Title: setBeginLeft
	  * @Description: TODO
	  * @param @param left    �趨�ļ�
	  * @return void    ��������
	  * @throws
	  */
	public void setBeginLeft(int left){
		begin_left=left;
	}
	/**
	  * ����ȡ������ϱ�Ե��ʼλ��
	  * setBeginTop(������һ�仰�����������������)
	  * @Title: setBeginTop
	  * @Description: TODO
	  * @param @param top    �趨�ļ�
	  * @return void    ��������
	  * @throws
	  */
	public void setBeginTop(int top){
		begin_top=top;
	}
	/**
	  * ����ȡ����ı߿���ɫ
	  * setPaintColor(������һ�仰�����������������)
	  * @Title: setPaintColor
	  * @Description: TODO
	  * @param @param color    �趨�ļ�
	  * @return void    ��������
	  * @throws
	  */
	public void setPaintColor(int color){
		paint_color=color;
	}
	
	/**
	  * ���û��ʵĿ��
	  * setPaintWidth
	  * @Title: setPaintWidth
	  * @Description: TODO
	  * @param @param width    �趨�ļ�
	  * @return void    	         ��������
	  * @throws
	  */
	public void setPaintWidth(int width){
		paint_width=width;
	}
	
	public void setOtherLeft(int left){
		other_left=left;
	}
	public int getBeginLeft(){
		return begin_left;
	}
	
	public int getBeginTop(){
		return begin_top;
	}
	
	public int getEndRight(){
		return end_right;
	}
	
	public int getEndBottom(){
		return end_bottom;
	}
	
	public int getScrWidth(){
		return scr_width;
	}
	
	public int getScrHeight(){
		return scr_height;
	}
}
