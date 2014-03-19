package com.neusoft.neespad.common;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;

public class MyApplication extends Application {

	private String message;//存储信息
	private String[] jsonString;// 传输数据
	private Map<String,Object>  map;//发送用的
	@Override
	public void onCreate() {
		super.onCreate();
		message="";
		jsonString=null;
		map=new HashMap<String, Object>();
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
}
