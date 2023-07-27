
package org.upsmf.grievance.controller;

import java.util.List;

import org.upsmf.grievance.dto.ChangePasswordDto;
import org.upsmf.grievance.dto.HelpdeskDto;
import org.upsmf.grievance.dto.HelpdeskTypeDto;
import org.upsmf.grievance.dto.UserDto;
import org.upsmf.grievance.exceptions.RequestDataMissingException;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.util.Constants;
import org.upsmf.grievance.util.ProjectUtil;

/**
 * @author Juhi This class will validate controller request.
 */
public class ControllerRequestValidator {
	/**
	 * This method will parse user registration data.
	 *
	 * @param user
	 *            UserDto
	 * @return boolean
	 * @throws RequestDataMissingException
	 */
	public static boolean validateRegistrationData(User user) {
		boolean response = true;
		if (ProjectUtil.isStringNullOrEmpty(user.getUsername())) {
			throw new RequestDataMissingException(Constants.USER_NAME_MISSING);
		} else if (ProjectUtil.isStringNullOrEmpty(user.getName())) {
			throw new RequestDataMissingException(Constants.PHONE_NUMBER_MISSING);
		}
		return response;
	}

	/**
	 * This method will validate change password data
	 *
	 * @param passwordDto
	 *            ChangePasswordDto
	 * @return boolean
	 * @throws RequestDataMissingException
	 */
	public static boolean changePassword(ChangePasswordDto passwordDto) {
		boolean response = true;
		if (ProjectUtil.isStringNullOrEmpty(passwordDto.getOldPass())) {
			throw new RequestDataMissingException(Constants.PSWRD_MISSING);
		} else if (ProjectUtil.isStringNullOrEmpty(passwordDto.getNewPass())) {
			throw new RequestDataMissingException(Constants.PSWRD_MISSING);
		} else if (!passwordDto.getNewPass().equals(passwordDto.getConfirmNewPass())) {
			throw new RequestDataMissingException(Constants.PSWRD_MISMATCH);
		} else if (passwordDto.getNewPass().equals(passwordDto.getOldPass())) {
			throw new RequestDataMissingException(Constants.PSWRD_SAME);
		}
		return response;
	}

	/**
	 * This method will validate incoming login request data
	 *
	 * @param userDto
	 *            ChangePasswordDto
	 * @return boolean
	 * @throws RequestDataMissingException
	 */
	public static boolean login(UserDto userDto) {
		boolean response = true;
		if (ProjectUtil.isStringNullOrEmpty(userDto.getUsername())) {
			throw new RequestDataMissingException(Constants.USER_NAME_MISSING);
		} else if (ProjectUtil.isStringNullOrEmpty(userDto.getPassword())) {
			throw new RequestDataMissingException(Constants.PSWRD_MISSING);
		}
		return response;
	}

	public static boolean forgotPassword(UserDto userDto) {
		boolean response = true;
		if (ProjectUtil.isStringNullOrEmpty(userDto.getUsername())) {
			throw new RequestDataMissingException(Constants.USER_NAME_MISSING);
		}
		return response;
	}

	/**
	 * This method validates the user id.
	 *
	 * @param userDto
	 *            UserDto
	 * @return UserDto
	 * @throws RequestDataMissingException
	 */
	public static UserDto validateInputId(UserDto userDto) {
		if (userDto.getId() == 0L) {
			throw new RequestDataMissingException(Constants.USER_NAME_MISSING);
		}
		return userDto;
	}

	/**
	 * This method validates primary details of the user
	 *
	 * @param userDto
	 *            UserDto
	 * @return boolean
	 * @throws RequestDataMissingException
	 */
	public static boolean validateUserPrimaryDetails(UserDto userDto) {
		boolean response = true;
		if (userDto.getId() == 0L) {
			throw new RequestDataMissingException(Constants.ID_MISSING);
		}

		User user = new User();
		user.setName(userDto.getName());
		user.setUsername(userDto.getUsername());
		validateRegistrationData(user);

		return response;
	}

	/**
	 * This method validates primary details of the user
	 *
	 * @param userDto
	 *            UserDto
	 * @return boolean
	 * @throws RequestDataMissingException
	 */
	public static boolean validateConfigureHelpdeskData(HelpdeskDto helpdeskDto) {
		boolean response = true;
		if (helpdeskDto.getTypes() != null && !helpdeskDto.getTypes().isEmpty()) {
			List<HelpdeskTypeDto> typeDtoList = helpdeskDto.getTypes();
			for (HelpdeskTypeDto dto : typeDtoList) {
				if (dto.getWorkflowStages() == null) {
					throw new RequestDataMissingException(Constants.HELPDESKTYPE_WITHOUTWORKFLOW);
				} else if (dto.getWorkflowStages().isEmpty()) {
					throw new RequestDataMissingException(Constants.HELPDESKTYPE_WORKFLOW_EMPTY);
				}
			}
		}
		return response;
	}

	private ControllerRequestValidator() {
		super();
	}

}
