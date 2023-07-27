package org.upsmf.grievance.util;

/**
 * This interface will hold all the SQL Queries which are being used by the
 * application Internally, the inner interface will have the queries separated
 * based on the functionalities that they are associated with
 *
 * @author Darshan Nagesh
 *
 */
public final class Sql {

	public static final String ID = "id";

	/**
	 * All the queries associated with the Common activities or transactions will be
	 * placed here
	 *
	 * @author Darshan Nagesh
	 *
	 */
	public final class Common {
		private Common() {
			super();

		}

		public static final String VERIFY_PSWRD = "SELECT id FROM user WHERE password = ? AND name = ? ";
		public static final String WHERE_CLAUSE = " WHERE ";
		public static final String AND_CONDITION = " AND ";
		public static final String OR_CONDITION = " OR ";
		public static final String OPEN_BRACE = "(";
		public static final String CLOSE_BRACE = ")";
		public static final String CHECK_EMAIL_IN_USE = "SELECT id FROM user  WHERE username=? AND is_active IS TRUE";
		public static final String CHECK_OLD_PSWRD = "SELECT pwd FROM password WHERE user_id=?";
		public static final String UPDATE_PSWRD = "UPDATE password SET pwd=?,updated_date=? WHERE user_id=? and pwd=?";
		public static final String CHECK_USER_BY_USERNAME = "SELECT id from user where username=?";
		public static final String SAVE_FORGOT_PSWRD = "UPDATE password SET pwd=? , updated_date=? WHERE user_id= ? ";
		public static final String GET_USER_DETAIL_BY_EMAIL = "Select id, name, username, phone, is_active as isActive, created_date as createdDate, updated_date as updatedDate, img_path as imagePath from user where username=?";
		public static final String GET_ORG_ID_BY_USER_ID = "SELECT org_id FROM user JOIN user_org where user.id=user_org.user_id and user.id=?";
		public static final String GET_ALL_USERS_BY_ORG = "Select user.id, name, username, phone, is_active, img_path, created_date, updated_date from user JOIN user_org where user.id=user_org.user_id and user.is_active is true and user_org.org_id=?";
		public static final String GET_IMAGE_PATH = "SELECT img_path from user where id=?;";
		public static final String ORGADMIN = "ORGADMIN";
		public static final String ENDUSER = "ENDUSER";
		public static final String SUPER_ADMIN = "SUPERADMIN";
	}

	public final class Tags {
		private Tags() {
			super();

		}

		public static final String SAVE_TICKET_TAG = "INSERT INTO tag_ticket(ticket_id,tag_id) values(?,?)";
		public static final String GET_ALL_TAGS = "SELECT id as id, tag_name as name, created_by as createdBy FROM tag";
		public static final String GET_ALL_TAG_BY_ORGANISATION = "select t.id as id, t.tag_name as name from tag t where t.org_id = ?";
		public static final String GET_ALL_TAG_BY_HELPDESK = "SELECT distinct t.id as id, t.tag_name as name FROM tag t, tag_ticket tt where tt.ticket_id in (select ticket_id from ticket,helpdesk_ticket where helpdesk_id=? and ticket.id=ticket_id and active is true) and t.id=tt.tag_id order by id;";
		public static final String GET_TAG_BY_NAME = "SELECT t.id as id, t.tag_name as name FROM tag t where t.tag_name = ? and t.org_id = (select org_id from user_org where user_id = ?)";
		public static final String TAG_ID = "id";
		public static final String SAVE_TAG = "INSERT INTO tag(tag_name, created_by, org_id) values(?, ?, ?)";
		public static final String REMOVE_TAG = "DELETE FROM tag_ticket WHERE ticket_id=? AND tag_id=?";
		public static final String DELETE_TICKET_TAGS = "DELETE FROM tag_ticket WHERE ticket_id=?";
		public static final String GET_ALL_TICKET_TAGS = "select t.id as id, t.tag_name as name, created_by as createdBy from tag t, tag_ticket tt where tt.tag_id=t.id and tt.ticket_id=? ";

	}

	public static final String SAVE_LINK_TAG = "INSERT INTO tt_tag_link(link_id,tag_id) values(?,?)";

	public final class Organization {
		private Organization() {
			super();

		}

