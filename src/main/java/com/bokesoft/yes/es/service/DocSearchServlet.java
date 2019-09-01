package com.bokesoft.yes.es.service;

import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bokesoft.yes.es.file.FileItem;
import com.bokesoft.yes.es.manager.ElasticSearchManager;
/**
 * 搜索的Servlet
 * 
 * @author rjx
 *
 */
public class DocSearchServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				
//		String keyword = request.getParameter("keyword");
//		ElasticSearchManager manager = new ElasticSearchManager();
//		manager.multiMatchQuery(keyword, "title", "content");
//		
//		JSONArray array = new JSONArray();
//		
//		JSONObject obj = item.toJSON();
//		JSONObject obj2 = item2.toJSON();
//		
//		array.put(obj);
//		array.put(obj2);
//		
//		response.setContentType("text/html;charset=UTF-8");
//		
//		response.getWriter().write(array.toString());
		
		
		JSONObject json = new JSONObject();
		json.put("key", "AAA");
		
		response.getWriter().write(json.toString());
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}
	
}
