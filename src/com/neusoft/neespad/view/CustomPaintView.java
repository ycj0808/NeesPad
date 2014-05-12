package com.neusoft.neespad.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.drawdemo.utils.PaintConstants.PEN_TYPE;
import com.example.drawdemo.view.PaintView;
import com.neusoft.neespad.common.MyApplication.PaintNode;

public class CustomPaintView extends PaintView {

	private Bitmap bitmap;
	public CustomPaintView(Context context) {
		super(context,null);
	}
	
	public CustomPaintView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomPaintView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs,defStyle);
	}
	@Override
	public void creatNewPen() {
		super.creatNewPen();
	}
	
	public void preview(ArrayList<PaintNode> paintPath){
		clearAll();
		PreviewThread previewThread=new  PreviewThread(this, paintPath);
		Thread thread=new Thread(previewThread);
		thread.start();
	}
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			((View)msg.obj).invalidate();
 		}
	};
	/**
	 * 清空屏幕
	 */
	public void clearScreen() {
		recycleMBitmap();
		recycleOrgBitmap();
		getPaintPadUndoStack().clearAll();
		creatCanvasBitmap(mBitmapWidth, mBitmapHeight);
	}
	class PreviewThread extends Thread{
		private long time;
		private ArrayList<PaintNode> paintPath;
		private View view;
		public PreviewThread(View view,ArrayList<PaintNode> path){
			this.paintPath=path;
			this.view=view;
		}
		public void run() {
			time=0;
			for(int i=0;i<paintPath.size();i++){
				PaintNode node=paintPath.get(i);
				float x=node.x;
				float y=node.y;
				if(i<paintPath.size()-1){
					time=paintPath.get(i+1).time-node.time;
				}
				switch (node.action){
				case MotionEvent.ACTION_DOWN:
					mCanvas.setBitmap(getBitmap());
					creatNewPen();
					getCurrentPaint().touchDown(x, y);
					getPaintPadUndoStack().clearRedo();
					getPaintViewCallBack().onTouchDown();
					break;
				case MotionEvent.ACTION_MOVE:
					getCurrentPaint().touchMove(x, y);
					if (getPaintType() == PEN_TYPE.ERASER) {
						getCurrentPaint().draw(mCanvas);
					}
					break;
				case MotionEvent.ACTION_UP:
					if (getCurrentPaint().hasDraw()) {
						getPaintPadUndoStack().push(getCurrentPaint());
						if (getPaintViewCallBack() != null) {
							// 控制undo\redo的现实
							getPaintViewCallBack().onHasDraw();
						}
					}
					getCurrentPaint().touchUp(x, y);
					// 只有在up的时候才在bitmap上画图，最终显示在view上
					getCurrentPaint().draw(mCanvas);
					isTouchUp = true;
					break;	
				}
				try{
					Message msg1=new Message();
					msg1.obj=view;
					handler.sendMessage(msg1);
					Thread.sleep(time);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
	}
}
