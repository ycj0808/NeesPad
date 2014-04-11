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

	private static int scr_width;//屏幕的宽度
	private static int scr_height;//屏幕的高度
	private static int begin_left=200;//取景的左边缘
	private static int begin_top=100;//取景的右边缘
	private static int end_right;
	private static int end_bottom;
	private static int paint_color=Color.RED;//取景框颜色
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
		paint.setStrokeWidth(paint_width);//设置线宽
		paint.setAlpha(100);
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//注意一定要 left<scr_width-left,top<scr_height-top
		end_right=scr_width-begin_left-other_left;
		end_bottom=scr_height-begin_top;
		canvas.drawRect(new Rect(begin_left, begin_top, end_right, end_bottom), paint);
	}
	/**
	  * 设置取景框的左边缘起始位置
	  * setBeginLeft(这里用一句话描述这个方法的作用)
	  * @Title: setBeginLeft
	  * @Description: TODO
	  * @param @param left    设定文件
	  * @return void    返回类型
	  * @throws
	  */
	public void setBeginLeft(int left){
		begin_left=left;
	}
	/**
	  * 设置取景框的上边缘起始位置
	  * setBeginTop(这里用一句话描述这个方法的作用)
	  * @Title: setBeginTop
	  * @Description: TODO
	  * @param @param top    设定文件
	  * @return void    返回类型
	  * @throws
	  */
	public void setBeginTop(int top){
		begin_top=top;
	}
	/**
	  * 设置取景框的边框颜色
	  * setPaintColor(这里用一句话描述这个方法的作用)
	  * @Title: setPaintColor
	  * @Description: TODO
	  * @param @param color    设定文件
	  * @return void    返回类型
	  * @throws
	  */
	public void setPaintColor(int color){
		paint_color=color;
	}
	
	/**
	  * 设置画笔的宽度
	  * setPaintWidth
	  * @Title: setPaintWidth
	  * @Description: TODO
	  * @param @param width    设定文件
	  * @return void    	         返回类型
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
