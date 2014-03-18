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
	public static Boolean mainThreadFlag = true;// ���̱߳�־λ
	public static Boolean ioThreadFlag = true;// IO�̱߳�־λ
	ServerSocket serverSocket = null;
	private final int SERVER_PORT = 10000;
	private MyApplication app;
	public Socket client;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/**
	 * �����µĹ���Ŀ¼ createNewDirectory
	 * 
	 * @Title: createNewDirectory
	 * @Description: TODO
	 * @param @param name �趨�ļ�
	 * @return void ��������
	 * @throws
	 */
	private void createNewDirectory(String name) {
		FileUtil.newFolder(name);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		createNewDirectory("neesPad");

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mainThreadFlag = false;
		ioThreadFlag = false;
	}

	/**
	 * �����¼� doListen
	 * 
	 * @Title: doListen
	 * @Description: TODO
	 * @param �趨�ļ�
	 * @return void ��������
	 * @throws
	 */
	public void doListen() {
		Log.d(TAG, Thread.currentThread().getName() + "---->"
				+ "doListen() START");
		serverSocket = null;
		try {
			Log.d(TAG, Thread.currentThread().getName() + "---->"
					+ "doListen() new ServerSocket");
			serverSocket = new ServerSocket(SERVER_PORT);
			boolean mainThreadFlag = true;
			while (mainThreadFlag) {
				Log.d(TAG, Thread.currentThread().getName() + "---->"
						+ "doListen() START");
				client = serverSocket.accept();
				// �ȿ���һ��� �߳�
				new Thread(new ThreadStartActivty(MainService.this, client)).start();
				// ������Ϣ �߳�
				new Thread(new ThreadWriterSocket(client)).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���͹㲥 sendBroadCast
	 * 
	 * @Title: sendBroadCast
	 * @Description: TODO
	 * @param @param name �趨�ļ�
	 * @return void ��������
	 * @throws
	 */
	public void sendBroadCast(String name) {
		sendBroadcast(new Intent(name));
	}

	/**
	 * У���ļ���MD5 checkFile(������һ�仰�����������������)
	 * 
	 * @Title: checkFile
	 * @Description: TODO
	 * @param @param MD5Str
	 * @param @param filePaths
	 * @param @return
	 * @param @throws Exception �趨�ļ�
	 * @return boolean ��������
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
	  * ��߳�
	  * @ClassName: ThreadStartActivty
	  * @Description: TODO
	  * @author yangchj
	  * @date 2014-3-18 ����3:00:43
	  */
	public class ThreadStartActivty implements Runnable {

		private Socket client;
		private Context context;
		BufferedInputStream in;
		BufferedOutputStream out;
		private Intent bootStart;

		public ThreadStartActivty(Context context, Socket client) {
			this.client = client;
			this.context = context;
			try {
				out = new BufferedOutputStream(client.getOutputStream());
				in = new BufferedInputStream(client.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			Log.d(TAG, Thread.currentThread().getName() + "---->"+ "a client has connected to server!");
			try {
				ioThreadFlag = true;
				while(ioThreadFlag){
					try {
						if (!client.isConnected()) {
							break;
						}
						//����PC����������
						Log.v(TAG, Thread.currentThread().getName() + "---->"+ "will read......");
						//����������
						String cmdStr=readCMDFromSocket(in);
						Log.v(TAG, Thread.currentThread().getName() + "cmdStr---->"+ cmdStr);
						//���������
						if(!"".equals(cmdStr)){
							
						}
						
					} catch (Exception e) {
						Log.e(TAG, Thread.currentThread().getName() + "---->"+ "read receive error1" + e.getMessage());
						out.write("err".getBytes());
						out.flush();
						client.close();
						break;
					}
				}
				in.close();
			} catch (Exception e) {
				Log.e(TAG, Thread.currentThread().getName() + "---->"+ "read receive error2"+e.getMessage());
				e.printStackTrace();
			} finally{
				try {
					if(client!=null){
						Log.v(TAG, Thread.currentThread().getName() + "---->"+ "client.close()");
						client.close();
					}
				} catch (IOException e2) {
					Log.e(TAG, Thread.currentThread().getName() + "---->"+ "read receive error3"+e2.getMessage());
				}
			}
		}
	}
	/**
	  * ��ȡ��������
	  * readCMDFromSocket
	  * @Title: readCMDFromSocket
	  * @Description: TODO
	  * @param @param in
	  * @param @return
	  * @param @throws IOException    �趨�ļ�
	  * @return String    ��������
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
	  * �����߳�
	  * @ClassName: ThreadWriterSocket
	  * @Description: TODO
	  * @author yangchj
	  * @date 2014-3-18 ����3:15:22
	  */
	public class ThreadWriterSocket implements Runnable{
		private Socket client;
		BufferedOutputStream out;
		ThreadWriterSocket(Socket client) {
			this.client=client;
			try {
				out = new BufferedOutputStream(client.getOutputStream());
			} catch (IOException e) {
				Log.e(TAG, Thread.currentThread().getName() + "---->"+ "read receive error3"+e.getMessage());
				e.printStackTrace();
			}
		}
		
		public boolean sendflag(Map<String,Object> map) {
			boolean flag = false;
			if(!map.isEmpty()){
				if(map.get("flag").equals("revices")){
					flag=true;
				}
			}
			return flag;
		}
		@Override
		public void run() {
			Log.d(TAG, Thread.currentThread().getName() + "---->"+ "a client has connected to server!");
			try {
				ioThreadFlag = true;
				while (ioThreadFlag) {
					try{
					if(!client.isConnected()){
						break;
					}
					app=(MyApplication) getApplication();
					Map<String,Object> map=app.getMap();
					if (!map.isEmpty() && !sendflag(map)) {
						String  jsonStr=JsonUtil.getResponse(map);
						out.write(jsonStr.getBytes());
						out.flush();
						map.clear();
					}
					}catch(Exception e){
						Log.e(TAG, Thread.currentThread().getName() + "---->"+ "read send  error1" + e.getMessage());
						client.close();
						break;
					}
				}
				out.close();
			} catch (Exception e) {
				Log.e(TAG, Thread.currentThread().getName() + "---->"+ "read send error2"+e.getMessage());
				e.printStackTrace();
			}finally{
				try {
					if (client != null) {
						Log.v(TAG, Thread.currentThread().getName() + "---->"+ "client.close()");
						client.close();
					}
				} catch (Exception e2) {
					Log.e(TAG, Thread.currentThread().getName() + "---->"+ "read send error3"+e2.getMessage());
					e2.printStackTrace();
				}
			}
		}
	}

}
