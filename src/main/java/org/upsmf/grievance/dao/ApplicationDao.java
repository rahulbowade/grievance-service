package org.upsmf.grievance.dao;

import java.util.List;

import org.upsmf.grievance.model.App;
import org.upsmf.grievance.model.ServiceRequest;
import org.upsmf.grievance.model.StatusIdMap;
import org.upsmf.grievance.model.User;

public interface ApplicationDao {

	public App createApp(App app, User user);

	/**
	 * This method will return an application information
	 *
	 * @param id
	 *            Long
	 * @param orgId
	 *            Long
	 * @return List<App>
	 */
	public List<App> getApp(Long id);

	/**
	 * This method will return all the applications
	 *
	 * @return List<App>
	 */
	public List<App> getAllApps();

	/**
	 * This method will return applications assigned to an org
	 *
	 * @param orgId
	 *            Long
	 * @return List<App>
	 */
	public List<App> getAppsByOrgId(Long orgId);

	/**
	 * This method will map an application to a helpdesk
	 *
	 * @param statusIdMap
	 *            StatusIdMap
	 * @return boolean
	 */
	public boolean mapAppsToHelpdesk(StatusIdMap statusIdMap);

	/**
	 * This method will fetch the available Service Requests from the database and
	 * map it to a list of Service Request Objects and returns to the Service Layer
	 *
	 * @return List<ServiceRequest>
	 */
	public List<ServiceRequest> getServiceRequests();

	public List<App> getAppIdAndAppObject();

	App updateApp(App app, User user);

	List<String> getDistinctAppNames(Long orgId);

}