		public static final String GET_ALL_ORG = "SELECT id, org_name as orgName, url as url, logo as logo, domain as emailDomain, color as orgColor,  "
				+ " created_by as createdBy, created_date as createdDate, is_active as isActive, description as orgDescription FROM organization where is_active is TRUE";
		public static final String GET_ORG_BY_NAME = "SELECT id FROM organization where org_name=? and is_active=1";
		public static final String GET_ORG_BY_ID = "select id, org_name as orgName, url as url, logo as logo, domain as emailDomain, color as orgColor,   created_by as createdBy, created_date as createdDate, is_active as isActive, description as orgDescription FROM organization where id = ? and is_active is true";
		public static final String AND_CONDITION = " AND ";
		public static final String OR_CONDITION = " OR ";
		public static final String OPEN_BRACE = "(";
		public static final String CLOSE_BRACE = ")";
		public static final String ADD_NEW_ORG = "INSERT INTO organization (org_name, url, logo, created_by, updated_by, description, color, domain) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		public static final String NEW_ORG_ADMIN_USER = "INSERT INTO user (name, username, phone, img_path) VALUES (?,?,?,?)";
		public static final String DELETE_ORG = "UPDATE organization SET is_active = FALSE , updated_by = ? , updated_date = ? WHERE id = ?";
		public static final String GET_ADMIN_BY_ORG = "SELECT DISTINCT a.id,a.name,a.username,a.phone FROM user a,organization b,roles c, user_org d, user_role e "
				+ "WHERE a.id = d.user_id AND a.id = e.user_id " + "AND c.id = ? AND b.id = ? AND a.is_active IS TRUE";
		public static final String GET_ROLE_ID_BY_ORG = "SELECT id FROM roles where org_id = ? and role_name = ?";
		public static final String ADD_ORG_ROLES = "insert into roles(role_name, org_id) values (?,?)";
		public static final String ADD_ROLE_PERMISSION = "insert into role_action(role_id, action_id) values (?,?)";
		public static final String NEW_ORG_ADMIN_ROLE = "INSERT INTO user_role(user_id,role_id) VALUES (?,?)";
		public static final String NEW_ORG_ADMIN_PSWRD = "INSERT INTO password (pwd, user_id) VALUES (?,?)";
		public static final String FIRST_ADMIN_COMP = "INSERT INTO user_org (org_id,user_id) VALUES (?,?)";
		public static final String UPDATE_ORG = "UPDATE organization SET org_name = ?,logo = ?,updated_date = ?,updated_by = ?,url = ?,description = ?,color = ?, domain = ? WHERE id = ?";
		public static final String GET_ORG_BY_USERID = "select user_org.org_id as id, logo from user, user_org, organization where user.id=user_org.user_id and user_org.org_id=organization.id and user.id= ?";
		public static final String CHECK_IF_ROLE_EXISTS = "select exists(select id from roles where role_name = ? and org_id = ?)";
		public static final String CHECK_IF_ACTION_EXISTS = "SELECT exists(select id from action where id=?);";
		public static final String GET_ROLE_ID_FROM_ORG = "select id from roles where role_name = ? and org_id = ?";
		public static final String DELETE_ACTION = "delete from role_action where role_id=?";

		public static final String ORG_BY_ID = "select organization.id as id, organization.org_name as orgName, organization.url, organization.logo, organization.domain as domain, organization.color as color,organization.created_by as createdBy, organization.created_date as createdDate, organization.is_active as isActive, organization.description as description, user.id as userId, user.name, user.username, user.phone from organization, roles, user, user_org, user_role where user.id = user_org.user_id and user.id = user_role.user_id and user_org.org_id = organization.id and organization.id = roles.org_id and organization.is_active = ? and user.is_active = ? and organization.id = ? and roles.id = (select id from roles where org_id = ? and role_name = ?)";
	}

	public final class Helpdesk {

		private Helpdesk() {
			super();

		}

		public static final String CREATE_HELPDESK = "insert into helpdesk(helpdesk_name, org_id, created_by, created_date, is_active, color, description) values(?, ?, ?, ?,?,?,?)";
		public static final String UPDATE_HELPDESK = "update helpdesk set helpdesk_name = ?, updated_by = ?, updated_date = ?, is_active = ?, color = ?, description = ? where id = ? and org_id = ?";
		public static final String GET_ORG_HELPDESK = "select id, helpdesk_name as name, color from helpdesk where org_id = ? and is_active = ?";
		public static final String DELETE_WORKFLOW_FOR_HELPDESKTYPE = "delete FROM workflow_stage where type_id=?";
		public static final String DELETE_TYPE_FOR_HELPDESK = " update helpdesk_type set is_active=false WHERE helpdesk_id = ? ";
		public static final String DELETE_CHECKLIST_FOR_HDTYPE = " Update checklist set is_active=false WHERE id in (select checklist_id from helpdesk_checklist where helpdesk_id = ? and helpdesk_type_id = ? ) ";
		public static final String DELETE_CHECKLIST_HD_MAP = " DELETE from helpdesk_checklist WHERE helpdesk_id = ? ";
		public static final String INSERT_TYPE_FOR_HELPDESK = " INSERT INTO helpdesk_type (helpdesk_id, name) values (?,?)";
		public static final String INSERT_WORKFLOW_FOR_HELPDESK_TYPE = " INSERT INTO workflow_stage (name, type_id) VALUES (?, ?) ";
		public static final String INSERT_CHECKLIST_FOR_HDTYPE = " INSERT INTO checklist (name) values (?)";
		public static final String MAP_CHECKLIST_HDTYPE = " INSERT INTO helpdesk_checklist (helpdesk_id, checklist_id, helpdesk_type_id) values (?, ?, ?) ";
		public static final String GET_HELPDESK_FOR_ID = " SELECT hlpdsk.id as id, hlpdsk.helpdesk_name as name, hlpdsk.is_active as isActive, hlpdsk.color as color, hlpdsk.allow_all_users, hlpdsk.description as description, hlpdsk.org_id as orgId, "
				+ " hlpdsktyp.id as helpdeskTypeId, hlpdsktyp.name as helpdeskType, "
				+ " ws.id as workflowStageId, ws.name as workflowStage "
				+ " FROM helpdesk hlpdsk LEFT JOIN helpdesk_type hlpdsktyp ON hlpdsk.id = hlpdsktyp.helpdesk_id "
				+ " LEFT JOIN workflow_stage ws ON hlpdsktyp.id = ws.type_id "
				+ " WHERE hlpdsk.id = ? AND hlpdsk.org_id = ? and hlpdsktyp.is_active is true";
		public static final String GET_HELPDESK_BY_ID = " SELECT hlpdsk.id as id,  hlpdsk.allow_all_users as allowAllUsers, hlpdsk.org_id as orgId FROM helpdesk hlpdsk WHERE hlpdsk.id = ? and hlpdsk.is_active is true";

