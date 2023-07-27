package org.upsmf.grievance.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.upsmf.grievance.executor.MasterDataManager;
import org.upsmf.grievance.model.App;
import org.upsmf.grievance.model.StatusIdMap;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.service.ApplicationService;
import org.upsmf.grievance.util.PathRoutes;
import org.upsmf.grievance.util.ResponseGenerator;
import org.upsmf.grievance.util.ResponseMessages;

@RestController
@RequestMapping(PathRoutes.APPS_ACTIONS_URL)
public class ApplicationController {

	private static final String KEYWORD = "keyword";
	private static final String ID = "id";
	private static final String ORG_ID = "orgId";
	private static final String USER_INFO = "UserInfo";

	public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);

	@Autowired
	private ApplicationService applicationService;

	@PostMapping(value = PathRoutes.AppsRoutes.CREATE_APP)
	public String createUpdateApp(@RequestAttribute(value = USER_INFO) User user, @RequestBody App app,
			BindingResult result) throws JsonProcessingException {
		if (result.hasErrors()) {
			return ResponseGenerator.failureResponse(HttpStatus.UNPROCESSABLE_ENTITY.toString());
		}

		if (app != null) {
			if (StringUtils.isBlank(app.getName())) {
				return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.ROLE_NAME_UNAVAILABLE);
			}
		} else {
			return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.ROLE_DETAILS_UNAVAILABLE);
		}
		user.setOrgId(app.getOrgId());
		App savedApp = applicationService.createApp(app, user);
		if (savedApp != null) {
			List<Object> savedApps = new ArrayList<>();
			savedApps.add(savedApp);
			MasterDataManager.getAppObjectFromAppName();
			return ResponseGenerator.successResponse(savedApps);
		} else {
			return ResponseGenerator.failureResponse(HttpStatus.SERVICE_UNAVAILABLE.toString());
		}
	}

	/**
	 * This method will return List of applications
	 *
	 * @param id
	 *            Long
	 * @param keyword
	 * @param user
	 *            User
	 * @return String
	 * @throws JsonProcessingException
	 */
	@GetMapping(value = PathRoutes.AppsRoutes.GET_APP)
	public String getApp(@RequestParam(value = ID, required = false) Long id,
			@RequestParam(value = KEYWORD, required = false) String keyword,
			@RequestParam(value = ORG_ID, required = false) Long orgId, @RequestAttribute(value = USER_INFO) User user)
			throws JsonProcessingException {
		if (orgId != null) {
			user.setOrgId(orgId);
		} else {
			user.setOrgId(null);
		}
		return ResponseGenerator.successResponse(applicationService.getApp(id, "", user));
	}

	@GetMapping(value = PathRoutes.AppsRoutes.GET_APP_BY_ORG_ID)
	public String getAppByOrgId(@RequestParam(value = ORG_ID, required = false) Long orgId)
			throws JsonProcessingException {
		return ResponseGenerator.successResponse(applicationService.getApp(orgId));
	}

	/**
	 * This method will map an application to a helpdesk
	 *
	 * @param statusIdMap
	 *            StatusIdMap
	 * @return String
	 * @throws JsonProcessingException
	 */
	@PostMapping(PathRoutes.AppsRoutes.MAP_APP_TO_HELPDESK)
	public String mapAppsToHelpdesk(@RequestBody StatusIdMap statusIdMap) throws JsonProcessingException {

		boolean response = applicationService.mapAppsToHelpdesk(statusIdMap);

		if (response) {
			return ResponseGenerator.successResponse(response);
		}

		return ResponseGenerator.failureResponse();
	}

	/**
	 * This API will get the Master Data which has been maintained for the Service
	 * Request types available in the application
	 *
	 * @param user
	 * @return
	 * @throws JsonProcessingException
	 */
	@GetMapping(value = PathRoutes.AppsRoutes.GET_SERVICE_REQUEST)
	public String getServiceRequests(@RequestAttribute(value = USER_INFO) User user) throws JsonProcessingException {
		return ResponseGenerator.successResponse(applicationService.getServiceRequests());
	}
}
