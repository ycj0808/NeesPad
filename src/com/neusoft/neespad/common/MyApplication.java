package com.neusoft.neespad.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.view.MotionEvent;

public class MyApplication extends Application {

	private String message;//存储信息
	private String[] jsonString;// 传输数据
	private Map<String,Object>  map;//发送用的
	private ArrayList<PaintNode> paintpath;//记录画笔笔记
	
	@Override
	public void onCreate() {
		super.onCreate();
		message="";
		jsonString=null;
		map=new HashMap<String, Object>();
		paintpath=new ArrayList<PaintNode>();
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String[] getJsonString() {
		return jsonString;
	}
	public void setJsonString(String[] jsonString) {
		this.jsonString = jsonString;
	}
	public Map<String, Object> getMap() {
		return map;
	}
	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
	
	/**
	 * 获取画笔轨迹
	 * @return
	 */
	public ArrayList<PaintNode> getPaintPath(){
		return paintpath;
	}
	/**
	 * 添加画笔轨迹
	 * @param node
	 */
	public void addNodeToPaintPath(PaintNode node){
		paintpath.add(node);
	}
	/**
	 * 清除画笔轨迹
	 */
	public void clearPaintNode(){
		paintpath.clear();
	}
	
	/**
	 * 记录轨迹
	 * @author yangchj
	 */
	public class PaintNode{
		public long time;
		public float x;
		public float y;
		public int action;
		public int penWidth;
		public int penStyle;
		public int penColor;
		public static final int ACTION_DOWN=MotionEvent.ACTION_DOWN;
		public static final int ACTION_UP=MotionEvent.ACTION_UP;
		public static final int ACTION_MOVE=MotionEvent.ACTION_MOVE;
		public PaintNode() {
		}
	}
}