		public static final String GET_CHECKLIST_FOR_HELPDESK = " SELECT cl.id as checklistId, cl.name as itemName, hcl.helpdesk_id as helpdeskId from helpdesk_checklist hcl "
				+ " LEFT JOIN checklist cl ON hcl.checklist_id= cl.id "
				+ " WHERE hcl.helpdesk_id = ? AND hcl.helpdesk_type_id = ? and cl.is_active is true";
		public static final String GET_CHECKLIST_FOR_TICKET = "SELECT c.id as id, name , checked from ticket_checklist tcl, checklist c where c.id=tcl.checklist_id and ticket_id=? and c.is_active is true;";
		public static final String GET_WORKFLOW_FOR_TICKET = "SELECT t.id, workflow_id as workFlowId, name, time, status FROM ticket_workflow t, workflow_stage w where ticket_id=? and w.id=t.workflow_id;";
		public static final String GET_WORKFLOW_FOR_TICKET_LIST = "SELECT t.id, ticket_id as ticketId, workflow_id as workFlowId, name, time, status FROM ticket_workflow t, workflow_stage w where w.id=t.workflow_id and ticket_id IN ";

		public static final String GET_HELPDESK_BY_USER_ID = "SELECT hlpdsk.id as id, hlpdsk.helpdesk_name as name, hlpdsk.is_active as isActive, hlpdsk.color as color from helpdesk hlpdsk where id IN (SELECT distinct id FROM helpdesk AS h LEFT JOIN helpdesk_admin AS ha ON h.id=ha.helpdesk_id LEFT JOIN helpdesk_users AS hu ON h.id = hu.helpdesk_id where is_active is true and (ha.user_id=? or hu.user_id=?)) or allow_all_users is true and is_active is true and org_id=?";
		public static final String GET_HELPDESK_USER_BY_ID = " SELECT id as id, name as name, username as userName, img_path from user where id IN (select user_id from helpdesk_users where helpdesk_id = ? )  ";
		public static final String DELETE_CHECKLIST_FOR_TICKET = "DELETE from ticket_checklist WHERE ticket_id = ?";
		public static final String GET_HELPDESK_ADMIN_BY_ID = " SELECT id as id, name as name, username as userName, img_path from user where id IN (select user_id from helpdesk_admin where helpdesk_id = ? )  ";
		public static final String GET_APP_ORG_ID = "SELECT org_id FROM organization_app where app_id=?;";
		public static final String GET_USER_DETAILS_FOR_HELPDESK = "SELECT user_id as id, name as name, username as userName, img_path from user, user_org where is_active is true and user.id=user_org.user_id and org_id=?";

		public static final String GET_HELPDESK_ADMIN_USER = "select distinct user.id, name as name, username, img_path as imagePath"
				+ " from user, helpdesk_admin " + "where user.id = helpdesk_admin.user_id"
				+ " and helpdesk_admin.helpdesk_id = ?  and user.is_active = true";
		public static final String UPDATE_HELPDESK_CHANNELS = "update helpdesk set direct = ?, playstore = ?, appstore = ?, aurora_sdk = ? where id = ? and org_id = ?";
	}

	public final class RoleAction {
		private RoleAction() {
			super();

		}

		public static final String GET_ALL_ROLES = "SELECT id as id, role_name as name, org_id as orgId FROM roles";
		public static final String ORG_ID_CONDITION = " org_id = ? ";
		public static final String SELECT_ROLES_ON_ID = "SELECT role_name FROM roles WHERE id= ? and org_id = ?";
		public static final String SELECT_ROLE_ACTIONS_ON_ROLEID = "SELECT action_id as actionId, role_id as roleId FROM role_action WHERE role_id=?";
		public static final String SELECT_ACTIONS_ON_ID = "SELECT * FROM action WHERE id=?";
		public static final String SAVE_NEW_ROLE = "INSERT INTO roles (role_name, org_id) VALUES (?,?)";
		public static final String UPDATE_ROLE = "UPDATE roles SET role_name = ? WHERE id = ? and org_id=?";
		public static final String GET_ALL_ACTIONS_FOR_ROLES = "select role.id as roleId, act.id as actionId, role.role_name as roleName,  "
				+ "act.name as actionName, act.display_name as displayName, act.url as actionUrl "
				+ "from role_action ra LEFT JOIN roles role ON ra.role_id = role.id "
				+ "LEFT JOIN action act ON act.id = ra.action_id " + "where role.id IN ";
	}

	public final class Apps {
		private Apps() {
			super();

		}

