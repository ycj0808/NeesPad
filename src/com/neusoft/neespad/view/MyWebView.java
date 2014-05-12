package com.neusoft.neespad.view;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;

/**
 * �Զ���WebView
 * @ClassName: MyWebView
 * @Description: TODO
 * @author yangchj
 * @date 2014-4-22 ����9:40:00
 */
public class MyWebView extends WebView{

	protected Context mContext;
	private Path mPath = new Path();
	private Matrix matrix = new Matrix();
	private Bitmap mBitmap = null;
	private ScrollChangedCallback scc;
	// �Ŵ󾵵İ뾶
	private static final int RADIUS = 80;
	// �Ŵ���
	private static final int FACTOR = 2;
	private int mCurrentX, mCurrentY;
	public MyWebView(Context context) {
		super(context);
		mContext = context;
		mPath.addCircle(RADIUS, RADIUS, RADIUS, Direction.CW);
		matrix.setScale(FACTOR, FACTOR);
	}

	
	public MyWebView(Context context,AttributeSet attr) {
		super(context,attr,0);
		mContext = context;
		mPath.addCircle(RADIUS, RADIUS, RADIUS, Direction.CW);
		matrix.setScale(FACTOR, FACTOR);
	}
	/**
	 * ����Bitmap setBitmap
	 * 
	 * @Title: setBitmap
	 * @Description: TODO
	 * @param @param bitmap �趨�ļ�
	 * @return void ��������
	 * @throws
	 */
	public void setBitmap(Bitmap bitmap) {
		mBitmap = bitmap;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mBitmap != null) {
			// ��ͼ
			canvas.drawBitmap(mBitmap, 0, 0, null);

			// ����
			canvas.translate(mCurrentX - RADIUS * 2, mCurrentY - RADIUS * 2);// ������canvasˮƽ�ƶ�x����ֱ�ƶ�y����
			
			Log.i("TAG", "����X����1"+(mCurrentX - RADIUS * 2));
			
			Log.i("TAG", "����Y����1"+(mCurrentY - RADIUS * 2));
			
			canvas.clipPath(mPath);
			// ���Ŵ���ͼ
			canvas.translate(RADIUS - mCurrentX * FACTOR, RADIUS - mCurrentY* FACTOR);
			
			Log.i("TAG", "����X����2"+(RADIUS - mCurrentX * FACTOR));
			
			Log.i("TAG", "����Y����2"+(RADIUS - mCurrentY* FACTOR));
			canvas.drawBitmap(mBitmap, matrix, null);
		}
	}

	public void setPoint(int currWidth, int currheight) {
		mCurrentX = currWidth;
		mCurrentY = currheight;
	}

	public void clearDraw() {
		if (mBitmap != null) {
			mBitmap = null;
		}
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
//		int height=this.getContentHeight();
		int curY=this.getScrollY();
		scc.scrollChanged(curY);
//		if(curY+1000>height){
//			scc.scrollChanged(curY);
//		}
	}

	/**
	 * ����������
	 * @author Administrator
	 */
	public interface ScrollChangedCallback{
		public void scrollChanged(int curY);
	}
	/**
	 * ע��ص��ӿ� 
	 * @param sc
	 */
	public void registerScrollChangedCallback(ScrollChangedCallback sc){
		this.scc=sc;
	}
}
