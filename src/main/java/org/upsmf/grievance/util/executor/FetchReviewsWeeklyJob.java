package org.upsmf.grievance.util.executor;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.upsmf.grievance.dao.TicketDao;
import org.upsmf.grievance.dao.UserDao;

public class FetchReviewsWeeklyJob implements Job {
	public static final Logger LOGGER = LoggerFactory.getLogger(FetchReviewsWeeklyJob.class);
	private static final String CLASSNAME = FetchReviewsWeeklyJob.class.getName();

	@Autowired
	private UserDao userDao;

	@Autowired
	private TicketDao ticketDao;

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		try {
			userDao.getReviews();
			ticketDao.getFeedBacksFromAuroraSdk();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), CLASSNAME);
		}
	}

}
