package com.bokesoft.yes.es.util;

import java.io.FileReader;
import java.io.IOException;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

public class HtmlParser1 extends HTMLEditorKit.ParserCallback {
	private StringBuilder sb;
	
	public HtmlParser1() {
		sb = new StringBuilder();
	}

	public String parser(String fileName) throws IOException {
		FileReader reader = new FileReader(fileName);		
		ParserDelegator delegator = new ParserDelegator();  
	    delegator.parse(reader, this, Boolean.TRUE);
	    return sb.toString();
	}
	
	public void handleText(char[] data, int pos) {
		sb.append(data);
    }
}
