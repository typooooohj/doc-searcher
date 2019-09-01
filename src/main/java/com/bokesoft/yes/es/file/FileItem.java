package com.bokesoft.yes.es.file;

import java.util.UUID;

import org.json.JSONObject;

/**
 * 代表一个需要存入ES的文件
 * @author rjx
 *
 */
public class FileItem {
	private String id;
	private String path;
	private String title;
	private String desc;// 摘要,用于搜索显示 TODO
	private String content;
	
	public FileItem(String path, String title, String content) {
		super();
		this.id = UUID.randomUUID().toString();
		this.path = path;
		this.title = title;
		this.content = content;
	}
	
	public String getId() {
		return id;
	}

	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("path", path);
		json.put("title", title);
		json.put("desc", desc);
		json.put("content", content);
		return json;
	}
	
}
