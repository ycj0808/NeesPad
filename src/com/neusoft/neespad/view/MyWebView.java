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
 * 自定义WebView
 * @ClassName: MyWebView
 * @Description: TODO
 * @author yangchj
 * @date 2014-4-22 上午9:40:00
 */
public class MyWebView extends WebView{

	protected Context mContext;
	private Path mPath = new Path();
	private Matrix matrix = new Matrix();
	private Bitmap mBitmap = null;
	private ScrollChangedCallback scc;
	// 放大镜的半径
	private static final int RADIUS = 80;
	// 放大倍数
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
	 * 设置Bitmap setBitmap
	 * 
	 * @Title: setBitmap
	 * @Description: TODO
	 * @param @param bitmap 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void setBitmap(Bitmap bitmap) {
		mBitmap = bitmap;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mBitmap != null) {
			// 底图
			canvas.drawBitmap(mBitmap, 0, 0, null);

			// 剪切
			canvas.translate(mCurrentX - RADIUS * 2, mCurrentY - RADIUS * 2);// 将整个canvas水平移动x，垂直移动y距离
			
			Log.i("TAG", "这是X坐标1"+(mCurrentX - RADIUS * 2));
			
			Log.i("TAG", "这是Y坐标1"+(mCurrentY - RADIUS * 2));
			
			canvas.clipPath(mPath);
			// 画放大后的图
			canvas.translate(RADIUS - mCurrentX * FACTOR, RADIUS - mCurrentY* FACTOR);
			
			Log.i("TAG", "这是X坐标2"+(RADIUS - mCurrentX * FACTOR));
			
			Log.i("TAG", "这是Y坐标2"+(RADIUS - mCurrentY* FACTOR));
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
	 * 滑动进度条
	 * @author Administrator
	 */
	public interface ScrollChangedCallback{
		public void scrollChanged(int curY);
	}
	/**
	 * 注册回调接口 
	 * @param sc
	 */
	public void registerScrollChangedCallback(ScrollChangedCallback sc){
		this.scc=sc;
	}
}
