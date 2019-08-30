package com.bokesoft.yes.es.listener;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.PropertyResourceBundle;

import com.bokesoft.yes.es.common.Setting;
import com.bokesoft.yes.es.file.FileItem;
import com.bokesoft.yes.es.file.FileReader;
import com.bokesoft.yes.es.manager.ElasticSearchManager;

public class SystemInit {
	
	public void init() {
		FileInputStream in = null;
		try {
//			URL coreUrl = Thread.currentThread().getContextClassLoader().getResource("core.properties");
//			if( coreUrl == null ) {
//				throw new RuntimeException("miss core config");
//			}
			
			URL esUrl = Thread.currentThread().getContextClassLoader().getResource("elasticsearch.properties");
			if( esUrl == null ) {
				throw new RuntimeException("miss es config");
			}
			
			Setting setting = Setting.getInstance();
//			in = new FileInputStream(coreUrl.getPath());
//			PropertyResourceBundle bundle = new PropertyResourceBundle(in);
//			if( bundle.containsKey("DocPath") ) {
//				String dir = bundle.getString("DocPath");
//				setting.setFileDir(dir);
//			}
//			if( bundle.containsKey("DocType") ) {
//				String type = bundle.getString("DocType");
//				setting.setTypes(Arrays.asList(type.split(",")));
//			}
			
			in = new FileInputStream(esUrl.getPath());
			PropertyResourceBundle bundle = new PropertyResourceBundle(in);
			if( bundle.containsKey("Host") ) {
				String host = bundle.getString("Host");
				setting.setHost(host);;
			}
			if( bundle.containsKey("Port") ) {
				String port = bundle.getString("Port");
				setting.setPort(Integer.parseInt(port));;
			}
			
			// 读取所有文件
			List<FileItem> docs = FileReader.read(setting.getWebappPath() + "files");
						
			ElasticSearchManager manager = new ElasticSearchManager();
			manager.createIndex(Setting.INDEX_NAME, Setting.INDEX_TYPE);
			
			// 添加文档
			manager.addDocs(docs);
			
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if( in != null ) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
