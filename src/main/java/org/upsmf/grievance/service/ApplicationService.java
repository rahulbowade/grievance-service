package org.upsmf.grievance.service;

import java.util.List;

import org.upsmf.grievance.model.App;
import org.upsmf.grievance.model.ServiceRequest;
import org.upsmf.grievance.model.StatusIdMap;
import org.upsmf.grievance.model.User;

/**
 * @author Darshan Nagesh
 *
 */
public interface ApplicationService {

	public App createApp(App app, User user);

	/**
	 * This method will return List of applications
	 *
	 * @param id
	 *            Long
	 * @param keyword
	 *            String
	 * @param user
	 *            User
	 * @return List<App>
	 */
	public List<App> getApp(Long id, String keyword, User user);

	/**
	 * This method will map an application to a helpdesk
	 *
	 * @param statusIdMap
	 *            StatusIdMap
	 * @return boolean
	 */
	public boolean mapAppsToHelpdesk(StatusIdMap statusIdMap);

	/**
	 * This method will invoke the DAO method to fetch the master data of Service
	 * Request types available in the application
	 *
	 * @return List<ServiceRequest>
	 *
	 */
	public List<ServiceRequest> getServiceRequests();

	public List<App> getAppIdAndAppObject();

	public Object getApp(Long orgId);

}
