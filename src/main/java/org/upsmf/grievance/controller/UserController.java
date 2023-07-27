package org.upsmf.grievance.controller;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.upsmf.grievance.dto.ChangePasswordDto;
import org.upsmf.grievance.dto.LoginDto;
import org.upsmf.grievance.dto.UserDto;
import org.upsmf.grievance.executor.MasterDataManager;
import org.upsmf.grievance.model.Action;
import org.upsmf.grievance.model.RolesDto;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.service.UserService;
import org.upsmf.grievance.util.Constants;
import org.upsmf.grievance.util.PathRoutes;
import org.upsmf.grievance.util.ProjectUtil;
import org.upsmf.grievance.util.ResponseGenerator;
import org.upsmf.grievance.util.ResponseMessages;
import org.upsmf.grievance.util.Sql;

@RestController
@RequestMapping(PathRoutes.USER_ACTIONS_URL)
public class UserController {

	private static final String BEARER = "Bearer ";
	private static final String FILE2 = "file";
	private static final String ID = "id";
	private static final String ORG_ID = "orgId";
	private static final String USER_INFO = "UserInfo";

	public static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@PostMapping(value = PathRoutes.UserRoutes.USER_ACTIONS_POST)
	public String getActions(@RequestBody RolesDto roles) throws JsonProcessingException {
		List<Action> actions = userService.findAllActionsByRoleID(roles.getIds());
		return ResponseGenerator.successResponse(actions);
	}

	@PostMapping(value = PathRoutes.UserRoutes.NUMBER_OF_USERS_GET)
	public String getNumberOfUsers(@RequestBody RolesDto roles) throws JsonProcessingException {
		return ResponseGenerator.successResponse(userService.getNumberOfUsers(roles.getId(), roles.isActive()));
	}

	@GetMapping(value = PathRoutes.UserRoutes.NUMBER_OF_ROLES_GET)
	public String getNumberOfRoles() throws JsonProcessingException {
		return ResponseGenerator.successResponse(userService.getNumberOfRoles());
	}

	@GetMapping(value = PathRoutes.UserRoutes.USER_BY_ID_GET)
	public String getOne(@RequestParam(value = ID, required = true) Long id) throws JsonProcessingException {
		return ResponseGenerator.successResponse(userService.findById(id));
	}

	@GetMapping(value = PathRoutes.UserRoutes.LIST_USER_GET)
	public String listUser(@RequestParam(value = ORG_ID, required = true) Long orgId) throws JsonProcessingException {
		return ResponseGenerator.successResponse(userService.findAll(orgId));
	}

	@GetMapping(value = PathRoutes.UserRoutes.UPLOAD_PROFILE_PICTURE)
	public String uploadProfilePicture(@RequestParam(value = FILE2, required = true) MultipartFile file,
			@RequestAttribute(value = USER_INFO) User user) throws JsonProcessingException {
		return ResponseGenerator.successResponse(userService.uploadFile(file, user.getId()));
	}

	@GetMapping(value = PathRoutes.UserRoutes.GET_PROFILE_PICTURE)
	public String getProfilePicture(@RequestAttribute(value = USER_INFO) User user) throws IOException {
		return ResponseGenerator.successResponse(userService.getFile(user.getId()));
	}

