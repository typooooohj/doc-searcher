package com.bokesoft.yes.es.common;

import java.util.ArrayList;
import java.util.List;
/**
 * ES��ȫ������
 * @author ����
 *
 */
public class Setting {

//	private String fileDir = "";
//	
//	private List<String> types = null;
	
	private String webappPath = "";
	
	private String host = "127.0.0.1";
	
	private int port = 9300;
	
	// ���������Ʊ���Сд
	public static String INDEX_NAME = "yigo";
	
	// ���������ͱ���Сд
	public static String INDEX_TYPE = "coc";
	
	private static Setting instance;
	
	private Setting() {
//		types = new ArrayList<String>();
	}
	
//	public String getFileDir() {
//		return fileDir;
//	}
//
//	public void setFileDir(String fileDir) {
//		this.fileDir = fileDir;
//	}
//
//	public List<String> getTypes() {
//		return types;
//	}
//
//	public void setTypes(List<String> types) {
//		this.types = types;
//	}
//	
//	public boolean containsType(String type) {
//	    return this.types.contains(type);
//	}

	public String getHost() {
		return host;
	}

	public String getWebappPath() {
		return webappPath;
	}

	public void setWebappPath(String webappPath) {
		this.webappPath = webappPath;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public static Setting getInstance() {
		if( instance == null ) {
			instance = new Setting();
		}
		return instance;
	}
}