		public static final String INSERT_NEW_APP = "INSERT into application (app_name, url, logo, sr_source_id, created_by, is_active, client_name, version, app_key, description)  values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		public static final String UPDATE_APP = "UPDATE application SET app_name=?, url=?, logo=?, sr_source_id=?, is_active=?, client_name=?, version=?,"
				+ " updated_by=?, updated_date=?, app_key=?, description=? where id=?";
		public static final String GET_APP = "SELECT id as id, app_name as name, description, url as appUrl, logo as logo, created_by as createdBy, created_date as createdDate, "
				+ " is_active as activeStatus,  client_name as clientName, version, sr_source_id as sourceId, app_key as appKey FROM application";
		public static final String ID_CONDITION = " id = ? ";
		public static final String GET_ID_FROM_ORG_APP = "select exists (select id from organization_app where app_id = ? and org_id = ?)";
		public static final String MAP_APP_TO_ORG = "insert into organization_app(app_id, org_id) values(?,?);";
		public static final String DELETE_ORG_APP = "delete from organization_app where app_id = ? and org_id = ?";
		public static final String GET_ORG_APPS = "select application.id as id,application.app_name as name, application.url as appUrl, application.description as description, application.logo,   application.client_name as clientName, application.version,  application.sr_source_id as sourceId,  application.app_key as appKey, application.is_active as activeStatus from organization_app, application where application.id = organization_app.app_id and organization_app.org_id = ?  and application.is_active is true";
		public static final String GET_APP_URLS = "select distinct application.url as appUrl from organization_app, application where application.id = organization_app.app_id and organization_app.org_id = ?  and application.is_active is true";

		public static final String MAP_HELPDESK_APP = "insert into helpdesk_app(helpdesk_id, app_id) values (?,?)";
		public static final String UPDATE_HELPDESK_APP = "update helpdesk_app set is_active = ? where helpdesk_id = ? and app_id = ?";
		public static final String GET_SOURCE = " SELECT id, name FROM source ";
		public static final String GET_HELPDESK_ID = "select exists(select id from helpdesk_app where helpdesk_id=? and app_id = ?)";
		public static final String GET_APP_ID_APP_KEY = "select id FROM application where url = ? and is_active is true;";
		public static final String CHECK_IF_APP_NAME_EXISTS = "select exists(select id FROM application where url = ? and is_active is true);";
		public static final String CHECK_IF_TICKET_EXIST = "select exists(select id from ticket where description = ? and active is true and requested_by in (select id from user where username=? and is_active is true) LIMIT 1);";
		public static final String CHECK_IF_TICKET_EXISTS = "select exists(select id from ticket where reviewId = ? and active is true and requested_by in (select id from user where username=? and is_active is true) LIMIT 1);";
		public static final String GET_APP_ID_FROM_APP_KEY = "select id FROM application where app_key = ? and is_active is true;";

		public static final String CHECK_IF_UPDATE_EXISTS = "select exists(select id from ticket_updates where updates = ? and active is true and created_date=? limit 1);";
		public static final String GET_REQUESTED_BY = "select requested_by from ticket where id=? and active is true";
		public static final String CHECK_GET_APP_ID_FROM_APP_KEY = "select exists(select id FROM application where app_key = ? and is_active is true);";
		public static final String GET_ORG_ID_FROM_APP_ID = "SELECT org_id as orgId FROM organization_app where app_id=?;";
		public static final String GET_HELPDESK_ID_FROM_APP_ID = "select helpdesk_id from helpdesk_app where app_id = ? and is_active is true LIMIT 1";

	}

	public final class UserQueries {
		private UserQueries() {
			super();

		}

		public static final String SELECT_USER_BY_TOKEN = "SELECT \"user\".id as id FROM \"user\",\"user_authentication\" WHERE auth_token = ? and \"user\".is_active is true and \"user\".id=user_authentication.user_id;";
		public static final String SELECT_USER_ON_USERNAME = "SELECT user.id as id, user.name as name, user.username as username, user_org.org_id as orgId, "
				+ " user.is_active as isActive, pwd.pwd as password from user, user_org, password pwd where "
				+ " user.id = user_org.user_id and pwd.user_id = user.id and username = ?";
		public static final String SELECT_USER_ROLES_ON_USERNAME = "SELECT usr.id as id, usr.name as name, usr.username as username, usr.is_active as isActive, "
				+ " usrorg.org_id as orgId, " + " pwd.pwd as password, "
				+ " role.role_name as roleName, role.id as roleId, role.org_id as roleOrgId "
				+ " from user usr LEFT JOIN user_org usrorg ON usr.id = usrorg.user_id  "
				+ " LEFT JOIN password pwd ON pwd.user_id = usr.id "
				+ " LEFT JOIN user_role usrrole ON usr.id = usrrole.user_id  "
				+ " LEFT JOIN roles role ON usrrole.role_id = role.id " + Common.WHERE_CLAUSE
				+ " role.org_id = usrorg.org_id and usr.username = ?";
		public static final String SELECT_USER_ROLES_ACTIONS_ON_USERNAME = "SELECT usr.id as id, usr.name as name, usr.username as username, usr.is_active as isActive, "
				+ " usrorg.org_id as orgId, " + " pwd.pwd as password, "
				+ " role.role_name as roleName, role.id as roleId, role.org_id as roleOrgId, "
				+ " actn.id as actionId, actn.name as actionName, actn.display_name as actionDisplayName, actn.query_params as actionQueryParams, "
				+ " actn.service_code as actionServiceCode, actn.url as actionUrl "
				+ " from user usr LEFT JOIN user_org usrorg ON usr.id = usrorg.user_id  "
				+ " LEFT JOIN password pwd ON pwd.user_id = usr.id "
				+ " LEFT JOIN user_role usrrole ON usr.id = usrrole.user_id "
				+ " LEFT JOIN roles role ON usrrole.role_id = role.id "
				+ " LEFT JOIN role_action rolactn ON role.id = rolactn.role_id "
				+ " LEFT JOIN action actn ON rolactn.action_id = actn.id " + Common.WHERE_CLAUSE
				+ " role.org_id = usrorg.org_id and usr.username = ? ";
		public static final String MAP_USER_TO_ROLE = "INSERT INTO user_role (user_id, role_id) VALUES (?, ?)";
		public static final String REMOVE_USER_ROLE_MAP = "DELETE FROM user_role WHERE user_id = ? ";

