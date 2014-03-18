package com.neusoft.neespad.common;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;

public class MyApplication extends Application {

	private String message;//�洢��Ϣ
	private String[] jsonString;// ��������
	private Map<String,Object>  map;//�����õ�
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
