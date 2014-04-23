package com.neusoft.neespad.view;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.ImageView;

/**
 * �Զ���WebView
 * 
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
			canvas.clipPath(mPath);
			// ���Ŵ���ͼ
			canvas.translate(RADIUS - mCurrentX * FACTOR, RADIUS - mCurrentY* FACTOR);
			
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

}