		public static final String UPDATE_USER_PROFILE_PROFILE_IMAGE = "UPDATE user SET img_path = ? WHERE id = ?";

		public static final String GET_USER_ACTIONS = "Select action.id, action.display_name as displayName, name, url from action inner join role_action on role_action.action_id = action.id where role_action.role_id = ?";
		public static final String USER_PROFILE_FETCH = "select * from user usr where id=? ";
		public static final String USER_DATA = "select COUNT(*) from user where username=?";
		public static final String USER = "select * from user where id=?";
		public static final String GET_USER_AUTH_DETAILS = "SELECT id, user_id as userId, auth_token FROM user_authentication WHERE id=?";
		public static final String SAVE_USER = "INSERT INTO user(name,username,phone,img_path) VALUES (?,?,?,?)";
		public static final String SAVE_ANONYMOUS_USER = "INSERT INTO user(name,username,phone,img_path,is_anonymous) VALUES (?,?,?,?,?)";
		public static final String SAVE_USER_AUTHENTICATION = "INSERT INTO user_authentication(user_id,auth_token) VALUES (?,?)";
		public static final String GET_USER_ROLE = "SELECT user_id, role_id FROM retail_user_role WHERE user_id=?";
		public static final String GET_ROLES_FOR_USER = "select ur.role_id as roleId, r.role_name as roleName from roles r, user_role ur where r.id = ur.role_id and ur.user_id = ?";
		public static final String USER_ACTIVE_CONDITION = " WHERE usr.is_active = ? ";
		public static final String WHERE_CLAUSE = " WHERE ";
		public static final String AND_CONDITION = " AND ";
		public static final String OR_CONDITION = " OR ";

		public static final String UPDATE_USER = "UPDATE user SET name = ?,username = ?, phone = ?, is_active = ?, img_path = ? where id = ? ";
		public static final String GET_USER_COUNT = "SELECT count(*) FROM user usr";
		public static final String GET_USER_COUNT_ON_ACTIVE_STATUS = "SELECT count(*) FROM user usr where usr.is_active = ? ";
		public static final String GET_USER_COUNT_FOR_ROLE = "SELECT count(*) FROM user usr LEFT JOIN user_role ur ON usr.id = ur.user_id where ur.role_id = ? and usr.is_active IS TRUE";
		public static final String GET_ROLE_COUNT = "SELECT count(*) FROM roles";

