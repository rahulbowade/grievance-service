package org.upsmf.grievance.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import org.upsmf.grievance.dto.HelpdeskDto;
import org.upsmf.grievance.dto.OrgUserRoleDto;
import org.upsmf.grievance.model.Access;
import org.upsmf.grievance.model.Action;
import org.upsmf.grievance.model.ActivityLog;
import org.upsmf.grievance.model.App;
import org.upsmf.grievance.model.CommonDataModel;
import org.upsmf.grievance.model.HelpDeskApp;
import org.upsmf.grievance.model.Helpdesk;
import org.upsmf.grievance.model.Organization;
import org.upsmf.grievance.model.Role;
import org.upsmf.grievance.model.S3Config;
import org.upsmf.grievance.model.ServiceRequest;
import org.upsmf.grievance.model.Tags;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.TicketTags;
import org.upsmf.grievance.model.Updates;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.service.ApplicationService;
import org.upsmf.grievance.service.HelpdeskService;
import org.upsmf.grievance.service.RoleActionService;
import org.upsmf.grievance.service.SuperAdminService;
import org.upsmf.grievance.service.UserService;

@Component
public class MasterDataManager implements ApplicationRunner {

	public static final Logger LOGGER = LoggerFactory.getLogger(MasterDataManager.class);

	
	protected static ConcurrentMap<Long, Role> roleMap = new ConcurrentHashMap<>();
	protected static ConcurrentMap<Long, List<Role>> orgRoleMap = new ConcurrentHashMap<>();

	
	protected static ConcurrentMap<Long, List<Long>> orgUserMap = new ConcurrentHashMap<>();
	protected static ConcurrentMap<Long, Long> userOrgMap = new ConcurrentHashMap<>();

	
	protected static ConcurrentMap<Long, Long> userRoleMap = new ConcurrentHashMap<>();
	protected static ConcurrentMap<Long, List<Long>> userRoleListMap = new ConcurrentHashMap<>();
	protected static ConcurrentMap<Long, Long> roleUserMap = new ConcurrentHashMap<>();
	protected static ConcurrentMap<Long, List<String>> roleActionsListMap = new ConcurrentHashMap<>();
	
	protected static ConcurrentMap<Long, Long> appIdHelpdeskIdMapping = new ConcurrentHashMap<>();
	protected static ConcurrentMap<String, App> appNameAppObjectMapping = new ConcurrentHashMap<>();
	protected static ConcurrentMap<Long, App> appIdAppObjectMapping = new ConcurrentHashMap<>();

	
	protected static ConcurrentMap<Long, String> orgIdAndOrgNameMap = new ConcurrentHashMap<>();

	
	protected static ConcurrentMap<Long, Helpdesk> helpdeskIdHelpdeskObjectMapping = new ConcurrentHashMap<>();
	protected static ConcurrentMap<Long, List<Long>> helpdeskIdAppIdsMapping = new ConcurrentHashMap<>();

	protected static ConcurrentMap<Long, String> userIdAndUserNameMap = new ConcurrentHashMap<>();

	
	public static final BeanPropertyRowMapper<User> rowMapUser = new BeanPropertyRowMapper<>(User.class);
	public static final BeanPropertyRowMapper<CommonDataModel> rowMapCommonDataModel = new BeanPropertyRowMapper<>(
			CommonDataModel.class);
	public static final BeanPropertyRowMapper<Organization> rowMapOrganizationModel = new BeanPropertyRowMapper<>(
			Organization.class);
	public static final BeanPropertyRowMapper<HelpdeskDto> rowMapHelpdeskDto = new BeanPropertyRowMapper<>(
			HelpdeskDto.class);
	public static final BeanPropertyRowMapper<App> rowMapApp = new BeanPropertyRowMapper<>(App.class);
	public static final BeanPropertyRowMapper<ServiceRequest> rowMapServiceRequest = new BeanPropertyRowMapper<>(
			ServiceRequest.class);
	public static final BeanPropertyRowMapper<Ticket> rowMapTicket = new BeanPropertyRowMapper<>(Ticket.class);
	public static final BeanPropertyRowMapper<Updates> rowMapUpdate = new BeanPropertyRowMapper<>(Updates.class);
	public static final BeanPropertyRowMapper<ActivityLog> rowMapActivityLogs = new BeanPropertyRowMapper<>(
			ActivityLog.class);
	public static final BeanPropertyRowMapper<S3Config> rowMapS3Config = new BeanPropertyRowMapper<>(S3Config.class);
	public static final BeanPropertyRowMapper<Access> rowMapAccess = new BeanPropertyRowMapper<>(Access.class);
	public static final BeanPropertyRowMapper<Tags> rowMapTags = new BeanPropertyRowMapper<>(Tags.class);
	public static final BeanPropertyRowMapper<TicketTags> rowMapTicketTags = new BeanPropertyRowMapper<>(
			TicketTags.class);
	public static final BeanPropertyRowMapper<Role> rowMapRole = new BeanPropertyRowMapper<>(Role.class);
	public static final BeanPropertyRowMapper<OrgUserRoleDto> rowMapOrgUserRoleDto = new BeanPropertyRowMapper<>(
			OrgUserRoleDto.class);
	public static final BeanPropertyRowMapper<HelpDeskApp> rowMapHelpDeskApp = new BeanPropertyRowMapper<>(
			HelpDeskApp.class);
	public static final BeanPropertyRowMapper<Helpdesk> rowMapHelpdesk = new BeanPropertyRowMapper<>(Helpdesk.class);
	public static final BeanPropertyRowMapper<Action> rowMapAction = new BeanPropertyRowMapper<>(Action.class);

