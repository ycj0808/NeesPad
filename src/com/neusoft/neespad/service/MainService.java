package com.neusoft.neespad.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import com.neusoft.neespad.common.FileUtil;
import com.neusoft.neespad.common.JsonUtil;
import com.neusoft.neespad.common.MyApplication;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service {

	public static final String TAG = "MainService";
	public static Boolean mainThreadFlag = true;// 主线程标志位
	public static Boolean ioThreadFlag = true;// IO线程标志位
	ServerSocket serverSocket = null;
	private final int SERVER_PORT = 10000;
	private MyApplication app;
	public Socket client;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/**
	 * 创建新的工程目录 createNewDirectory
	 * 
	 * @Title: createNewDirectory
	 * @Description: TODO
	 * @param @param name 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void createNewDirectory(String name) {
		FileUtil.newFolder(name);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		createNewDirectory("neesPad");
		new Thread() {
			public void run() {
				doListen();
			}
		}.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mainThreadFlag = false;
		ioThreadFlag = false;
	}

	/**
	 * 监听事件 doListen
	 * 
	 * @Title: doListen
	 * @Description: TODO
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void doListen() {
		Log.d(TAG, Thread.currentThread().getName() + "---->"
				+ "doListen() START");
		serverSocket = null;
		try {
			Log.d(TAG, Thread.currentThread().getName() + "---->"
					+ "doListen() new ServerSocket");
			// 建立服务器端socket
			serverSocket = new ServerSocket(SERVER_PORT);
			boolean mainThreadFlag = true;
			while (mainThreadFlag) {
				Log.d(TAG, Thread.currentThread().getName() + "---->"
						+ "doListen() START");
				// 监听直到有客户端请求
				client = serverSocket.accept();
				// 先开启一个活动 线程
				new Thread(new ThreadStartActivty(MainService.this, client))
						.start();
				// 发送消息 线程
				new Thread(new ThreadWriterSocket(client)).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送广播 sendBroadCast
	 * 
	 * @Title: sendBroadCast
	 * @Description: TODO
	 * @param @param name 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void sendBroadCast(String name) {
		sendBroadcast(new Intent(name));
	}

	/**
	 * 校验文件的MD5 checkFile(这里用一句话描述这个方法的作用)
	 * 
	 * @Title: checkFile
	 * @Description: TODO
	 * @param @param MD5Str
	 * @param @param filePaths
	 * @param @return
	 * @param @throws Exception 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	public boolean checkFile(JSONArray MD5Str, JSONArray filePaths)
			throws Exception {
		boolean flag = true;
		for (int i = 0; i < filePaths.length(); i++) {
			String filename = filePaths.get(i).toString();
			String MD5 = MD5Str.get(i).toString();
			if (!FileUtil.getMd5Value(filename).equals(MD5)) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	/**
	 * 活动线程
	 * 
	 * @ClassName: ThreadStartActivty
	 * @Description: TODO
	 * @author yangchj
	 * @date 2014-3-18 下午3:00:43
	 */
	public class ThreadStartActivty implements Runnable {

		private Socket client;
		private Context context;
		BufferedInputStream in;
		BufferedOutputStream out;
		private Intent bootStart;

		public ThreadStartActivty(Context context, Socket client) {
			// 客户端
			this.client = client;
			this.context = context;
			try {
				// 输出流
				out = new BufferedOutputStream(client.getOutputStream());
				// 输入流
				in = new BufferedInputStream(client.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			Log.d(TAG, Thread.currentThread().getName() + "---->"
					+ "a client has connected to server!");
			try {
				ioThreadFlag = true;
				while (ioThreadFlag) {
					try {
						if (!client.isConnected()) {
							break;
						}
						// 接收PC发来的数据
						Log.v(TAG, Thread.currentThread().getName() + "---->"
								+ "will read......");
						// 读操作命令
						String cmdStr = readCMDFromSocket(in);
						Log.v(TAG, Thread.currentThread().getName()
								+ "cmdStr---->" + cmdStr);
						// 根据命令处理活动
						if (!"".equals(cmdStr)) {
							//调出拍摄画面
							if ("1".equals(cmdStr)) {
								sendBroadCast("nees.takePhoto_start");
							}
							//拍照的动作
							if ("2".equals(cmdStr)) {
								sendBroadCast("nees.takePhoto_processing");
							}
							//签名
							if ("3".equals(cmdStr)) {
								sendBroadCast("nees.sign");
							}
							//签名完成
							if ("4".equals(cmdStr)) {
								sendBroadCast("nees.takePhotoCompleted");
							}
							//查看协议
							if("5".equals(cmdStr)){
								sendBroadCast("nees.look_protocal");
							}
							//拍摄照片的大取景框
							if("6".equals(cmdStr)){
								sendBroadCast("nees.big_surface");
							}
							//调用照身份证,小取景框
							if("7".equals(cmdStr)){
								sendBroadCast("nees.small_surface");
							}
							//大取景框时的拍摄动作
							if("8".contains(cmdStr)){
								sendBroadCast("nees.take_big_photo_processing");
							}
							//小取景框时的拍摄动作
							if("9".contains(cmdStr)){
								sendBroadCast("nees.take_small_photo_processing");
							}
							//返回主页
							if("000".equals(cmdStr)){
								sendBroadCast("nees.back_home");
							}
						}

					} catch (Exception e) {
						Log.e(TAG, Thread.currentThread().getName() + "---->"
								+ "read receive error1" + e.getMessage());
						out.write("err".getBytes());
						out.flush();
						client.close();
						break;
					}
				}
				in.close();
			} catch (Exception e) {
				Log.e(TAG, Thread.currentThread().getName() + "---->"
						+ "read receive error2" + e.getMessage());
				e.printStackTrace(); 
			} finally {
				try {
					if (client != null) {
						Log.v(TAG, Thread.currentThread().getName() + "---->"
								+ "client.close()");
						client.close();  
					}
				} catch (IOException e2) {
					Log.e(TAG, Thread.currentThread().getName() + "---->"
							+ "read receive error3" + e2.getMessage());
				}
			}
		}
	}

	/**
	 * 读取操作命令 readCMDFromSocket
	 * 
	 * @Title: readCMDFromSocket
	 * @Description: TODO
	 * @param @param in
	 * @param @return
	 * @param @throws IOException 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public String readCMDFromSocket(InputStream in) throws IOException {
		int MAX_BUFFER_BYTES = 4096;
		String msg = "";
		byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];
		int numReadedBytes = in.read(tempbuffer, 0, tempbuffer.length);
		if (numReadedBytes != -1) {
			msg = new String(tempbuffer, 0, numReadedBytes, "utf-8");
		}
		tempbuffer = null;
		return msg;
	}

	/**
	 * 发送线程
	 * 
	 * @ClassName: ThreadWriterSocket
	 * @Description: TODO
	 * @author yangchj
	 * @date 2014-3-18 下午3:15:22
	 */
	public class ThreadWriterSocket implements Runnable {
		private Socket client;
		BufferedOutputStream out;// 输出流

		ThreadWriterSocket(Socket client) {
			this.client = client;
			try {
				out = new BufferedOutputStream(client.getOutputStream());
			} catch (IOException e) {
				Log.e(TAG, Thread.currentThread().getName() + "---->"
						+ "read receive error3" + e.getMessage());
				e.printStackTrace();
			}
		}

		public boolean sendflag(Map<String, Object> map) {
			boolean flag = false;
			if (!map.isEmpty()) {
				if (map.get("flag").equals("revices")) {
					flag = true;
				}
			}
			return flag;
		}

		@Override
		public void run() {
			Log.d(TAG, Thread.currentThread().getName() + "---->"
					+ "a client has connected to server!");
			try {
				ioThreadFlag = true;
				while (ioThreadFlag) {
					try {
						if (!client.isConnected()) {
							break;
						} 
						Thread.sleep(500);//休眠半秒钟,防止资源未准备好,而使socket中端,而无法连续发送图片资源
						app = (MyApplication) getApplication();
						Map<String, Object> map = app.getMap();
						if (!map.isEmpty() && !sendflag(map)) {
							String jsonStr = JsonUtil.getResponse(map);
							out.write(jsonStr.getBytes());
							out.flush();
							map.clear();
						}
					} catch (Exception e) {
						Log.e(TAG, Thread.currentThread().getName() + "---->"
								+ "read send  error1" + e.getMessage());
						client.close();
						break;
					}
				}
				out.close();
			} catch (Exception e) {
				Log.e(TAG, Thread.currentThread().getName() + "---->"
						+ "read send error2" + e.getMessage());
				e.printStackTrace();
			} finally {
				try {
					if (client != null) {
						Log.v(TAG, Thread.currentThread().getName() + "---->"
								+ "client.close()");
						client.close();
					}
				} catch (Exception e2) {
					Log.e(TAG, Thread.currentThread().getName() + "---->"
							+ "read send error3" + e2.getMessage());
					e2.printStackTrace();
				}
			}
		}
	}

}