		public static final String INVALIDATE_TOKEN = "DELETE from user_authentication WHERE auth_token = ? ";
		public static final String FETCH_AUTH_TOKEN_REF = "SELECT id FROM user_authentication WHERE auth_token = ? ";
		public static final String CHECK_USER_DEVICE_TOKEN = "SELECT COUNT(*) FROM user_device WHERE user_id = ? AND device_token = ? ";
		public static final String INSERT_PD = "insert into password(pwd, user_id) values(?,?)";
		public static final String INSERT_ACTION = "INSERT INTO action(`id`,`name`,`display_name`,`url`) VALUES(?,?,?,?);";
		public static final String UPDATE_ACTION = "UPDATE action set name=?, display_name=?, url=? where id=?";
		public static final String GET_AUTH_TYPE_ID = "select aut_id from organization_auth where org_id = ?";
		public static final String CHECK_FIRST_ADMIN = "SELECT org_id FROM user_org WHERE user_id=?";
		public static final String GET_MASTER_DATA_CHECK = "SELECT COUNT(user_id) as id FROM user_org WHERE org_id = ? ";
		public static final String NEW_ORG_AUTH = "Insert into organization_auth(org_id, aut_id, base_url, email_domain) values (?,?,?,?)";
		public static final String GET_ROLE_ID_BY_ORG = "SELECT id FROM roles where org_id = ? and role_name = ?";
		public static final String ADD_ADMIN = "UPDATE user_role SET role_id = ? WHERE user_id = ?";
		public static final String DELETE_ADMIN = "UPDATE user_role SET role_id = ? WHERE user_id = ?";
		public static final String MAP_USER_TO_ORG = "Insert into user_org(user_id,org_id) values (?,?)";
		public static final String GET_HELPDESK_ADMINS = "select user_id from helpdesk_admin where helpdesk_id=? ";
		public static final String GET_TICKET_CC = "select user_id from ticket_cc where ticket_id=? ";
		public static final String GET_TICKET_ATTACHMENT = "select url from ticket_attachment,attachment_url where ticket_id=? and ticket_attachment.attachment_id=attachment_url.id;";
		public static final String REMOVE_ALL_HELPDESK_ADMIN = "DELETE FROM helpdesk_admin where helpdesk_id=?";
		public static final String REMOVE_ALL_USERS_FROM_HELPDESK = "DELETE FROM helpdesk_users where helpdesk_id=?";
		public static final String REMOVE_ALL_TICKET_CC = "DELETE FROM ticket_cc where ticket_id=?";
		public static final String REMOVE_ALL_HELPDESK_SOURCE = "DELETE FROM helpdesk_source where helpdesk_id=?";
		public static final String ADD_ADMINS_TO_HELPDESK = "insert into helpdesk_admin(helpdesk_id,user_id) values (?,?)";
		public static final String ADD_USERS_TO_HELPDESK = "insert into helpdesk_users(helpdesk_id,user_id) values (?,?)";
		public static final String ADD_CC_TO_TICKET = "insert into ticket_cc(ticket_id,user_id) values (?,?)";
		public static final String GET_USER_ORG_MAP = "SELECT user_org.id as id , user_id as userId , org_id as orgId FROM \"user\", \"user_org\" where \"user\".id=user_org.user_id and \"user\".is_anonymous is false;";
		public static final String GET_USER_ROLE_MAP = "SELECT id as id, user_id as userId, role_id as roleId FROM user_role ";
		public static final String GET_APP_ID_HELPDESK_ID = "select helpdesk_id as helpdeskId,app_id as appId from helpdesk_app where is_active is true;";
		public static final String GET_APP_ID_APP_OBJECT = "SELECT id,app_name as name,logo,url FROM application;";
		public static final String GET_HELPDESK_ID_HELPDESK_OBJECT = "SELECT id,helpdesk_name as name,org_id as orgId FROM helpdesk where is_active is true ";
		public static final String GET_USER_ID_AND_USER_NAME = "SELECT id,name FROM \"user\" where is_active is true;";
		public static final String UPDATE_ALLOW_ALL_USERS = "UPDATE helpdesk SET allow_all_users = ? WHERE id = ?";
		public static final String REMOVE_ALL_TICKET_ATTACHMENT = "DELETE FROM ticket_attachment where ticket_id=?";
		public static final String GET_HELPDESK_CHANNELS = "SELECT direct, playstore, appstore, aurora_sdk FROM helpdesk where id=? and is_active is true ";

		public static final String QUERY1 = "SELECT toa.aut_id as id, toa.base_url as name, auth_name as description from organization_auth toa, ";
		public static final String QUERY2 = " authentication auth WHERE toa.aut_id = auth.id AND  toa.org_id = ";
		public static final String QUERY3 = " AND toa.is_active IS TRUE ";
	}

	public final class Ticket {
		private Ticket() {
			super();

		}