	private static RoleActionService roleActionService;

	private static UserService userService;

	private static ApplicationService applicationService;

	private static HelpdeskService helpdeskService;

	private static SuperAdminService superAdminService;

	@Autowired
	public void setApplicationService(ApplicationService applicationService) {
		MasterDataManager.applicationService = applicationService;
	}

	@Autowired
	public void setUserService(HelpdeskService helpdeskService) {
		MasterDataManager.helpdeskService = helpdeskService;
	}

	@Autowired
	public void setRoleActionService(RoleActionService roleActionService) {
		MasterDataManager.roleActionService = roleActionService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		MasterDataManager.userService = userService;
	}

	@Autowired
	public void setSuperAdminService(SuperAdminService superAdminService) {
		MasterDataManager.superAdminService = superAdminService;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		getAllOrgRoles();
		getAllOrg();
		getAllOrgUsers();
		getAllUserRoles();
		getAppObjectFromAppName();
		getHelpdeskIdFromAppId();
		getHelpdeskObjectFromHelpdeskId();
		getUserIdAndUserName();
		initializeActions();
		intializeRolesAndActions();
		getAllActionsForRoles();
	}

	private static void intializeRolesAndActions() {
		roleActionService.intializeRolesAndActions();

	}

	private static void initializeActions() {
		LOGGER.info("#MasterDataManager :: Initializing Actions : ");
		roleActionService.initializeActions();

	}

	public static void flushMasterData() {
		orgRoleMap.clear();
		orgUserMap.clear();
		getUserOrgMap().clear();
		getRoleMap().clear();
		getUserRoleMap().clear();
		getRoleUserMap().clear();
		appIdHelpdeskIdMapping.clear();
		appNameAppObjectMapping.clear();
		appIdAppObjectMapping.clear();
		getOrgIdAndOrgNameMap().clear();
		getHelpdeskIdHelpdeskObjectMapping().clear();
		helpdeskIdAppIdsMapping.clear();
		getUserIdAndUserNameMap().clear();
	}

	public static void reloadMasterData() {
		getAllOrgRoles();
		getAllOrg();
		getUserIdAndUserName();
		getAllOrgUsers();
		getAllUserRoles();
		getAppObjectFromAppName();
		getHelpdeskIdFromAppId();
		getHelpdeskObjectFromHelpdeskId();
	}

	

	public static void getAllOrgRoles() {
		getRoleMap().clear();
		orgRoleMap.clear();

		List<Role> roleList = roleActionService.getAllOrgRoles();
		for (Role role : roleList) {
			getRoleMap().put(role.getId(), role);
			if (orgRoleMap.containsKey(role.getOrgId())) {
				List<Role> orgRoleList = orgRoleMap.get(role.getOrgId());
				orgRoleList.add(role);
			} else {
				List<Role> orgRoleList = new ArrayList<>();
				orgRoleList.add(role);
				orgRoleMap.put(role.getOrgId(), orgRoleList);
			}
		}
	}

	public static void getAllOrg() {
		getOrgIdAndOrgNameMap().clear();

		try {
			List<Organization> orgs = superAdminService.getAllOrganization();
			for (Organization org : orgs) {
				getOrgIdAndOrgNameMap().put(org.getId(), org.getOrgName());
			}
		} catch (Exception e) {
			LOGGER.error(String.format("Error : %s", e.getMessage()));
		}
	}

	public static void getAllOrgUsers() {
		orgUserMap.clear();
		getUserOrgMap().clear();

		List<OrgUserRoleDto> orgUserList = userService.getAllOrgUsers();
		for (OrgUserRoleDto dto : orgUserList) {
			if (orgUserMap.containsKey(dto.getOrgId())) {
				List<Long> userList = orgUserMap.get(dto.getOrgId());
				userList.add(dto.getUserId());
			} else {
				List<Long> userList = new ArrayList<>();
				userList.add(dto.getUserId());
				orgUserMap.put(dto.getOrgId(), userList);
			}
			getUserOrgMap().put(dto.getUserId(), dto.getOrgId());
		}
	}

