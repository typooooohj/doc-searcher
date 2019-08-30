package com.bokesoft.yes.es.quartz;

import java.io.FileInputStream;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzManager {

	private StdSchedulerFactory factory;
	
	private QuartzManager() {
		factory = new StdSchedulerFactory();
	}
	
	private static QuartzManager instance;
	
	public static QuartzManager getInstance() {
		if( instance == null ) {
			instance = new QuartzManager();
		}
		return instance;
	}
	
	public void init(FileInputStream in) throws Throwable {
		factory.initialize(in);
	}
	
	public void addJob(String jobName, String jobGroup, String triggerName, String triggerGroup,
			Job job, long delay, int repeatCount, long repeatInterval)
					throws Throwable {
		Scheduler scheduler = factory.getScheduler();
		JobDetail jobDetail = JobBuilder.newJob(job.getClass()).withIdentity(jobName, jobGroup).build();
		Date startTime = new Date(delay + System.currentTimeMillis());
    
		SimpleTrigger trigger = null;
		if ( repeatCount == -1 ) {
			trigger = (SimpleTrigger)TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroup)
					.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(repeatInterval).repeatForever()).startAt(startTime).build();			
		} else {
			trigger = (SimpleTrigger)TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroup)
					.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(repeatInterval).withRepeatCount(repeatCount)).startAt(startTime).build();			
		}
		scheduler.scheduleJob(jobDetail, trigger);
  }

}
