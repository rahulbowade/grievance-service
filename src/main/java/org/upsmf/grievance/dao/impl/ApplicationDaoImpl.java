package org.upsmf.grievance.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import org.upsmf.grievance.dao.ApplicationDao;
import org.upsmf.grievance.executor.MasterDataManager;
import org.upsmf.grievance.model.App;
import org.upsmf.grievance.model.KeyFactory;
import org.upsmf.grievance.model.S3Config;
import org.upsmf.grievance.model.ServiceRequest;
import org.upsmf.grievance.model.StatusIdMap;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.util.Constants;
import org.upsmf.grievance.util.DateUtil;
import org.upsmf.grievance.util.S3FileManager;
import org.upsmf.grievance.util.Sql;
import org.upsmf.grievance.util.Sql.Apps;

@Repository(Constants.APP_DAO)
public class ApplicationDaoImpl implements ApplicationDao {

	private static final String ENCOUNTERED_AN_EXCEPTION_WHILE_CREATING_APP_S = "Encountered an Exception while creating App :  %s";
	private static final String ENCOUNTERED_AN_EXCEPTION_GET_ORGANIZATION_APP_DAO_IMPL_S = "Encountered an exception getOrganizationApp daoImpl :  %s";
	private static final String QUERY_TO_EXECUTE = "Query to execute : ";
	private static final String ENCOUNTERED_AN_EXCEPTION_S = "Encountered an Exception :  %s";
	public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationDaoImpl.class);
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public App createApp(App app, User user) {
		Long id = (long) 0;
		try {
			app.setAppKey(UUID.randomUUID().toString());
			KeyHolder keyHolder = KeyFactory.getkeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					String[] returnValColumn = new String[] { "id" };
					PreparedStatement statement = con.prepareStatement(Apps.INSERT_NEW_APP, returnValColumn);
					statement.setString(1, app.getName());
					statement.setString(2, app.getAppUrl());
					statement.setString(3, app.getLogo());
					statement.setLong(4, app.getSourceId());
					statement.setLong(5, user.getId());
					statement.setBoolean(6, Boolean.TRUE);
					statement.setString(7, app.getClientName());
					statement.setString(8, app.getVersion());
					statement.setString(9, app.getAppKey());
					statement.setString(10, app.getDescription());
					return statement;
				}
			}, keyHolder);
			id = keyHolder.getKey().longValue();
			app.setId(id);
			Long orgAppId = jdbcTemplate.queryForObject(Sql.Apps.GET_ID_FROM_ORG_APP,
					new Object[] { id, user.getOrgId() }, Long.class);
			if (orgAppId < 1) {
				jdbcTemplate.update(Apps.MAP_APP_TO_ORG, new Object[] { id, user.getOrgId() });
			}
			MasterDataManager.getAppObjectFromAppName();
		} catch (InvalidDataAccessApiUsageException e) {
			LOGGER.error(String.format(
					"Encountered an Exception while creating App :  InvalidDataAccessApiUsageException :  %s",
					e.getMessage()));
		} catch (DataAccessException e) {
			LOGGER.error(String.format("Encountered an Exception while creating App  : DataAccessException :  %s",
					e.getMessage()));
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_CREATING_APP_S, e.getMessage()));
		}
		return app;
	}

	@Override
	public App updateApp(App app, User user) {
		try {

			jdbcTemplate.update(Apps.UPDATE_APP, new Object[] { app.getName(), app.getAppUrl(), app.getLogo(),
					app.getSourceId(), app.getActiveStatus(), app.getClientName(), app.getVersion(), user.getId(),
					DateUtil.getFormattedDateInUTC(new Date()), app.getAppKey(), app.getDescription(), app.getId() });
			if (app.getActiveStatus()) {
				jdbcTemplate.update(Sql.Apps.DELETE_ORG_APP, new Object[] { app.getId(), user.getOrgId() });
			}
			MasterDataManager.getAppObjectFromAppName();
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_CREATING_APP_S, e.getMessage()));
		}
		return app;
	}

	@Override
	public List<App> getApp(Long id) {
		List<App> appList = null;
		try {
			LOGGER.info(QUERY_TO_EXECUTE + Apps.GET_APP + Sql.Common.WHERE_CLAUSE + Apps.ID_CONDITION);
			appList = jdbcTemplate.query(Apps.GET_APP + Sql.Common.WHERE_CLAUSE + Apps.ID_CONDITION,
					new Object[] { id }, MasterDataManager.rowMapApp);
			appList = getLogo(appList);
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an exception while fetching all roles :  %s", e.getMessage()));
		}
		return appList;
	}

	public List<App> getLogo(List<App> appList) {
		try {
			for (int i = 0; i < appList.size(); i++) {
				String url = getAppLogo(appList.get(i));
				appList.get(i).setLogo(url);
			}
			return appList;
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return appList;
	}

	public String getAppLogo(App app) {
		if (app.getLogo() != null && !app.getLogo().isEmpty()) {
			try {
				List<S3Config> s3 = jdbcTemplate.query(Sql.GET_S3_ACCESS, MasterDataManager.rowMapS3Config);
				S3Config s3values;
				if (!s3.isEmpty()) {
					s3values = s3.get(0);
					String url = null;
					url = S3FileManager.getPreSignedURL(s3values, app.getLogo());
					return url;
				}
			} catch (Exception e) {
				LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
			}
		}
		return null;
	}

	@Override
	public List<App> getAllApps() {
		List<App> appList = null;

		try {
			LOGGER.info(QUERY_TO_EXECUTE + Apps.GET_APP);
			appList = jdbcTemplate.query(Apps.GET_APP, new Object[] {}, MasterDataManager.rowMapApp);
			if (appList != null && !appList.isEmpty()) {
				appList = getLogo(appList);
			}
		} catch (Exception e) {
			LOGGER.error(String.format("Exception in getAllApplications daoImpl :  %s", e.getMessage()));
		}

		return appList;
	}

	@Override
	public List<App> getAppsByOrgId(Long orgId) {
		try {
			LOGGER.info(QUERY_TO_EXECUTE + Apps.GET_ORG_APPS);
			List<App> apps = jdbcTemplate.query(Apps.GET_ORG_APPS, new Object[] { orgId }, MasterDataManager.rowMapApp);
			if (apps != null && !apps.isEmpty()) {
				apps = getLogo(apps);
			}
			return apps;
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_GET_ORGANIZATION_APP_DAO_IMPL_S, e.getMessage()));
			return new ArrayList<>();
		}
	}

	@Override
	public List<String> getDistinctAppNames(Long orgId) {
		try {
			LOGGER.info(QUERY_TO_EXECUTE + Apps.GET_APP_URLS);
			return jdbcTemplate.queryForList(Apps.GET_APP_URLS, new Object[] { orgId }, String.class);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_GET_ORGANIZATION_APP_DAO_IMPL_S, e.getMessage()));
			return new ArrayList<>();
		}
	}

	@Override
	public boolean mapAppsToHelpdesk(StatusIdMap statusIdMap) {
		try {
			Long id = jdbcTemplate.queryForObject(Apps.GET_HELPDESK_ID,
					new Object[] { statusIdMap.getHelpdeskId(), statusIdMap.getAppId() }, Long.class);
			if (id == 0) {
				LOGGER.info(QUERY_TO_EXECUTE + Apps.MAP_HELPDESK_APP);
				jdbcTemplate.update(Apps.MAP_HELPDESK_APP,
						new Object[] { statusIdMap.getHelpdeskId(), statusIdMap.getAppId() });
				MasterDataManager.getHelpdeskIdFromAppId();
				return true;
			}
		} catch (Exception e) {
			LOGGER.error(String.format("Exception in mapAppToOrg daoImpl :  %s", e.getMessage()));
		}
		return false;
	}

	@Override
	public List<ServiceRequest> getServiceRequests() {
		List<ServiceRequest> serviceRequests = null;
		try {
			LOGGER.info(QUERY_TO_EXECUTE + Apps.GET_SOURCE);
			serviceRequests = jdbcTemplate.query(Apps.GET_SOURCE, MasterDataManager.rowMapServiceRequest);
			return serviceRequests;
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an exception getOrganizationApp daoImpl  %s", e.getMessage()));
			return serviceRequests;
		}
	}

	@Override
	public List<App> getAppIdAndAppObject() {
		List<App> app = null;
		try {
			app = jdbcTemplate.query(Sql.UserQueries.GET_APP_ID_APP_OBJECT, MasterDataManager.rowMapApp);
			return app;
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an exception while fetching all roles  %s", e.getMessage()));
			return app;
		}
	}

}
