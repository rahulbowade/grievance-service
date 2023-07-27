package org.upsmf.grievance.util;

/**
 *
 * @author Darshan Nagesh
 *
 */

public final class PathRoutes {

	public static final String USER_ACTIONS_URL = "/user";
	public static final String SUPERADMIN_ACTIONS_URL = "/superadmin";
	public static final String TICKET_ACTIONS_URL = "/tickets";
	public static final String ROLE_ACTIONS_URL = "/roles";
	public static final String HELPDESK_URL = "/helpdesk";
	public static final String APPS_ACTIONS_URL = "/apps";
	public static final String TAGS_URL = "/tags";

	public final class UserRoutes {
		private UserRoutes() {
			super();
		}

		public static final String USER_ACTIONS_POST = "/getAllActions";
		public static final String EMPLOYMENT_TYPES_GET = "/getEmploymentTypes";
		public static final String NUMBER_OF_USERS_GET = "/getNumberOfUsers";
		public static final String NUMBER_OF_ROLES_GET = "/getNumberOfRoles";
		public static final String LIST_USER_GET = "/getAllUsers";
		public static final String USER_BY_ID_GET = "/getUser";
		public static final String CREATE_UPDATE_USER_POST = "/createOrUpdate";
		public static final String USER_ROLE_MAPPING_POST = "/mapUserToRole";
		public static final String SINGLE_FILE_UPLOAD_POST = "/upload";
		public static final String IMAGE_GET = "/images";
		public static final String LOGOUT_GET = "/logout";
		public static final String PUBLIC = "/public";
		public static final String CHANGE_PSWRD = "/changePassword";
		public static final String FORGOT_PSWRD = "/forgotPassword";
		public static final String LOGIN = "/login";
		public static final String GET_PROFILE_PICTURE = "/getProfilePicture";
		public static final String UPLOAD_PROFILE_PICTURE = "/uploadProfilePicture";
		public static final String GET_REVIEWS = "/getReviews";
	}

	public final class SuperAdminRoutes {
		private SuperAdminRoutes() {
			super();
		}

		public static final String GET_ALL_ORG = "/getAllOrg";
		public static final String UPDATE_ORG_BY_ID = "/updateOrgById";
		public static final String GET_ORG_BY_ID = "/getOrgById";
		public static final String DELETE_ORGANIZATION = "/deleteOrganization";
		public static final String ADD_ORGANIZATION = "/addOrganization";
		public static final String ADD_ADMIN = "/addAdmin";
		public static final String REMOVE_ADMIN = "/removeAdmin";
		public static final String MAP_APPS_TO_ORG = "/mapAppsToOrg";
		public static final String GET_AUTH_TYPES = "/getAuthTypes";
	}

	public final class RoleActionRoutes {
		private RoleActionRoutes() {
			super();

		}

		public static final String LIST_ROLES_GET = "/getAllRoles";
		public static final String ADD_ROLE_POST = "/addRole";
		public static final String ROLE_BY_ID_GET = "/getRoleById";
		public static final String UPDATE_ROLE_POST = "/updateRole";
		public static final String MDM_CHECK = "/mdmCheck";
	}

	public final class AppsRoutes {
		private AppsRoutes() {
			super();

		}

		public static final String CREATE_APP = "/addUpdateApp";
		public static final String GET_APP = "/getApp";
		public static final String GET_APP_BY_ORG_ID = "/getAppByOrgId";
		public static final String MAP_APP_TO_HELPDESK = "/mapAppToHelpdesk";
		public static final String GET_SERVICE_REQUEST = "/getServiceRequests";
	}

	public final class TagRoutes {
		private TagRoutes() {
			super();

		}

		public static final String CREATE_TAG = "/addUpdateTag";
		public static final String GET_TAG = "/getTag";
		public static final String GET_ALL_TAGS = "/getAllTags";
		public static final String GET_TAG_BY_ORG_ID = "/getTagByOrgId";
		public static final String GET_TAG_BY_HELPDESK_ID = "/getTagByHelpdeskId";
		public static final String REMOVE_TAG = "/removeTag";
		public static final String ADD_TICKET_TAG = "/addTicketTag";
		public static final String GET_ALL_TICKET_TAGS = "/getAllTicketTags";
		public static final String DELETE_TICKET_TAG = "/deleteTicketTag";
	}

	public final class TicketRoutes {

		private TicketRoutes() {
			super();

		}

		public static final String ADD_TICKET = "/addTicket";
		public static final String GET_ALL_TICKETS_BY_ORG = "/getAllTicketsByOrg";
		public static final String UPDATE_TICKET_BASIC = "/updateTicketBasic";
		public static final String UPDATE_TICKET_TYPE = "/updateTicketType";
		public static final String UPDATE_TICKET_STATUS = "/updateTicketStatus";
		public static final String UPDATE_TICKET_CHECKLIST = "/updateTicketChecklist";
		public static final String GET_ALL_TICKETS = "/getAllTickets";
		public static final String GET_ALL_TICKETS_PER_USER = "/getAllTicketsPerUser";
		public static final String GET_ALL_TICKETS_ASSIGNED_TO_ME = "/getAllTicketsAssignedToMe";
		public static final String GET_ALL_TICKETS_TO_BE_APPROVED = "/getAllTicketsToBeApproved";
		public static final String GET_TICKET_DETAILS_PER_TICKET = "/getTicketDetailsPerTicket";
		public static final String GET_PRIORITY_LEVELS = "/getPriorityLevels";
		public static final String GET_TEMPLATES_VERSION = "/getTemplatesVersion";
		public static final String GET_TEMPLATES = "/getTemplates";
		public static final String GET_NO_OF_TICKETS = "/getNoOfTickets";
		public static final String CONFIGURE_TEMPLATE = "/configureTemplates";
		public static final String ADD_NOTES = "/addNotes";
		public static final String ADD_UPDATE_UPDATES = "/addUpdateUpdates";
		public static final String GET_UPDATES = "/getUpdates";
		public static final String GET_ACTIVITY_LOGS = "/getActivityLogs";
		public static final String GET_ACTIVITY_LOGS_PER_USER = "/getActivityLogsPerUser";
		public static final String PINNED_TICKET = "/pinTicket";
		public static final String GET_TICKET_COUNT_PER_MONTH_PER_USER = "/getTicketsCountPerMonthPerUser";
		public static final String GET_FEEDBACK_FROM_AURORA_SDK = "/getFeedbackFromAuroraSdk";
		public static final String UPLOAD_ATTACHMENT = "/uploadAttachment";
		public static final String SEND_REPLY_TO_REVIEWS = "/sendRepliesToReviews";

	}

	public final class HelpdeskRoutes {

		private HelpdeskRoutes() {
			super();

		}

		public static final String CREATE_UPDATE_HELPDESK = "/createUpdateHelpdesk";
		public static final String GET_ORG_HELPDESK = "/getOrgHelpdesk";
		public static final String CREATE_UPDATE_HELPDESK_TYPE = "/createUpdateHelpdeskType";
		public static final String ADD_UPDATE_HELPDESK_ADMINS = "/addUpdateHelpdeskAdmins";
		public static final String GET_HELPDESK_ADMINS = "/getHelpdeskAdmins";
		public static final String GET_HELPDESK = "/getHelpdesk";
		public static final String CONFIGURE_HELPDESK = "/configureHelpdesk";

		public static final String GET_PERFORMANCE_WITH_ACCESSCONTROL = "/getPerformanceWithAccessControl";
		public static final String GET_PERFORMANCE_WITHOUT_ACCESSCONTROL = "/getPerformanceWithoutAccessControl";
	}

}
