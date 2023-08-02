package org.upsmf.grievance.util;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResponseMessages {

	public static final String UNAVAILABLE = "UNAVAILABLE";
	public static final String INVALID = "INVALID";
	public static final String ALREADY_EXISITS = "ALREADYEXISTS";
	public static final String INTERNAL_ERROR = "INTERNALERROR";

	public class ErrorMessages {
		final Integer CUSTOM_ERROR_ID = 9999;
		public static final String ROLE_NAME_UNAVAILABLE = "Role Name is mandatory. Please add and try again";
		public static final String ROLE_ID_UNAVAILABLE = "Role ID is mandatory. Please add and try again";
		public static final String ROLE_DETAILS_UNAVAILABLE = "Role Details are not available. Please check";
		public static final String ROLE_DETAILS_NOTSAVED = "Unable to save the Role Details. Please try again later";
		public static final String USER_ROLE_MAPPING_NOTSAVED = "Unable to save the User Role mapping";
		public static final String USER_ID_UNAVAILABLE = "User ID is mandatory. Please add and try again";
		public static final String ROLE_ID_INVALID = "Role ID cannot be Zero. Please check and try again!";
		public static final String FEATURE_NAME_UNAVAILABLE = "Feature Name is mandatory. Please add and try again";
		public static final String FEATURE_CODE_UNAVAILABLE = "Feature Code is mandatory. Please add and try again";
		public static final String FEATURE_URL_UNAVAILABLE = "Feature URL is mandatory. Please add and try again";
		public static final String FEATURE_DETAILS_UNAVAILABLE = "Feature Details are not available. Please check";
		public static final String FEATURE_DETAILS_NOTSAVED = "Unable to save the Feature Details. Please try again later";
		public static final String USER_PROFILE_UNAVAILABLE = "User Profile Details are not found. Please check";
		public static final String USER_NAME_ALREADY_EXISTS = "UserName already exists. Please try with a different input";
		public static final String USER_PROFILE_ID_MANDATORY = "User Profile ID is mandatory. Please check";
		public static final String USER_PROFILE_SAVE_FAILURE = "Could not save the User Profile. Please check";
		public static final String EMAIL_PHONE_ALREADY_EXISTS = "This email or phone number already exists. Please reenter and check ";
		public static final String EMAIL_MANDATORY = "Email Address is mandatory. Please enter and try again";
		public static final String LOGOUT_FAILED = "User Log Out action has failed. Please try again";
		public static final String GETALLORG_FAILED = "Couldn't fetch all the organization. Please try again later";
		public static final String UPDATE_ORG_FAILED = "Couldn't update the organization. Please check";
		public static final String GET_ORG_BY_ID_FAILED = "Couldn't fetch this organization. Please try again later";
		public static final String ADD_ORG_FAILED = "Couldn't create new organization. Please try with a different email address";
		public static final String DELETE_ORG_FAILED = "Couldn't delete this organization. Please check.";
		public static final String ADDTICKET_FAILED = "Couldn't create this Ticket. Please check.";
		public static final String GETALLTICKET_FAILED = "Could't fetch the tickets. Please try again later";
		public static final String UPDATETICKET_FAILED = "Couldn't update this ticket at the moment. Please check";
		public static final String GETTICKETDETAILS_FAILED = "Couldn't fetch ticket details. Please try again later";
		public static final String LOGIN_FAILED = "We couldn't log you in. Please try again later with right credentials";
		public static final String FORGOT_PSWRD_FAILED = "Something went wrong. Please enter your correct username";
		public static final String CHANGE_PSWRD_FAILED = "Couldn't change your password. Please try again with the correct passwords";
		public static final String INVALID_ACCESS_ROLE = "Role does not have access to this URL";
		public static final String UNAUTHORIZED_ACCESS = "Unauthorized Access. Please login again and try!!";
	}

	public class SuccessMessages {
		public static final String ROLE_CREATED = "Role has been added successfully!";
		public static final String ROLE_UPDATED = "Role has been updated successfully!";
		public static final String USER_ROLE_MAPPED = "User has been mapped to Role";
		public static final String ACTION_ADDED = "Feature has been added successfully!";
		public static final String LOGOUT_SUCCESS = "User Logged out successfully";
	}

}
