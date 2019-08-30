package com.bokesoft.yes.es.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.bokesoft.yes.es.common.Setting;
import com.bokesoft.yes.es.util.HtmlParser;

public class FileReader {
	
	private FileReader() {
		
	}

	/**
	 * 读取一个目录中的所有文件及子文件,返回文件列表
	 * 并做一些特殊处理,如html文件,去除一些script标签
	 * @param path 目录
	 * @return 文件列表
	 * @throws Throwable 
	 */
	public static List<FileItem> read(String path) throws Throwable {
		List<FileItem> items = new ArrayList<FileItem>();
		File dir = new File(path);
		if( !dir.isDirectory() ){
			throw new RuntimeException("not a directory");
		}
		File[] files = dir.listFiles();
		for( File file : files ) {
			readFile(file, items);
		}
		return items;
	}
	
	private static void readFile(File file,List<FileItem> items) throws Throwable {
		if( !file.isDirectory() ) {
//			String fileName = file.getName();
//			String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
//			Setting setting = Setting.getInstance();
//			if( setting.containsType(prefix) ) {
				items.add(HtmlParser.parse(file, "UTF-8"));
//			}
		} else {
			readFile(file,items);			 
		}
	}
	
}