		public static final String ADD_SOURCE_TO_HELPDESK = "INSERT INTO helpdesk_source(helpdesk_id, source_id) VALUES (?,?);";
		public static final String ADD_TICKET = "insert into ticket(created_time, rate, max_rating, priority, requested_by, description, type, updated_time, pinned_ticket) values(?,?,?,?,?,?,?,?,?);";
		public static final String ADD_TICKET_TO_HELPDESK = "INSERT INTO helpdesk_ticket(ticket_id, sr_source_id, helpdesk_id, app_id) VALUES (?,?,?,?);";
		public static final String UPDATE_TICKET = "update ticket set priority=?, notes=?, active=?, updated_time=? where id=?";
		public static final String UPDATE_TICKET_REVIEW_ID = "update ticket set reviewId=? where id=?";
		public static final String GET_ALL_TICKETS = "SELECT t.id, t.created_time as createdTime, t.updated_time as updatedTime, t.rate, t.max_rating as maxRating, t.priority, requested_by as requestedBy, description, notes ,  pinned_ticket as pinnedTicket from ticket t where requested_by = ? and active is true;";
		public static final String GET_TICKET_BY_ID = "select t.id, pinned_ticket as pinnedTicket, created_time as createdTime, updated_time as updatedTime, rate, max_rating as maxRating, priority, requested_by as requestedBy, description, notes, type, sr_source_id as sourceId, helpdesk_id as helpdeskId, app_id as appId, active from ticket t, helpdesk_ticket ht where t.id = ? and t.id=ht.ticket_id and  active is true;";
		public static final String GET_ALL_TICKETS_BY_HELPDESK_ID = "select t.id as id, pinned_ticket as pinnedTicket, created_time as createdTime, updated_time as updatedTime, rate, max_rating as maxRating,  priority, requested_by as requestedBy, description from ticket t, helpdesk_ticket ht where t.active is true and ht.helpdesk_id=? and t.id=ht.ticket_id order by t.id desc;";
		public static final String GET_ALL_TICKETS_BY_APP_ID = "select t.id as id, pinned_ticket as pinnedTicket, created_time as createdTime, updated_time as updatedTime, rate, max_rating as maxRating, priority, requested_by as requestedBy, description from ticket t, helpdesk_ticket ht where t.active is true and ht.app_id=? and t.id=ht.ticket_id order by t.id desc;";
		public static final String UPDATE_STATUS = "update tt_ticket set status=? where id=?";
		public static final String CLOSE_TICKET = "update tt_ticket set closed_by=?,closed_time=current_timestamp,is_closed=true where id=?;";
		public static final String APPROVE_TICKET = "update tt_ticket set updated_by=?,updated_time=current_timestamp,is_approved=true where id=?;";
		public static final String GET_VERSION_FOR_TEMPLATES = "SELECT version FROM template_version ";
		public static final String UPDATE_VERSION_TIMESTAMP = " UPDATE template_version SET version = ? ";
		public static final String UPDATE_NOTES_TO_TICKETS = "update ticket set notes=? where id=?";
		public static final String UPDATE_UPDATES = "update ticket_updates set updates=?, active=?, created_date=? where id=?";
		public static final String ADD_UPDATES = "insert into ticket_updates(updates,created_by,ticket_id,created_date) values(?,?,?,?);";
		public static final String INSERT_TYPE_FOR_TICKETS = " INSERT INTO ticket_details (ticket_id, info_type) values (?,?)";
		public static final String INSERT_WORKFLOW_FOR_TICKET_TYPE = " INSERT INTO ticket_workflow (workflow_id, ticket_id) VALUES (?, ?) ";
		public static final String INSERT_CHECKLIST_FOR_TICKET_TYPE = " INSERT INTO ticket_checklist (ticket_id, checklist_id) VALUES (?, ?)";
		public static final String DELETE_WORKFLOW_FOR_TICKET_TYPE = "delete FROM ticket_workflow where ticket_id=?";
		public static final String GET_DEFAULT_TICKET_TYPE = "SELECT min(id) as id FROM helpdesk_type where helpdesk_id=? and is_active is true;";
		public static final String GET_DEFAULT_ADMIN_ID = "SELECT user_id as id FROM helpdesk_admin where helpdesk_id=? LIMIT 1;";
		public static final String UPDATE_TICKET_WORKFLOW = "update ticket_workflow set status=?, time=? where workflow_id=? and ticket_id=?";
		public static final String GET_UPDATES = "SELECT created_by as createdBy, updates as upds, created_date as createdDate FROM ticket_updates where ticket_id=? and active is true order by id asc; ";
		public static final String UPDATE_TICKET_TYPE = "update ticket set type=? where id=?;";
		public static final String UPDATE_TICKET_CHECKLIST = "update ticket_checklist set checked=? where checklist_id=? and ticket_id=?";
		public static final String ADD_ACTIVITY_LOG = "INSERT INTO activity_logs(activity, ticket_id, timestamp, changes_by) VALUES (?,?,?,?);";
		public static final String GET_ACTIVITY_LOGS = "select id,activity,ticket_id as ticketId, timestamp, changes_by as changesBy from activity_logs where ticket_id=?";
		public static final String GET_WORKFLOW_NAME = "SELECT name FROM workflow_stage where id=?;";
		public static final String UPDATE_TICKET_DESCRIPTION = "update ticket set description=? where id=?";
		public static final String UPDATE_TICKET_PIN = "update ticket set pinned_ticket=? where id=?";
		public static final String GET_REVIEW_ID = "SELECT reviewId FROM ticket where id=? and active is true;";
		public static final String GET_ORG_ID_FROM_TICKET_ID = "SELECT org_id from helpdesk where id in (select helpdesk_id FROM helpdesk_ticket where ticket_id=?) limit 1;";
		public static final String GET_APP_URL_FROM_TICKET_ID = "select url from application where id in (select app_id FROM helpdesk_ticket where ticket_id=?);";

		public static final String GET_ACTIVITY_LOGS_PER_USER = "select activity_logs.id,activity,activity_logs.ticket_id as ticketId, timestamp, changes_by as changesBy, helpdesk_id as helpdeskId from activity_logs, helpdesk_ticket where activity_logs.ticket_id=helpdesk_ticket.ticket_id and activity_logs.ticket_id in (SELECT distinct t.id from ticket t where (requested_by = ? or (id in (select ticket_id from ticket_cc where user_id = ?))) and active is true) order by id desc;";

		public static final String GET_CREATED_AND_COPIED_TO_TICKET_IDS = "SELECT distinct t.id from ticket t where requested_by = ? or (id in (select ticket_id from ticket_cc where user_id = ?)) and active is true;";
		public static final String GET_CREATED_AND_COPIED_TO_TICKET_IDS_BY_HELPDESK_ID = "SELECT distinct t.id from ticket t, helpdesk_ticket where (requested_by = ? or (t.id in (select ticket_id from ticket_cc where user_id = ?))) and helpdesk_ticket.ticket_id=t.id and helpdesk_ticket.helpdesk_id=? and active is true";
		public static final String GET_TICKETS_BY_TAGS = "SELECT distinct ticket_id as id FROM tag_ticket where tag_ticket.tag_id in (select id from tag where tag_name in ";
		public static final String GET_HELPDESK_ID_FOR_TICKET = "select helpdesk.id from helpdesk_ticket,helpdesk where ticket_id=? and helpdesk.id=helpdesk_ticket.helpdesk_id and helpdesk.is_active is true;";
		public static final String STATUS = "status.keyword";
		public static final String ACTIVE = "active";
		public static final String DESCRIPTION = "description";
		public static final String HELPDESK_ID = "helpdeskId";
		public static final String TAGS = "tags.keyword";
		public static final String CC = "cc.keyword";
		public static final String SOURCE_ID = "sourceId";
		public static final String GET_TICKET_IDS_BY_HELPDESK_ID = "SELECT distinct t.id from ticket t, helpdesk_ticket where helpdesk_ticket.ticket_id=t.id and helpdesk_ticket.helpdesk_id=? and active is true;";