	public static void getAllUserRoles() {
		getUserRoleMap().clear();
		getRoleUserMap().clear();
		getUserRoleListMap().clear();

		List<OrgUserRoleDto> userRoleList = userService.getAllUserRoles();
		for (OrgUserRoleDto dto : userRoleList) {
			if (!getUserRoleMap().containsKey(dto.getUserId())) {
				getUserRoleMap().put(dto.getUserId(), dto.getRoleId());
			}

			if (!getRoleUserMap().containsKey(dto.getRoleId())) {
				getRoleUserMap().put(dto.getRoleId(), dto.getUserId());
			}

			if (!getUserRoleListMap().containsKey(dto.getUserId())) {
				getUserRoleListMap().put(dto.getUserId(), dto.getRoleIds());
			}
		}
	}

	public static void getAllActionsForRoles() {
		roleActionsListMap.clear();
		List<Long> roleIds = new ArrayList<>();
		for (Map.Entry<Long, Role> entry : getRoleMap().entrySet()) {
			roleIds.add(entry.getKey());
		}
		Map<Long, List<String>> fetchedMap = roleActionService.getAllActionsForRoles(roleIds);
		roleActionsListMap.putAll(fetchedMap);
	}

	public static void getHelpdeskIdFromAppId() {
		helpdeskIdAppIdsMapping.clear();
		appIdHelpdeskIdMapping.clear();

		List<HelpDeskApp> appIdHelpdeskId = helpdeskService.getAppIdAndHelpDeskId();
		for (HelpDeskApp dto : appIdHelpdeskId) {
			appIdHelpdeskIdMapping.put(dto.getAppId(), dto.getHelpDeskId());
			List<Long> appIds = new ArrayList<>();
			if (helpdeskIdAppIdsMapping.get(dto.getHelpDeskId()) != null) {
				appIds = helpdeskIdAppIdsMapping.get(dto.getHelpDeskId());
			}
			appIds.add(dto.getAppId());
			helpdeskIdAppIdsMapping.put(dto.getHelpDeskId(), appIds);
		}
	}

	public static void getAppObjectFromAppName() {
		appIdAppObjectMapping.clear();
		appNameAppObjectMapping.clear();

		List<App> app = applicationService.getAppIdAndAppObject();
		for (App dto : app) {
			appNameAppObjectMapping.put(dto.getName(), dto);
			appIdAppObjectMapping.put(dto.getId(), dto);
		}

	}

	public static void getHelpdeskObjectFromHelpdeskId() {
		getHelpdeskIdHelpdeskObjectMapping().clear();

		List<Helpdesk> helpdesk = helpdeskService.getHelpdeskObjectFromHelpdeskId();
		for (Helpdesk dto : helpdesk) {
			getHelpdeskIdHelpdeskObjectMapping().put(dto.getId(), dto);
		}
	}

	public static void getUserIdAndUserName() {
		getUserIdAndUserNameMap().clear();
		List<User> users = userService.getUserIdAndUserName();
		for (User usr : users) {
			getUserIdAndUserNameMap().put(usr.getId(), usr.getName());
		}
	}

	

	public static List<Role> getRolesByOrg(Long orgId) {
		if (orgRoleMap.containsKey(orgId)) {
			return orgRoleMap.get(orgId);
		} else {
			getAllOrgRoles();
			if (orgRoleMap.containsKey(orgId)) {
				return orgRoleMap.get(orgId);
			} else {
				return new ArrayList<>();
			}
		}
	}

	public static Role getRoleById(Long roleId) {
		if (getRoleMap().containsKey(roleId)) {
			return getRoleMap().get(roleId);
		} else {
			getAllOrgRoles();
			if (getRoleMap().containsKey(roleId)) {
				return getRoleMap().get(roleId);
			} else {
				return new Role();
			}
		}
	}

	public static Long getOrgForUser(Long userId) {
		if (getUserOrgMap().containsKey(userId)) {
			return getUserOrgMap().get(userId);
		} else {
			getAllOrgUsers();
			if (getUserOrgMap().containsKey(userId)) {
				return getUserOrgMap().get(userId);
			} else {
				return 0L;
			}
		}
	}

	public static List<Long> getUsersForOrg(Long orgId) {
		if (orgUserMap.containsKey(orgId)) {
			return orgUserMap.get(orgId);
		} else {
			getAllOrgUsers();
			if (orgUserMap.containsKey(orgId)) {
				return orgUserMap.get(orgId);
			} else {
				return new ArrayList<>();
			}
		}
	}

