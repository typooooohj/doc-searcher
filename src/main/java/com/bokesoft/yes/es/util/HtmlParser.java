package com.bokesoft.yes.es.util;

import java.io.File;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.bokesoft.yes.es.file.FileItem;

/**
 * 文档解析类
 * 
 * @author rjx
 *
 */
public class HtmlParser {
	
	public static FileItem parse(File html,String charsetName) throws Throwable {
		Document doc = Jsoup.parse(html,charsetName);
		String title = doc.title();
		String content = doc.body().text();
		FileItem item = new FileItem(html.getPath(), title, content);
		return item;
	}
	
	public static void main(String[] args) throws Throwable {
		File file = new File("E:/files/Yigo2.html");
		
		FileItem item = HtmlParser.parse(file, "UTF-8");
		
		System.out.println(item.getTitle());
		
		System.out.println(item.getContent());
		
	}
	
}
