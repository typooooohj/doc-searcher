package com.bokesoft.yes.es.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.bokesoft.yes.es.common.Setting;

/**
 * 服务器启动执行
 * @author rjx
 *
 */
public class ServerStartListener implements ServletContextListener {

	public void contextInitialized(ServletContextEvent event) {
		
		String webappPath = event.getServletContext().getRealPath("/");
		Setting.getInstance().setWebappPath(webappPath);
		
		SystemInit systemInit = new SystemInit();
		systemInit.init();
	}

	public void contextDestroyed(ServletContextEvent event) {
		
	}
}