	public static Long getRoleForUser(Long userId) {
		if (getUserRoleMap().containsKey(userId)) {
			return getUserRoleMap().get(userId);
		} else {
			getAllUserRoles();
			if (getUserRoleMap().containsKey(userId)) {
				return getUserRoleMap().get(userId);
			} else {
				return 0l;
			}
		}
	}

	public static Long getUserForRole(Long roleId) {
		if (getRoleUserMap().containsKey(roleId)) {
			return getRoleUserMap().get(roleId);
		} else {
			getAllUserRoles();
			if (getRoleUserMap().containsKey(roleId)) {
				return getRoleUserMap().get(roleId);
			} else {
				return 0l;
			}
		}
	}

	public static List<Long> getAppIdsForHelpdesk(Long helpdeskId) {
		if (helpdeskIdAppIdsMapping.containsKey(helpdeskId)) {
			return helpdeskIdAppIdsMapping.get(helpdeskId);
		} else {
			getHelpdeskIdFromAppId();
			if (helpdeskIdAppIdsMapping.containsKey(helpdeskId)) {
				return helpdeskIdAppIdsMapping.get(helpdeskId);
			} else {
				return new ArrayList<>();
			}
		}
	}

	public static App getAppFromAppId(Long appId) {
		if (appIdAppObjectMapping.containsKey(appId)) {
			return appIdAppObjectMapping.get(appId);
		} else {
			getAppObjectFromAppName();
			if (appIdAppObjectMapping.containsKey(appId)) {
				return appIdAppObjectMapping.get(appId);
			} else {
				return null;
			}
		}
	}

	public static List<Long> getRoleIdsForUserId(Long userId) {
		if (getUserRoleListMap().containsKey(userId)) {
			return getUserRoleListMap().get(userId);
		} else {
			getAllUserRoles();
			if (getUserRoleListMap().containsKey(userId)) {
				return getUserRoleListMap().get(userId);
			} else {
				return new ArrayList<>();
			}
		}
	}

	public static List<String> getActionUrlsForRoleId(Long roleId) {
		if (roleActionsListMap.containsKey(roleId)) {
			return roleActionsListMap.get(roleId);
		} else {
			getAllUserRoles();
			if (roleActionsListMap.containsKey(roleId)) {
				return roleActionsListMap.get(roleId);
			} else {
				return new ArrayList<>();
			}
		}
	}

	public static ConcurrentMap<Long, Long> getUserOrgMap() {
		return userOrgMap;
	}

	public static void setUserOrgMap(ConcurrentMap<Long, Long> userOrgMap) {
		MasterDataManager.userOrgMap = userOrgMap;
	}

	public static ConcurrentMap<Long, String> getOrgIdAndOrgNameMap() {
		return orgIdAndOrgNameMap;
	}

	public static void setOrgIdAndOrgNameMap(ConcurrentMap<Long, String> orgIdAndOrgNameMap) {
		MasterDataManager.orgIdAndOrgNameMap = orgIdAndOrgNameMap;
	}

	public static ConcurrentMap<Long, Role> getRoleMap() {
		return roleMap;
	}

	public static void setRoleMap(ConcurrentMap<Long, Role> roleMap) {
		MasterDataManager.roleMap = roleMap;
	}

	public static ConcurrentMap<Long, Long> getUserRoleMap() {
		return userRoleMap;
	}

	public static void setUserRoleMap(ConcurrentMap<Long, Long> userRoleMap) {
		MasterDataManager.userRoleMap = userRoleMap;
	}

	public static ConcurrentMap<Long, Helpdesk> getHelpdeskIdHelpdeskObjectMapping() {
		return helpdeskIdHelpdeskObjectMapping;
	}

	public static void setHelpdeskIdHelpdeskObjectMapping(
			ConcurrentMap<Long, Helpdesk> helpdeskIdHelpdeskObjectMapping) {
		MasterDataManager.helpdeskIdHelpdeskObjectMapping = helpdeskIdHelpdeskObjectMapping;
	}

	public static ConcurrentMap<Long, String> getUserIdAndUserNameMap() {
		return userIdAndUserNameMap;
	}

	public static void setUserIdAndUserNameMap(ConcurrentMap<Long, String> userIdAndUserNameMap) {
		MasterDataManager.userIdAndUserNameMap = userIdAndUserNameMap;
	}

	public static ConcurrentMap<Long, Long> getRoleUserMap() {
		return roleUserMap;
	}

	public static void setRoleUserMap(ConcurrentMap<Long, Long> roleUserMap) {
		MasterDataManager.roleUserMap = roleUserMap;
	}

	public static ConcurrentMap<Long, List<Long>> getUserRoleListMap() {
		return userRoleListMap;
	}

	public static void setUserRoleListMap(ConcurrentMap<Long, List<Long>> userRoleListMap) {
		MasterDataManager.userRoleListMap = userRoleListMap;
	}

}
