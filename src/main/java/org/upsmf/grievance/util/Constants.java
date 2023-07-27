package org.upsmf.grievance.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {

	private static final String FEEDBACK_BY_NAME = "feedbackByName";
	private static final String TYPE = "type";
	private static final String TOTAL = "total";
	/**
	 * Header and Request Parameters
	 */
	public static final long ACCESS_TOKEN_VALIDITY_SECONDS = (long) 30 * 24 * 60 * 60;
	public static final String REVIEWS2 = "/reviews?translationLanguage=en";
	public static final String HTTPS_WWW_GOOGLEAPIS_COM_ANDROIDPUBLISHER_V3_APPLICATIONS = "https://www.googleapis.com/androidpublisher/v3/applications/";
	public static final String REVIEWSPEC_JSON = "/reviewspec.json";
	public static final String ACCESSTOKENSPEC_JSON = "/accesstokenspec.json";
	public static final String SIGNING_KEY = "devglan123r";
	public static final String JWT_ISSUER = "http://devglan.com";
	public static final String JWT_GRANTED_AUTHORITY = "ROLE_ADMIN";
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String HEADER_APPLICATION_JSON = "application/json";
	public static final String ERROR_CODE = "errorCode";
	public static final String ERROR_FIELD = "errorField";
	public static final String ERROR_MESSAGE_CODE = "errorMessageCode";
	public static final String ERROR_MESSAGE_VALUE = "common.error.";
	public static final String SUCCESS_CODE = "successCode";
	public static final String ERROR_MESSAGE = "errorMessage";
	public static final String SUCCESS_MESSAGE = "successMessage";
	public static final String AUTH_HEADER = "Authorization";
	public static final String PARAMETERS = "parameters";

	public enum ELK_OPERATION {
		SAVE, UPDATE, DELETE;
	}

	public final class RequestParams {
		private RequestParams() {
			super();
		}

		public static final String USER_INFO = "UserInfo";
		public static final String ID = "id";
		public static final String HELPDESK_ID = "helpdeskId";
		public static final String APP_ID = "appId";
	}

	/**
	 * Query Parameters and Response Parameters
	 */
	public static final String USER_INFO_HEADER = "x-user-info";
	public static final String SUCCESS = "success";
	public static final String ASC = "asc";
	public static final String DESC = "desc";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String STRING_BLANK = "";
	public static final String COMMA_SPACE_SEPARATOR = ", ";
	public static final String DATE = "date";
	public static final String QUERY_ALERT_SUBJECT = "Query Alert!!";
	public static final String SCHEDULER_ALERT_SUBJECT = "Scheduler Alert!!";
	public static final String STRING_SPACE = " ";
	public static final String STRING_HYPEN = "-";
	public static final String NEW_MESSAGE = "New";
	public static final String READ_MESSAGE = "Read";
	public static final String DELETE_MESSAGE = "Delete";
	public static final String SEND_MESSAGE = "Send";
	public static final String FILE_TYPE = "PDF,DOC,TXT,JPG,JPEG,PNG,GIF,AAC,MP3,MP4";
	public static final String IMAGE_FILE_TYPE = "JPG,JPEG,PNG,GIF";
	public static final String FCM_API_URL = "fcm.api.url";
	public static final String FCM_API_KEY = "fcm.api.key";

	/**
	 * URLs and Paths
	 */
	public static final String UPLOADED_FOLDER = "/usr/grievance";
	public static final String ATTACHMENT_FOLDER = "C:\\Users\\Juhi Agarwal\\git\\grievance-desk-core\\public\\attachments";

	/**
	 * Status Code and Messages
	 */
	public static final int UNAUTHORIZED_ID = 401;
	public static final int SUCCESS_ID = 200;
	public static final int FAILURE_ID = 320;
	public static final String UNAUTHORIZED = "Invalid credentials. Please try again.";
	public static final String PROCESS_FAIL = "Process failed, Please try again.";

	public enum userRole {
		SUPERADMIN, ORGADMIN, ENDUSER;
	}

	/**
	 * Indicators or Classifiers
	 */
	public static final String ROLE = "ROLE";
	public static final String ORG = "ORG";

	/**
	 * Allowed Origins for CORS Bean
	 */
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";
	public static final String OPTIONS = "OPTIONS";

	/**
	 * Qualifiers and Services
	 */
	public static final String USER_SERVICE = "userService";
	public static final String USER_DAO = "userDao";
	public static final String SUPER_ADMIN_SERVICE = "superAdminService";
	public static final String SUPER_ADMIN_DAO = "superAdminDao";
	public static final String TICKET_SERVICE = "ticketService";
	public static final String TICKET_DAO = "ticketDao";
	public static final String ROLE_ACTION_SERVICE = "roleActionService";
	public static final String APP_SERVICE = "appService";
	public static final String TAG_SERVICE = "tagService";
	public static final String ROLE_DAO = "roleDao";
	public static final String APP_DAO = "appDao";
	public static final String TAG_DAO = "tagDao";
	public static final String TIME_ZONE = "UTC";
	public static final String APPEND_SECONDS = ":00";
	public static final String HTTPS_ACCOUNTS_GOOGLE_COM_O_OAUTH2_TOKEN = "https://accounts.google.com/o/oauth2/token";
	public static final String CLIENT_SECRET = "client_secret";
	public static final String CLIENT_ID = "client_id";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String GRANT_TYPE = "grant_type";

	private static final List<Integer> superAdminActions = new ArrayList<>(Arrays.asList(1, 3, 4, 5, 6, 7, 8, 9, 10, 11,
			12, 15, 16, 17, 18, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42,
			43, 44, 45, 46, 47, 48, 49, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61));
	private static final List<Integer> orgAdminActions = new ArrayList<>(
			Arrays.asList(3, 4, 5, 6, 9, 11, 12, 15, 18, 23, 24, 25, 29, 30, 33, 34, 36, 38, 39, 40, 41, 42, 43, 44, 45,
					46, 47, 48, 49, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61));
	private static final List<Integer> endUserActions = new ArrayList<>(Arrays.asList(11, 12, 23, 24, 29, 30, 33, 34,
			36, 38, 39, 40, 41, 44, 45, 46, 47, 48, 49, 51, 52, 53, 54, 55, 56, 57, 58, 60, 61));

	private static Map<Integer, List<String>> actions = createMap();

	protected static Map<Integer, List<String>> createMap() {
		Map<Integer, List<String>> actions = new HashMap<>();
		actions.put(1, Arrays.asList("Get Apps", "Get All Applications", "/app/getAllApps"));
		actions.put(3, Arrays.asList("Get Roles", "Get All Roles", "/roles/getAllRoles"));
		actions.put(4, Arrays.asList("Add Update Apps", "Add Or Update App", "/apps/addUpdateApp"));
		actions.put(5, Arrays.asList("Get Apps", "Get Applications", "/apps/getApp"));
		actions.put(6, Arrays.asList("Create Update Helpdesk", "Create or Update a new Helpdesk",
				"/helpdesk/createUpdateHelpdesk"));
		actions.put(7, Arrays.asList("Fetch Helpdesk", "Get Helpdesk", "/helpdesk/getOrgHelpdesk"));
		actions.put(8, Arrays.asList("Check MDM", "Check MDM", "/roles/mdmCheck"));
		actions.put(9, Arrays.asList("Get Service Request", "Fetch Service Requests", "/apps/getServiceRequests"));
		actions.put(10,
				Arrays.asList("Get Priority Levels", "Get All Ticket Priority Levels", "/tickets/getPriorityLevels"));
		actions.put(11, Arrays.asList("Get Helpdesk", "Fetch Helpdesks", "/helpdesk/getHelpdesk"));
		actions.put(12, Arrays.asList("Add Update User", "Add Or Update User Details", "/user/createOrUpdate"));
		actions.put(15, Arrays.asList("Update Organization", "Update Organization", "/superadmin/updateOrgById"));
		actions.put(16, Arrays.asList("Add Org", "Add Organization", "/superadmin/addOrganization"));
		actions.put(17, Arrays.asList("Get Org By ID", "Get Org By ID", "/superadmin/getOrgById"));
		actions.put(18, Arrays.asList("Delete Org", "Delete Organization", "/superadmin/deleteOrganization"));
		actions.put(20,
				Arrays.asList("Get Template Version", "Get Version of Template", "/tickets/getTemplatesVersion"));
		actions.put(21, Arrays.asList("Configure Templates", "Configure Templates", "/tickets/configureTemplates"));
		actions.put(22, Arrays.asList("Get Templates", "Get Templates", "/tickets/getTemplates"));
		actions.put(23, Arrays.asList("Get All Users", "getAllUsers", "/user/getAllUsers"));
		actions.put(24, Arrays.asList("Get User By Id", "getUserById", "/user/getUser"));
		actions.put(25, Arrays.asList("Get Apps By Org", "getAppByOrgId", "/apps/getAppByOrgId"));
		actions.put(26, Arrays.asList("Add or Update Helpdesk Admins", "addOrUpdateHelpdeskAdmins",
				"/helpdesk/addUpdateHelpdeskAdmins"));
		actions.put(27, Arrays.asList("Get Helpdesk Admins", "getHelpdeskAdmins", "/helpdesk/getHelpdeskAdmins"));
		actions.put(28, Arrays.asList("Map Apps To Helpdesk", "mappAppsToHelpdesk", "/apps/mapAppToHelpdesk"));
		actions.put(29, Arrays.asList("Add Ticket", "addTicket", "/tickets/addTicket"));
		actions.put(30, Arrays.asList("Get Tickets", "getAllTickets", "/tickets/getAllTickets"));
		actions.put(32, Arrays.asList("Add Notes", "addNotes", "/tickets/addNotes"));
		actions.put(33, Arrays.asList("Add Update Updates", "addUpdateUpdates", "/tickets/addUpdateUpdates"));
		actions.put(34, Arrays.asList("Get Updates", "getUpdates", "/tickets/getUpdates"));
		actions.put(35, Arrays.asList("add admin", "addAdmin", "/superadmin/addAdmin"));
		actions.put(36, Arrays.asList("Update Ticket Basic", "updateTicketBasic", "/tickets/updateTicketBasic"));
		actions.put(37, Arrays.asList("Update Ticket Type", "updateTicketType", "/tickets/updateTicketType"));
		actions.put(38, Arrays.asList("Update Ticket Status", "updateTicketStatus", "/tickets/updateTicketStatus"));
		actions.put(39,
				Arrays.asList("Update Ticket Checklist", "updateTicketChecklist", "/tickets/updateTicketChecklist"));
		actions.put(40, Arrays.asList("Get Activity Logs Per Ticket", "getActivityLogs", "/tickets/getActivityLogs"));
		actions.put(41, Arrays.asList("Change Password", "changePassword", "/user/changePassword"));
		actions.put(42, Arrays.asList("Add Admin", "addAdmin", "/superadmin/addAdmin"));
		actions.put(43, Arrays.asList("Remove Admin", "removeAdmin", "/superadmin/removeAdmin"));
		actions.put(44, Arrays.asList("Upload Profile Picture", "uploadProfilePicture", "/user/uploadProfilePicture"));
		actions.put(45, Arrays.asList("Pin Ticket", "pinTicket", "/tickets/pinTicket"));
		actions.put(46, Arrays.asList("Get No Of Tickets", "getNoOfTickets", "/tickets/getNoOfTickets"));
		actions.put(47, Arrays.asList("Get Activity Logs Per User", "getActivityLogsPerUser",
				"/tickets/getActivityLogsPerUser"));
		actions.put(48, Arrays.asList("Get Tickets Count Per Month Per User", "getTicketsCountPerMonthPerUser",
				"/tickets/getTicketsCountPerMonthPerUser"));
		actions.put(49, Arrays.asList("Upload Ticket Attachments", "uploadAttachment", "/tickets/uploadAttachment"));
		actions.put(51, Arrays.asList("Add Update Tag", "addorUpdateTags", "/tags/addUpdateTag"));
		actions.put(52, Arrays.asList("Get All Ticket Tags", "getAllTicketTags", "/tags/getAllTicketTags"));
		actions.put(53, Arrays.asList("Get All Tags By Org Id", "getTagByOrgId", "/tags/getTagByOrgId"));
		actions.put(54, Arrays.asList("Get All Tags By Helpdesk Id", "getTagByHelpdeskId", "/tags/getTagByHelpdeskId"));
		actions.put(55, Arrays.asList("Get Feedback From grievance Sdk", "getFeedbackFromAuroraSdk",
				"/tickets/getFeedbackFromAuroraSdk"));
		actions.put(56, Arrays.asList("Get All Tickets V2", "getAllTicketsV2", "/tickets/getAllTicketsV2"));
		actions.put(57, Arrays.asList("Get All Org", "getAllOrg", "/superadmin/getAllOrg"));
		actions.put(58, Arrays.asList("Forgot Password", "forgotPassword", "/user/forgotPassword"));
		actions.put(59, Arrays.asList("Map Apps To Org", "mapAppsToOrg", "/superadmin/mapAppsToOrg"));
		actions.put(60, Arrays.asList("Get Reviews", "getReviews", "/user/getReviews"));
		actions.put(61, Arrays.asList("Send Reply To Reviews", "sendReplyToReviews", "/tickets/sendRepliesToReviews"));
		return actions;
	}

	public static List<Integer> getOrgadminactions() {
		return orgAdminActions;
	}

	public static List<Integer> getEnduseractions() {
		return endUserActions;
	}

	public static Map<Integer, List<String>> getActions() {
		return actions;
	}

	public static void setActions(Map<Integer, List<String>> actions) {
		Constants.actions = actions;
	}

	public static List<Integer> getSuperadminactions() {
		return superAdminActions;
	}

	public static String[] getExcludeFields() {
		return EXCLUDE_FIELDS;
	}

	public static String[] getIncludeFields() {
		return INCLUDE_FIELDS;
	}

	public class SMTP {
		private SMTP() {
			super();
		}

		public static final String HOST = "smtp.sendgrid.net";
		public static final int PORT = 465;
		public static final boolean SSL = true;
		public static final String USER = "apikey";
		public static final String PSWRD = "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";
		public static final String EMAIL = "shishir.suman@tarento.com";
		public static final String ALIAS = "grievance-desk.support";
	}

	public static final String HOST = "smtp.sendgrid.net";
	public static final String FROM = "shishir.suman@tarento.com";
	public static final String USER = "apikey";
	public static final String PSWRD = "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";
	public static final String LOGO_URL = "https://cabhound-static.s3.amazonaws.com/insuranceDoc/claim/tarento_logo.png";
	public static final String ALIAS = "auroradesk.support";

	public static final int MAX_EXECUTOR_THREAD = 10;
	public static final String HTTPHEADERANDSECURITY = null;
	public static final String USER_NAME_MISSING = "User name is mandatory.";
	public static final String NAME_MISSING = " Oops ! Name is mandatory.";
	public static final String PHONE_NUMBER_MISSING = "Phone number is mandatory.";
	public static final String ID_MISSING = " Oops ! Id is mandatory.";
	public static final String PSWRD_MISSING = "Oops! Password is Missing";
	public static final String PSWRD_MISMATCH = "New and Confirm passwords don't match.";
	public static final String PSWRD_SAME = "New and Old passwords cannot be same.";
	public static final int AUTH_TYPE = 1;
	public static final String PSWRD_REGENERATED = "Password regenerated";
	public static final String FORGOT_PSWRD_VM_FILE = "forgot-password.vm";
	public static final String NOT_A_CUSTOM_PSWRD = "Please visit GreenPine for Forgot Password Link";
	public static final int ADMIN_ID = 2;
	public static final String USERS = "user";
	public static final String GOOGLE_AUTH = "google";
	public static final String CUSTOM_AUTH = "custom";
	public static final String ADD_ADMIN_VM_FILE = "add-admin-grievance.vm";
	public static final String NEW_ADMIN = "You are added as an admin in grievance-Desk";
	public static final String NEW_HELPDESK_ADMIN = "You are added as a helpdesk admin in grievance-Desk";
	public static final String DELETE_HELPDESK_ADMIN = "Revoke of helpdesk admin access in grievance-Desk";
	public static final String COPIEDTO = "You are copied to a ticket in grievance-Desk";
	public static final String TICKETCREATION = "You have created a new ticket in grievance-Desk";
	public static final String REMOVEDFROMCOPIEDTO = "You are removed from a ticket in grievance-Desk";
	public static final String DELETE_ADMIN_VM_FILE = "remove_admin.vm";
	public static final String DELETE_ADMIN = "Revoke of admin access in grievance-Desk";
	public static final String HELPDESKTYPE_WITHOUTWORKFLOW = "Helpdesk Type cannot be created without workflow. Please add workflow and try again";
	public static final String HELPDESKTYPE_WORKFLOW_EMPTY = "Helpdesk Type Workflow cannot be empty. Please add valid workflow and try again";
	public static final int CAPACITY = 1024;
	public static final String PSWORD_REGENERATED = "Password regenerated";
	public static final String FORGOT_PSWORD_VM_FILE = "forgot-password.vm";
	public static final String STATUS_CHANGE = "Ticket Status Change!";
	public static final String UPDATES = "Here's what you missed while you were away!";
	public static final String A_1_0 = "1.0";
	public static final String SV = "sv";
	public static final String SAVE = "save";
	public static final String ID = "id";
	public static final String CC = "cc";
	public static final String TAGS2 = "tags";
	public static final String USER_EVENT = "userEvent";
	public static final String DEVICE_SCREEN_RESOLUTION = "deviceScreenResolution";
	public static final String DEVICE_LOCALE = "deviceLocale";
	public static final String DEVICE_MANUFACTURE = "deviceManufacture";
	public static final String REVIEW_ID = "reviewId";
	public static final String DEVICE_NAME = "deviceName";
	public static final String COUNTRY = "country";
	public static final String IP = "ip";
	public static final String DEVICE_TYPE = "deviceType";
	public static final String OS_TYPE = "osType";
	public static final String OS_VERSION = "osVersion";
	public static final String STATUS = "status";
	public static final String PRIORITY = "priority";
	public static final String TYPE_ID = "typeId";
	public static final String SOURCE_ID = "sourceId";
	public static final String HELPDESK_ID = "helpdeskId";
	public static final String APP_ID = "appId";
	public static final String UPDATE = "update";
	public static final String OPR = "opr";
	public static final String PINNED_TICKET = "pinnedTicket";
	public static final String ACTIVE = "active";
	public static final String APP_NAME = "appName";
	public static final String APP_VERSION = "appVersion";
	public static final String REQUESTED_BY = "requestedBy";
	public static final String UPDATED_TIME_TS = "updatedTimeTS";
	public static final String CREATED_TIME_TS = "createdTimeTS";
	public static final String U_T = "uT";
	public static final String C_T = "cT";
	public static final String UPDATED_TIME = "updatedTime";
	public static final String CREATED_TIME = "createdTime";
	public static final String MAX_RATING = "max-rating";
	public static final String RATE = "rate";
	public static final String DESCRIPTION = "description";

	private static final String[] INCLUDE_FIELDS = new String[] { ID, STATUS, TOTAL, DESCRIPTION, PINNED_TICKET,
			REQUESTED_BY, HELPDESK_ID, UPDATED_TIME, CREATED_TIME, U_T, C_T };
	private static final String[] EXCLUDE_FIELDS = new String[] { OS_VERSION, OS_TYPE, DEVICE_TYPE, DEVICE_LOCALE,
			DEVICE_SCREEN_RESOLUTION, DEVICE_MANUFACTURE, DEVICE_NAME, IP, USER_EVENT, SV, COUNTRY, SOURCE_ID, TYPE_ID,
			TYPE, PRIORITY, RATE, MAX_RATING, APP_VERSION, APP_NAME, APP_ID, FEEDBACK_BY_NAME, ACTIVE, CREATED_TIME_TS,
			UPDATED_TIME_TS };
}
