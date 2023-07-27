package org.upsmf.grievance.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.upsmf.grievance.executor.MasterDataManager;
import org.upsmf.grievance.model.CommonDataModel;
import org.upsmf.grievance.model.Organization;
import org.upsmf.grievance.model.StatusIdMap;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.model.enums.AuthTypes;
import org.upsmf.grievance.service.SuperAdminService;
import org.upsmf.grievance.util.PathRoutes;
import org.upsmf.grievance.util.ResponseGenerator;
import org.upsmf.grievance.util.ResponseMessages;

@RestController
@RequestMapping(PathRoutes.SUPERADMIN_ACTIONS_URL)
public class SuperAdminController {

	private static final String USER_ID = "userId";
	private static final String ID = "id";
	private static final String USER_INFO = "UserInfo";
	@Autowired
	private SuperAdminService superAdminService;

	@GetMapping(value = PathRoutes.SuperAdminRoutes.GET_ALL_ORG)
	public String getAllOrganization(@RequestParam(value = ID, required = false) Long id,
			@RequestParam(value = USER_ID, required = false) Long userId) throws JsonProcessingException {
		List<Organization> orgList = new ArrayList<>();
		if (id != null && id > 0) {
			Organization response = superAdminService.getOrganizationById(id);
			orgList.add(response);
		} else if (userId != null) {
			List<Organization> response = superAdminService.getOrganizationByUserId(userId);
			orgList = response;
		} else {
			orgList = superAdminService.getAllOrganization();
		}
		if (!orgList.isEmpty()) {
			return ResponseGenerator.successResponse(orgList);
		}
		return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.GETALLORG_FAILED);
	}

	@PostMapping(value = PathRoutes.SuperAdminRoutes.ADD_ORGANIZATION)
	public String addOrganization(@RequestBody Organization organization) throws JsonProcessingException {
		Organization response = superAdminService.addOrganization(organization);
		if (response != null) {
			MasterDataManager.getAllOrgRoles();
			MasterDataManager.getUserIdAndUserName();
			MasterDataManager.getAllOrgUsers();
			MasterDataManager.getAllUserRoles();
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.ADD_ORG_FAILED);
	}

	@PostMapping(value = PathRoutes.SuperAdminRoutes.DELETE_ORGANIZATION)
	public String deleteOrganization(@RequestAttribute(value = USER_INFO) User user,
			@RequestBody Organization organization) throws JsonProcessingException {
		organization.setUserId(user.getId());
		boolean response = superAdminService.deleteOrganization(organization);
		if (response) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.DELETE_ORG_FAILED);
	}

	@PostMapping(value = PathRoutes.SuperAdminRoutes.UPDATE_ORG_BY_ID)
	public String updateOrganizationById(@RequestAttribute(value = USER_INFO) User user,
			@RequestBody Organization organization) throws JsonProcessingException {
		organization.setUserId(user.getId());
		boolean response = superAdminService.updateOrganizationById(organization);
		if (response) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.UPDATE_ORG_FAILED);
	}

	@PostMapping(value = PathRoutes.SuperAdminRoutes.ADD_ADMIN)
	public String addAdmin(@RequestAttribute(value = USER_INFO) User user, @RequestBody CommonDataModel commonDataModel)
			throws JsonProcessingException {
		boolean response = superAdminService.addAdmin(commonDataModel.getUserId());
		if (response) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.UPDATE_ORG_FAILED);
	}

	@PostMapping(value = PathRoutes.SuperAdminRoutes.REMOVE_ADMIN)
	public String removeAdmin(@RequestAttribute(value = USER_INFO) User user,
			@RequestBody CommonDataModel commonDataModel) throws JsonProcessingException {
		boolean response = superAdminService.removeAdmin(commonDataModel.getUserId());
		if (response) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.UPDATE_ORG_FAILED);
	}

	/**
	 * This method will map an application to an organization
	 *
	 * @param statusIdMap
	 *            StatusIdMap
	 * @return String
	 * @throws JsonProcessingException
	 */
	@PostMapping(PathRoutes.SuperAdminRoutes.MAP_APPS_TO_ORG)
	public String mapAppsToOrg(@RequestBody StatusIdMap statusIdMap) throws JsonProcessingException {

		boolean response = superAdminService.mapAppsToOrg(statusIdMap);

		if (response) {
			return ResponseGenerator.successResponse(response);
		}

		return ResponseGenerator.failureResponse();
	}

	@GetMapping(value = PathRoutes.SuperAdminRoutes.GET_AUTH_TYPES)
	public String getAuthTypes() throws JsonProcessingException {
		final List<CommonDataModel> authTypes = new ArrayList<>();
		for (final AuthTypes key : AuthTypes.values()) {
			authTypes.add(new CommonDataModel(key));
		}
		return ResponseGenerator.successResponse(authTypes);
	}

}