	@PostMapping(value = PathRoutes.UserRoutes.CREATE_UPDATE_USER_POST)
	public String saveUser(@RequestAttribute(value = USER_INFO) User user,
			@RequestParam(value = FILE2, required = false) MultipartFile file, @RequestBody User profile)
			throws JsonProcessingException {
		if (!StringUtils.isNotBlank(profile.getUsername())) {
			ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.EMAIL_MANDATORY);
		}
		ControllerRequestValidator.validateRegistrationData(profile);
		Long userId = userService.checkUserNameExists(profile.getUsername());
		if (!ProjectUtil.isObjectNull(profile.getId())) {
			if (userId.equals(profile.getId())) {
				return ResponseGenerator.successResponse(userService.update(file, profile));
			} else {
				return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.EMAIL_PHONE_ALREADY_EXISTS);
			}
		} else if (userId > 0) {
			return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.EMAIL_PHONE_ALREADY_EXISTS);
		}
		String name = MasterDataManager.getRoleMap().get(MasterDataManager.getUserRoleMap().get(user.getId()))
				.getName();
		if (userId == 0
				&& (name.equalsIgnoreCase(Sql.Common.SUPER_ADMIN) || name.equalsIgnoreCase(Sql.Common.ORGADMIN))) {
			User isAdded = userService.save(file, user.getId(), profile);
			if (isAdded != null) {
				MasterDataManager.getAllOrgUsers();
				MasterDataManager.getAllUserRoles();
				return ResponseGenerator.successResponse(isAdded);
			}
		}
		return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.USER_PROFILE_SAVE_FAILURE);
	}

	@PostMapping(value = PathRoutes.UserRoutes.LOGIN)
	public String login(@RequestBody UserDto userDto) throws JsonProcessingException {
		ControllerRequestValidator.login(userDto);
		LoginDto response = userService.login(userDto);
		if (response != null) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.LOGIN_FAILED);
	}

	@PostMapping(value = PathRoutes.UserRoutes.FORGOT_PSWRD)
	public String forgotPassword(@RequestBody UserDto userDto) throws JsonProcessingException {
		ControllerRequestValidator.forgotPassword(userDto);
		boolean forgotPasswordResponse = userService.forgotPassword(userDto);

		if (forgotPasswordResponse) {
			return ResponseGenerator.successResponse(forgotPasswordResponse);
		}
		return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.FORGOT_PSWRD_FAILED);
	}

	@PostMapping(value = PathRoutes.UserRoutes.CHANGE_PSWRD)
	public String changePassword(@RequestAttribute(value = USER_INFO) User user,
			@RequestBody ChangePasswordDto changePasswordDto) throws JsonProcessingException {
		ControllerRequestValidator.changePassword(changePasswordDto);
		changePasswordDto.setUserId(user.getId());
		boolean response = userService.changePassword(changePasswordDto);
		if (response) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.CHANGE_PSWRD_FAILED);
	}

	@PostMapping(value = PathRoutes.UserRoutes.USER_ROLE_MAPPING_POST)
	public String mapUserToRole(@RequestBody User user) throws JsonProcessingException {
		if (user != null && user.getRoles() != null) {
			if (user.getRoles().isEmpty()) {
				return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.ROLE_ID_UNAVAILABLE);
			}
			if (user.getId() == null) {
				return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.USER_ID_UNAVAILABLE);
			}
		} else {
			return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.ROLE_DETAILS_UNAVAILABLE);
		}
		Boolean mappingStatus = userService.mapUserToRole(user);
		if (mappingStatus) {
			return ResponseGenerator.successResponse(ResponseMessages.SuccessMessages.USER_ROLE_MAPPED);
		} else {
			return ResponseGenerator.failureResponse(HttpStatus.SERVICE_UNAVAILABLE.toString());
		}
	}

	@GetMapping(value = PathRoutes.UserRoutes.LOGOUT_GET)
	public String invalidateToken(@RequestHeader(value = Constants.AUTH_HEADER) String authToken)
			throws JsonProcessingException {
		Boolean status = false;
		if (authToken != null) {
			authToken = authToken.replace(BEARER, "");
			status = userService.invalidateToken(authToken);
		}
		if (status) {
			return ResponseGenerator.successResponse(ResponseMessages.SuccessMessages.LOGOUT_SUCCESS);
		}
		return ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.LOGOUT_FAILED);
	}

	@GetMapping(value = PathRoutes.UserRoutes.GET_REVIEWS)
	public String getReviews(@RequestAttribute(value = USER_INFO) User user) throws IOException {
		userService.getReviews();
		return ResponseGenerator.successResponse();
	}

}
