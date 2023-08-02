package org.upsmf.grievance.util.executor;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReviewsSchedulerManager {
	private static final String CLASSNAME = ReviewsSchedulerManager.class.getName();
	public static final Logger LOGGER = LoggerFactory.getLogger(FetchReviewsWeeklyJob.class);

	/**
	 * scheduler job is configured here
	 */
	public static void schedule() {
		JobDetail weeklyJob = JobBuilder.newJob(FetchReviewsWeeklyJob.class)
				.withIdentity("WeeklyFetchReviews", "WeeklyFetchReviewsData").build();
		Trigger weeklyTrigger = TriggerBuilder.newTrigger().withIdentity("FetchReviewsWeekly", "FetchReviewsDataWeekly")
				.withSchedule(CronScheduleBuilder.cronSchedule("0 0 8 ? * SUN *")).build();
		try {
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler.scheduleJob(weeklyJob, weeklyTrigger);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), CLASSNAME);
		}

	}

	private ReviewsSchedulerManager() {
		super();
	}
}