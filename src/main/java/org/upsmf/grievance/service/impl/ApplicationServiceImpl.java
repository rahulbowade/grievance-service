package org.upsmf.grievance.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.upsmf.grievance.dao.ApplicationDao;
import org.upsmf.grievance.model.App;
import org.upsmf.grievance.model.ServiceRequest;
import org.upsmf.grievance.model.StatusIdMap;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.service.ApplicationService;
import org.upsmf.grievance.util.Constants;
import org.upsmf.grievance.util.ProjectUtil;
import org.upsmf.grievance.util.Sql;

@Service(value = Constants.APP_SERVICE)
public class ApplicationServiceImpl implements ApplicationService {

	@Autowired
	private ApplicationDao applicationDao;

	@Override
	public App createApp(App app, User user) {
		if (app.getId() != null) {
			return applicationDao.updateApp(app, user);
		}
		return applicationDao.createApp(app, user);
	}

	@Override
	public List<App> getApp(Long id, String keyword, User user) {
		List<App> app = new ArrayList<>();
		if (id != null) {
			return applicationDao.getApp(id);
		} else if (!ProjectUtil.isObjectNull(user.getRoles()) && user.getOrgId() == null) {
			if (user.getRoles().get(0).getName().equalsIgnoreCase(Sql.Common.SUPER_ADMIN)) {
				return applicationDao.getAllApps();
			}

		} else if (user.getOrgId() != null) {
			return applicationDao.getAppsByOrgId(user.getOrgId());
		}
		return app;
	}

	@Override
	public boolean mapAppsToHelpdesk(StatusIdMap statusIdMap) {
		return applicationDao.mapAppsToHelpdesk(statusIdMap);
	}

	@Override
	public List<ServiceRequest> getServiceRequests() {
		return applicationDao.getServiceRequests();
	}

	@Override
	public List<App> getAppIdAndAppObject() {
		return applicationDao.getAppIdAndAppObject();
	}

	@Override
	public List<App> getApp(Long orgId) {
		return applicationDao.getAppsByOrgId(orgId);
	}

}