		public static final String GET_TICKET_DETAILS_BY_HELPDESK = "select tkt.id as ticketId, tkt.pinned_ticket as pinnedTicket, tkt.created_time as createdTime, tkt.updated_time as updatedTime, "
				+ " tkt.rate as rate, tkt.max_rating as maxRating, tkt.priority as priority, tkt.requested_by as requestedBy, "
				+ " tkt.description as decription, tkt.notes as notes, tkt.type as type, tkt.active as active, "
				+ " ht.sr_source_id as sourceId, ht.helpdesk_id as helpdeskId, ht.app_id as appId, "
				+ " tcc.user_id as tktCCUserId, "
				+ " clist.id as clistId, clist.name as clistName, tcl.checked as tclChecked "
				+ " from ticket tkt LEFT JOIN helpdesk_ticket ht ON tkt.id=ht.ticket_id "
				+ " LEFT JOIN ticket_cc tcc ON tkt.id = tcc.ticket_id "
				+ " LEFT JOIN ticket_checklist tcl ON tkt.id = tcl.ticket_id "
				+ " LEFT JOIN checklist clist ON tcl.checklist_id = clist.id "
				+ " where ht.helpdesk_id = ? and  active is true";
		public static final String REQUESTEDBY = "requestedBy";
	}

	public static final String GET_USER_ACTIONS = "SELECT * FROM retail_actions ma inner join retail_role_actions mra on mra.action_id = ma.id where ma.enabled = true and mra.role_id = ? order by ma.order_number";
	public static final String GET_USER_AUTH_DETAILS = "SELECT id, user_id, auth_token FROM user_authentication WHERE id=?";
	public static final String SAVE_USER = "INSERT INTO user(name,username,password) VALUES (?,?,?)";
	public static final String SAVE_USER_AUTHENTICATION = "INSERT INTO user_authentication(user_id,auth_token) VALUES (?,?)";
	public static final String GET_USER_ROLE = "SELECT user_id, role_id FROM retail_user_role WHERE user_id=?";
	public static final String GET_ROLES_FOR_USER = " select mur.user_id, mur.role_id, mr.role_name, mr.description, mr.priority from retail_user_role mur LEFT JOIN retail_role mr ON mur.role_id = mr.id "
			+ " WHERE mur.user_id = ? ";
	public static final String USER_ACTIVE_CONDITION = " WHERE usr.is_active = ? ";
	public static final String WHERE_CLAUSE = " WHERE ";
	public static final String AND_CONDITION = " AND ";
	public static final String OR_CONDITION = " OR ";

	public static final String UPDATE_USER = "UPDATE user SET name = ?, username = ? , is_active = ? where id = ? ";
	public static final String UPDATE_USER_PROFILE = "UPDATE user_profile SET name = ?, email = ?, phone_number = ?,"
			+ " updated_by = ?  WHERE user_id = ? ";
	public static final String GET_USER_COUNT = "SELECT count(*) FROM user usr";
	public static final String GET_USER_COUNT_ON_ACTIVE_STATUS = "SELECT count(*) FROM user usr where usr.is_active = ? ";
	public static final String GET_USER_COUNT_FOR_ROLE = "SELECT count(*) FROM user usr LEFT JOIN retail_user_role usrrole ON usr.id = usrrole.user_id where usrrole.role_id = ? "
			+ "and usr.is_active IS TRUE";
	public static final String GET_ROLE_COUNT = "SELECT count(*) FROM retail_role";

	public static final String INVALIDATE_TOKEN = "DELETE from user_authentication WHERE auth_token = ? ";
	public static final String FETCH_AUTH_TOKEN_REF = "SELECT id FROM user_authentication WHERE auth_token = ? ";
	public static final String CHECK_USER_DEVICE_TOKEN = "SELECT COUNT(*) FROM user_device WHERE user_id = ? AND device_token = ? ";
	public static final String GET_S3_ACCESS = "SELECT access_key as accessKey,secret_key as secretKey,bucket_name as bucketName FROM s3_config WHERE id = 1";
	public static final String GET_CONFIG = "SELECT client_id as clientId,client_secret as clientSecret,refresh_token as refreshToken FROM review_config where org_id=?;";
	public static final String INSERT_PROFILE_PICTURE = "UPDATE user set img_path=? WHERE id=?";
	public static final String GET_DP = "SELECT img_path FROM user WHERE id=?";
	public static final String ADD_ATTACHMENT_TO_TICKET = "insert into ticket_attachment(ticket_id, attachment_id) values (?,?);";
	public static final String INSERT_ATTACHMENT = "insert into attachment_url(url) values (?);";
}
