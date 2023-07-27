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
import org.upsmf.grievance.model.Role;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.service.RoleActionService;
import org.upsmf.grievance.util.PathRoutes;
import org.upsmf.grievance.util.ResponseGenerator;
import org.upsmf.grievance.util.ResponseMessages;

@RestController
@RequestMapping(PathRoutes.ROLE_ACTIONS_URL)
public class RoleActionController {

	private static final String ORG_ID = "orgId";
	private static final String USER_ID = "userId";
	private static final String USER_INFO = "UserInfo";

	public static final Logger LOGGER = LoggerFactory.getLogger(RoleActionController.class);

	@Autowired
	private RoleActionService roleActionService;

	@GetMapping(value = PathRoutes.RoleActionRoutes.LIST_ROLES_GET)
	public String listRoles(@RequestParam(value = ORG_ID, required = true) Long orgId) throws JsonProcessingException {
		List<Role> data = roleActionService.getAllRoles(orgId);
		if (data != null) {
			return ResponseGenerator.successResponse(data);
		} else {
			return ResponseGenerator.failureResponse();
		}
	}

	@PostMapping(value = PathRoutes.RoleActionRoutes.ADD_ROLE_POST)
	public String saveRole(@RequestAttribute(value = USER_INFO) User user, @RequestBody Role role, BindingResult result)
			throws JsonProcessingException {
		if (result.hasErrors()) {
			return ResponseGenerator.failureResponse(HttpStatus.UNPROCESSABLE_ENTITY.toString());
		}

		if (role != null) {
			if (StringUtils.isBlank(role.getName())) {
				return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.ROLE_NAME_UNAVAILABLE);
			}
		} else {
			return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.ROLE_DETAILS_UNAVAILABLE);
		}
		role.setOrgId(user.getOrgId());
		Role savedRole = roleActionService.saveRole(role);
		if (savedRole != null) {
			List<Object> savedRoles = new ArrayList<>();
			savedRoles.add(savedRole);
			return ResponseGenerator.successResponse(savedRoles);
		} else {
			return ResponseGenerator.failureResponse(HttpStatus.SERVICE_UNAVAILABLE.toString());
		}
	}

	@PostMapping(value = PathRoutes.RoleActionRoutes.ROLE_BY_ID_GET)
	public String getOne(@RequestAttribute(value = USER_INFO) User user, @RequestBody Role role, BindingResult result)
			throws JsonProcessingException {
		role.setOrgId(user.getOrgId());
		String roles = roleActionService.findById(role);
		if (roles != null) {
			return ResponseGenerator.successResponse(roles);
		} else {
			return ResponseGenerator.failureResponse("Couldn't fetch this role");
		}
	}

	@PostMapping(value = PathRoutes.RoleActionRoutes.UPDATE_ROLE_POST)
	public String update(@RequestAttribute(value = USER_INFO) User user, @RequestBody Role role, BindingResult result)
			throws JsonProcessingException {
		if (result.hasErrors()) {
			return ResponseGenerator.failureResponse(HttpStatus.UNPROCESSABLE_ENTITY.toString());
		}

		if (role != null) {
			if (role.getId() == null) {
				return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.ROLE_ID_UNAVAILABLE);
			}
			if (StringUtils.isBlank(role.getName())) {
				return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.ROLE_NAME_UNAVAILABLE);
			}
			if (role.getId() <= 0) {
				return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.ROLE_ID_INVALID);
			}
			role.setOrgId(user.getOrgId());
			Role savedRole = roleActionService.updateRole(role);
			if (savedRole != null) {
				List<Object> savedRoles = new ArrayList<>();
				savedRoles.add(savedRole);
				return ResponseGenerator.successResponse(savedRoles);
			}
		}
		return ResponseGenerator.failureResponse(HttpStatus.SERVICE_UNAVAILABLE.toString());
	}

	@GetMapping(value = PathRoutes.RoleActionRoutes.MDM_CHECK)
	public String mdmCheck(@RequestAttribute(value = USER_INFO) User user,
			@RequestParam(value = USER_ID, required = false) Long userId) {
		return MasterDataManager.getRoleForUser(userId).toString();
	}

}
