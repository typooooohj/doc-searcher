package com.bokesoft.yes.es.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.bokesoft.yes.es.common.Setting;

/**
 * Æô¶¯¼àÌýÆ÷
 * @author ³ÂÈð
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
