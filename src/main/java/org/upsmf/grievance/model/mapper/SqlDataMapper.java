package org.upsmf.grievance.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import org.upsmf.grievance.dto.HelpdeskDto;
import org.upsmf.grievance.dto.HelpdeskTypeDto;
import org.upsmf.grievance.dto.HelpdeskWorkflowDto;
import org.upsmf.grievance.dto.OrgUserRoleDto;
import org.upsmf.grievance.dto.TicketWorkflowDto;
import org.upsmf.grievance.model.Action;
import org.upsmf.grievance.model.ChecklistItem;
import org.upsmf.grievance.model.HelpDeskApp;
import org.upsmf.grievance.model.Helpdesk;
import org.upsmf.grievance.model.Organization;
import org.upsmf.grievance.model.Role;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.model.UserAuthentication;
import org.upsmf.grievance.util.SqlConstants;

public class SqlDataMapper {

	public static final Logger LOGGER = LoggerFactory.getLogger(SqlDataMapper.class);
	public static final String ERROR_LOG_FOREWORD = "Encountered an Exception while ";

	public User buildUserObjectFromResultSet(ResultSet rs) {
		User user = new User();
		try {
			user.setId(rs.getLong(SqlConstants.DbAttributes.ID));
			user.setUsername(rs.getString(SqlConstants.DbAttributes.USERNAME));
			user.setName(rs.getString(SqlConstants.DbAttributes.NAME));
			user.setOrgId(rs.getLong(SqlConstants.DbAttributes.ORGID));
			user.setIsActive(rs.getBoolean(SqlConstants.DbAttributes.ISACTIVE));
			user.setPassword(rs.getString(SqlConstants.DbAttributes.PSWRD));
		} catch (Exception e) {
			
		}
		return user;
	}

	public class UserMapper implements RowMapper<User> {

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			return buildUserObjectFromResultSet(rs);
		}

	}

	public class UserRolesMapper implements RowMapper<User> {
		private Map<Long, Role> roleMap = new HashMap<>();

		public Map<Long, Role> getRoleMap() {
			return roleMap;
		}

		public void setRoleMap(Map<Long, Role> roleMap) {
			this.roleMap = roleMap;
		}

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = buildUserObjectFromResultSet(rs);
			if (!roleMap.containsKey(rs.getLong(SqlConstants.DbAttributes.ROLEID))) {
				roleMap.put(rs.getLong(SqlConstants.DbAttributes.ROLEID), buildRoleObjectFromResultSet(rs));
			}
			return user;
		}

	}

	public class UserRolesActionsMapper implements RowMapper<User> {
		private Map<Long, Role> roleMap = new HashMap<>();
		private Map<Long, Action> actionMap = new HashMap<>();

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = buildUserObjectFromResultSet(rs);
			if (!getRoleMap().containsKey(rs.getLong(SqlConstants.DbAttributes.ROLEID))) {
				getRoleMap().put(rs.getLong(SqlConstants.DbAttributes.ROLEID), buildRoleObjectFromResultSet(rs));
			}
			if (!getActionMap().containsKey(rs.getLong(SqlConstants.DbAttributes.ACTIONID))) {
				getActionMap().put(rs.getLong(SqlConstants.DbAttributes.ACTIONID), buildActionObjectFromResultSet(rs));
			}
			return user;
		}

		private Action buildActionObjectFromResultSet(ResultSet rs) {
			Action action = new Action();
			try {
				action.setId(rs.getLong(SqlConstants.DbAttributes.ACTIONID));
				action.setName(rs.getString(SqlConstants.DbAttributes.ACTIONNAME));
				action.setDisplayName(rs.getString(SqlConstants.DbAttributes.ACTIONDISPLAYNAME));
				action.setQueryParams(rs.getString(SqlConstants.DbAttributes.ACTIONQUERYPARAMS));
				action.setServiceCode(rs.getString(SqlConstants.DbAttributes.ACTIONSERVICECODE));
				action.setUrl(rs.getString(SqlConstants.DbAttributes.ACTIONURL));
			} catch (Exception e) {
				
			}
			return action;
		}

		public Map<Long, Role> getRoleMap() {
			return roleMap;
		}

		public void setRoleMap(Map<Long, Role> roleMap) {
			this.roleMap = roleMap;
		}

		public Map<Long, Action> getActionMap() {
			return actionMap;
		}

		public void setActionMap(Map<Long, Action> actionMap) {
			this.actionMap = actionMap;
		}

	}

	private Role buildRoleObjectFromResultSet(ResultSet rs) {
		Role role = new Role();
		try {
			role.setId(rs.getLong(SqlConstants.DbAttributes.ROLEID));
			role.setName(rs.getString(SqlConstants.DbAttributes.ROLENAME));
			role.setOrgId(rs.getLong(SqlConstants.DbAttributes.ROLEORGID));
		} catch (Exception e) {
			
		}
		return role;
	}

	public class UserDetailsMapper implements RowMapper<User> {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getLong(SqlConstants.DbAttributes.ID));
			user.setUsername(rs.getString(SqlConstants.DbAttributes.USERNAME));
			user.setName(rs.getString(SqlConstants.DbAttributes.NAME));
			user.setPhone(rs.getString(SqlConstants.DbAttributes.PHONE));
			user.setImagePath(rs.getString(SqlConstants.DbAttributes.IMAGE_PATH));
			user.setIsActive(rs.getBoolean(SqlConstants.DbAttributes.IS_ACTIVE));
			user.setCreatedDate(rs.getString(SqlConstants.DbAttributes.CREATED_DATE));
			user.setUpdatedDate(rs.getString(SqlConstants.DbAttributes.UPDATED_DATE));
			return user;
		}
	}

	public class UserAuthenticationMapper implements RowMapper<UserAuthentication> {
		@Override
		public UserAuthentication mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserAuthentication userAuthentication = new UserAuthentication();
			userAuthentication.setId(rs.getLong(SqlConstants.DbAttributes.ID));
			userAuthentication.setUserId(rs.getLong(SqlConstants.DbAttributes.USERID));
			userAuthentication.setAuthToken(rs.getString(SqlConstants.DbAttributes.AUTH_TOKEN));
			return userAuthentication;
		}
	}

	public class UserRoleMapper implements RowMapper<Role> {
		private Map<Long, Role> roleMap = new HashMap<>();

		@Override
		public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
			if (!getRoleMap().containsKey(rs.getLong(SqlConstants.DbAttributes.ROLEID))) {
				Role role = new Role();
				role.setId(rs.getLong(SqlConstants.DbAttributes.ROLEID));
				role.setName(rs.getString(SqlConstants.DbAttributes.ROLENAME));
				getRoleMap().put(rs.getLong(SqlConstants.DbAttributes.ROLEID), role);
			}
			return null;
		}

		public Map<Long, Role> getRoleMap() {
			return roleMap;
		}

		public void setRoleMap(Map<Long, Role> roleMap) {
			this.roleMap = roleMap;
		}
	}

	public class RoleActionMapper implements RowMapper<Long> {
		private Map<Long, List<String>> roleActionsMap = new HashMap<>();

		@Override
		public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
			if (getRoleActionsMap().containsKey(rs.getLong(SqlConstants.DbAttributes.ROLEID))) {
				List<String> actionsList = getRoleActionsMap().get(rs.getLong(SqlConstants.DbAttributes.ROLEID));
				actionsList.add(rs.getString(SqlConstants.DbAttributes.ACTIONURL));
			} else {
				List<String> actionsList = new ArrayList<>();
				actionsList.add(rs.getString(SqlConstants.DbAttributes.ACTIONURL));
				getRoleActionsMap().put(rs.getLong(SqlConstants.DbAttributes.ROLEID), actionsList);
			}
			return null;
		}

		public Map<Long, List<String>> getRoleActionsMap() {
			return roleActionsMap;
		}

		public void setRoleActionsMap(Map<Long, List<String>> roleActionsMap) {
			this.roleActionsMap = roleActionsMap;
		}
	}

	public class UserOrgMapper implements RowMapper<OrgUserRoleDto> {
		@Override
		public OrgUserRoleDto mapRow(ResultSet rs, int rowNum) throws SQLException {
			OrgUserRoleDto dto = new OrgUserRoleDto();
			dto.setId(rs.getLong(SqlConstants.DbAttributes.ID));
			dto.setOrgId(rs.getLong(SqlConstants.DbAttributes.ORGID));
			dto.setUserId(rs.getLong(SqlConstants.DbAttributes.USERID));
			return dto;
		}
	}

	public class RolesUserMapper implements RowMapper<OrgUserRoleDto> {
		private Map<Long, OrgUserRoleDto> userMap = new HashMap<>();
		Map<Long, List<Long>> userRolesMap = new HashMap<>();

		@Override
		public OrgUserRoleDto mapRow(ResultSet rs, int rowNum) throws SQLException {
			if (userRolesMap.containsKey(rs.getLong(SqlConstants.DbAttributes.USERID))) {
				List<Long> roleIds = userRolesMap.get(rs.getLong(SqlConstants.DbAttributes.USERID));
				roleIds.add(rs.getLong(SqlConstants.DbAttributes.ROLEID));
			} else {
				OrgUserRoleDto dto = new OrgUserRoleDto();
				dto.setId(rs.getLong(SqlConstants.DbAttributes.ID));
				dto.setRoleId(rs.getLong(SqlConstants.DbAttributes.ROLEID));
				dto.setUserId(rs.getLong(SqlConstants.DbAttributes.USERID));
				List<Long> roleIds = new ArrayList<>();
				roleIds.add(rs.getLong(SqlConstants.DbAttributes.ROLEID));
				userRolesMap.put(rs.getLong(SqlConstants.DbAttributes.USERID), roleIds);
				dto.setRoleIds(userRolesMap.get(rs.getLong(SqlConstants.DbAttributes.USERID)));
				getUserMap().put(rs.getLong(SqlConstants.DbAttributes.USERID), dto);
			}
			return null;
		}

		public Map<Long, OrgUserRoleDto> getUserMap() {
			return userMap;
		}

		public void setUserMap(Map<Long, OrgUserRoleDto> userMap) {
			this.userMap = userMap;
		}
	}

	public class HelpDeskAppMapper implements RowMapper<HelpDeskApp> {
		@Override
		public HelpDeskApp mapRow(ResultSet rs, int rowNum) throws SQLException {
			HelpDeskApp app = new HelpDeskApp();
			app.setAppId(rs.getLong(SqlConstants.DbAttributes.APPID));
			app.setHelpDeskId(rs.getLong(SqlConstants.DbAttributes.HELPDESKID));
			return app;
		}
	}

	public class HelpdeskUserMapper implements RowMapper<User> {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getLong(SqlConstants.DbAttributes.ID));
			user.setName(rs.getString(SqlConstants.DbAttributes.NAME));
			user.setUsername(rs.getString(SqlConstants.DbAttributes.USERNAME));
			user.setImagePath(rs.getString(SqlConstants.DbAttributes.IMAGE_PATH));
			return user;
		}
	}

	public class UserHelpdeskMapper implements RowMapper<Helpdesk> {
		@Override
		public Helpdesk mapRow(ResultSet rs, int rowNum) throws SQLException {
			Helpdesk helpdesk = new Helpdesk();
			helpdesk.setId(rs.getLong(SqlConstants.DbAttributes.ID));
			helpdesk.setName(rs.getString(SqlConstants.DbAttributes.NAME));
			helpdesk.setIsActive(rs.getBoolean(SqlConstants.DbAttributes.ISACTIVE));
			helpdesk.setColor(rs.getString(SqlConstants.DbAttributes.COLOR));
			return helpdesk;
		}
	}

	public class ChecklistItemMapper implements RowMapper<ChecklistItem> {
		@Override
		public ChecklistItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			ChecklistItem item = new ChecklistItem();
			item.setId(rs.getLong(SqlConstants.DbAttributes.CHECKLISTID));
			item.setItem(rs.getString(SqlConstants.DbAttributes.ITEMNAME));
			return item;
		}
	}

	public class TicketsChecklistItemMapper implements RowMapper<ChecklistItem> {
		@Override
		public ChecklistItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			ChecklistItem item = new ChecklistItem();
			item.setId(rs.getLong(SqlConstants.DbAttributes.ID));
			item.setItem(rs.getString(SqlConstants.DbAttributes.NAME));
			item.setChecked(rs.getBoolean(SqlConstants.DbAttributes.CHECKED));
			return item;
		}
	}

	public class TicketWorkFlowMapper implements RowMapper<TicketWorkflowDto> {
		@Override
		public TicketWorkflowDto mapRow(ResultSet rs, int rowNum) throws SQLException {
			TicketWorkflowDto ticketWorkflowDto = new TicketWorkflowDto();
			ticketWorkflowDto.setId(rs.getLong(SqlConstants.DbAttributes.ID));
			ticketWorkflowDto.setName(rs.getString(SqlConstants.DbAttributes.NAME));
			ticketWorkflowDto.setStatus(rs.getBoolean(SqlConstants.DbAttributes.STATUS));
			ticketWorkflowDto.setTime(rs.getString(SqlConstants.DbAttributes.TIME));
			ticketWorkflowDto.setWorkFlowId(rs.getLong(SqlConstants.DbAttributes.WORKFLOWID));
			return ticketWorkflowDto;
		}
	}

	public class TicketWorkFlowMapperV2 implements RowMapper<TicketWorkflowDto> {
		private Map<Long, List<TicketWorkflowDto>> ticketWorkflowMap = new HashMap<>();

		@Override
		public TicketWorkflowDto mapRow(ResultSet rs, int rowNum) throws SQLException {
			if (getTicketWorkflowMap().containsKey(rs.getLong(SqlConstants.DbAttributes.TICKET_ID))) {
				TicketWorkflowDto ticketWorkflowDto = new TicketWorkflowDto();
				ticketWorkflowDto.setId(rs.getLong(SqlConstants.DbAttributes.ID));
				ticketWorkflowDto.setName(rs.getString(SqlConstants.DbAttributes.NAME));
				ticketWorkflowDto.setStatus(rs.getBoolean(SqlConstants.DbAttributes.STATUS));
				ticketWorkflowDto.setTime(rs.getString(SqlConstants.DbAttributes.TIME));
				ticketWorkflowDto.setWorkFlowId(rs.getLong(SqlConstants.DbAttributes.WORKFLOWID));
				List<TicketWorkflowDto> ticketWorkflowList = getTicketWorkflowMap()
						.get(rs.getLong(SqlConstants.DbAttributes.TICKET_ID));
				ticketWorkflowList.add(ticketWorkflowDto);
			} else {
				TicketWorkflowDto ticketWorkflowDto = new TicketWorkflowDto();
				ticketWorkflowDto.setId(rs.getLong(SqlConstants.DbAttributes.ID));
				ticketWorkflowDto.setName(rs.getString(SqlConstants.DbAttributes.NAME));
				ticketWorkflowDto.setStatus(rs.getBoolean(SqlConstants.DbAttributes.STATUS));
				ticketWorkflowDto.setTime(rs.getString(SqlConstants.DbAttributes.TIME));
				ticketWorkflowDto.setWorkFlowId(rs.getLong(SqlConstants.DbAttributes.WORKFLOWID));
				List<TicketWorkflowDto> ticketWorkflowList = new ArrayList<>();
				ticketWorkflowList.add(ticketWorkflowDto);
				getTicketWorkflowMap().put(rs.getLong(SqlConstants.DbAttributes.TICKET_ID), ticketWorkflowList);
			}
			return null;
		}

		public Map<Long, List<TicketWorkflowDto>> getTicketWorkflowMap() {
			return ticketWorkflowMap;
		}

		public void setTicketWorkflowMap(Map<Long, List<TicketWorkflowDto>> ticketWorkflowMap) {
			this.ticketWorkflowMap = ticketWorkflowMap;
		}
	}

	public class HelpdeskRowRecordMapper implements RowMapper<HelpdeskDto> {
		private Map<Long, HelpdeskDto> helpdeskMap = new HashMap<>();
		private Map<Long, List<Long>> helpdeskTypeMapping = new HashMap<>();
		private Map<Long, HelpdeskTypeDto> helpdeskTypeMap = new HashMap<>();
		private Map<Long, List<Long>> typeWorkflowMapping = new HashMap<>();
		private Map<Long, HelpdeskWorkflowDto> helpdeskWorkflowMap = new HashMap<>();

		@Override
		public HelpdeskDto mapRow(ResultSet rs, int rowNum) throws SQLException {
			if (getHelpdeskMap().containsKey(rs.getLong(SqlConstants.DbAttributes.ID))) {
				if (getHelpdeskTypeMap().containsKey(rs.getLong(SqlConstants.DbAttributes.HELPDESKTYPEID))) {
					if (!getHelpdeskWorkflowMap().containsKey(rs.getLong(SqlConstants.DbAttributes.WORKFLOWSTAGEID))) {
						HelpdeskWorkflowDto workflowDto = new HelpdeskWorkflowDto();
						workflowDto.setId(rs.getLong(SqlConstants.DbAttributes.WORKFLOWSTAGEID));
						workflowDto.setName(rs.getString(SqlConstants.DbAttributes.WORKFLOWSTAGE));
						workflowDto.setTypeId(rs.getLong(SqlConstants.DbAttributes.HELPDESKTYPEID));
						getHelpdeskWorkflowMap().put(rs.getLong(SqlConstants.DbAttributes.WORKFLOWSTAGEID),
								workflowDto);

						List<Long> workflowStageIds = getTypeWorkflowMapping()
								.get(rs.getLong(SqlConstants.DbAttributes.HELPDESKTYPEID));
						workflowStageIds.add(workflowDto.getId());
						getTypeWorkflowMapping().put(rs.getLong(SqlConstants.DbAttributes.HELPDESKTYPEID),
								workflowStageIds);
					}
				} else {
					HelpdeskWorkflowDto workflowDto = new HelpdeskWorkflowDto();
					workflowDto.setId(rs.getLong(SqlConstants.DbAttributes.WORKFLOWSTAGEID));
					workflowDto.setName(rs.getString(SqlConstants.DbAttributes.WORKFLOWSTAGE));
					workflowDto.setTypeId(rs.getLong(SqlConstants.DbAttributes.HELPDESKTYPEID));
					getHelpdeskWorkflowMap().put(rs.getLong(SqlConstants.DbAttributes.WORKFLOWSTAGEID), workflowDto);

					HelpdeskTypeDto typeDto = new HelpdeskTypeDto();
					typeDto.setId(rs.getLong(SqlConstants.DbAttributes.HELPDESKTYPEID));
					typeDto.setName(rs.getString(SqlConstants.DbAttributes.HELPDESKTYPE));
					typeDto.setHelpdeskId(rs.getLong(SqlConstants.DbAttributes.ID));
					getHelpdeskTypeMap().put(rs.getLong(SqlConstants.DbAttributes.HELPDESKTYPEID), typeDto);

					List<Long> workflowStageIds = new ArrayList<>();
					workflowStageIds.add(workflowDto.getId());
					getTypeWorkflowMapping().put(rs.getLong(SqlConstants.DbAttributes.HELPDESKTYPEID),
							workflowStageIds);

					List<Long> typeIds = getHelpdeskTypeMapping().get(rs.getLong(SqlConstants.DbAttributes.ID));
					typeIds.add(typeDto.getId());
					getHelpdeskTypeMapping().put(rs.getLong(SqlConstants.DbAttributes.ID), typeIds);

				}
			} else {
				HelpdeskWorkflowDto workflowDto = new HelpdeskWorkflowDto();
				workflowDto.setId(rs.getLong(SqlConstants.DbAttributes.WORKFLOWSTAGEID));
				workflowDto.setName(rs.getString(SqlConstants.DbAttributes.WORKFLOWSTAGE));
				workflowDto.setTypeId(rs.getLong(SqlConstants.DbAttributes.HELPDESKTYPEID));
				getHelpdeskWorkflowMap().put(rs.getLong(SqlConstants.DbAttributes.WORKFLOWSTAGEID), workflowDto);

				HelpdeskTypeDto typeDto = new HelpdeskTypeDto();
				typeDto.setId(rs.getLong(SqlConstants.DbAttributes.HELPDESKTYPEID));
				typeDto.setName(rs.getString(SqlConstants.DbAttributes.HELPDESKTYPE));
				typeDto.setHelpdeskId(rs.getLong(SqlConstants.DbAttributes.ID));
				getHelpdeskTypeMap().put(rs.getLong(SqlConstants.DbAttributes.HELPDESKTYPEID), typeDto);

				HelpdeskDto dto = new HelpdeskDto();
				dto.setId(rs.getLong(SqlConstants.DbAttributes.ID));
				dto.setName(rs.getString(SqlConstants.DbAttributes.NAME));
				dto.setIsActive(rs.getBoolean(SqlConstants.DbAttributes.ISACTIVE));
				dto.setOrgId(rs.getLong(SqlConstants.DbAttributes.ORGID));
				dto.setAllowAllUsers(rs.getBoolean(SqlConstants.DbAttributes.ALLOW_ALL_USERS));
				getHelpdeskMap().put(rs.getLong(SqlConstants.DbAttributes.ID), dto);
				dto.setColor(rs.getString(SqlConstants.DbAttributes.COLOR));
				dto.setDescription(rs.getString(SqlConstants.DbAttributes.DESCRIPTION));

				List<Long> workflowStageIds = new ArrayList<>();
				workflowStageIds.add(workflowDto.getId());
				getTypeWorkflowMapping().put(rs.getLong(SqlConstants.DbAttributes.HELPDESKTYPEID), workflowStageIds);

				List<Long> typeIds = new ArrayList<>();
				typeIds.add(typeDto.getId());
				getHelpdeskTypeMapping().put(rs.getLong(SqlConstants.DbAttributes.ID), typeIds);
			}
			return null;
		}

		public Map<Long, HelpdeskWorkflowDto> getHelpdeskWorkflowMap() {
			return helpdeskWorkflowMap;
		}

		public void setHelpdeskWorkflowMap(Map<Long, HelpdeskWorkflowDto> helpdeskWorkflowMap) {
			this.helpdeskWorkflowMap = helpdeskWorkflowMap;
		}

		public Map<Long, List<Long>> getHelpdeskTypeMapping() {
			return helpdeskTypeMapping;
		}

		public void setHelpdeskTypeMapping(Map<Long, List<Long>> helpdeskTypeMapping) {
			this.helpdeskTypeMapping = helpdeskTypeMapping;
		}

		public Map<Long, HelpdeskTypeDto> getHelpdeskTypeMap() {
			return helpdeskTypeMap;
		}

		public void setHelpdeskTypeMap(Map<Long, HelpdeskTypeDto> helpdeskTypeMap) {
			this.helpdeskTypeMap = helpdeskTypeMap;
		}

		public Map<Long, List<Long>> getTypeWorkflowMapping() {
			return typeWorkflowMapping;
		}

		public void setTypeWorkflowMapping(Map<Long, List<Long>> typeWorkflowMapping) {
			this.typeWorkflowMapping = typeWorkflowMapping;
		}

		public Map<Long, HelpdeskDto> getHelpdeskMap() {
			return helpdeskMap;
		}

		public void setHelpdeskMap(Map<Long, HelpdeskDto> helpdeskMap) {
			this.helpdeskMap = helpdeskMap;
		}
	}

	public class TicketDetailsMapper implements RowMapper<Ticket> {
		Map<Long, Ticket> ticketMap = new HashMap<>();

		@Override
		public Ticket mapRow(ResultSet rs, int rowNum) throws SQLException {
			if (ticketMap.containsKey(rs.getLong(SqlConstants.DbAttributes.TICKETID))) {
				Ticket ticket = ticketMap.get(rs.getLong(SqlConstants.DbAttributes.TICKETID));
				ticket.getCc().add(rs.getLong(SqlConstants.DbAttributes.TKTCCUSERID));
				List<ChecklistItem> checklistList = ticket.getChecklist();
				ChecklistItem checklistItem = new ChecklistItem();
				checklistItem.setId(rs.getLong(SqlConstants.DbAttributes.CHECKLISTID));
				checklistItem.setItem(rs.getString(SqlConstants.DbAttributes.CHECKLISTNAME));
				checklistItem.setChecked(rs.getBoolean(SqlConstants.DbAttributes.TCLCHECKED));
				checklistList.add(checklistItem);
			} else {
				Ticket ticket = new Ticket();
				ticket.setId(rs.getLong(SqlConstants.DbAttributes.TICKETID));
				ticket.setAppId(rs.getLong(SqlConstants.DbAttributes.APPID));
				ticket.setPinnedTicket(rs.getBoolean(SqlConstants.DbAttributes.PINNEDTICKET));
				ticket.setUpdatedTime(rs.getString(SqlConstants.DbAttributes.UPDATEDTIME));
				ticket.setUpdatedTimeTS(rs.getDate(SqlConstants.DbAttributes.UPDATEDTIME).getTime());
				ticket.setCreatedTime(rs.getString(SqlConstants.DbAttributes.CREATEDTIME));
				ticket.setCreatedTimeTS(rs.getDate(SqlConstants.DbAttributes.CREATEDTIME).getTime());
				ticket.setRate(rs.getInt(SqlConstants.DbAttributes.RATING));
				ticket.setMaxRating(rs.getInt(SqlConstants.DbAttributes.MAXRATING));
				ticket.setPriority(rs.getString(SqlConstants.DbAttributes.PRIORITY));
				ticket.setRequestedBy(rs.getLong(SqlConstants.DbAttributes.REQUESTEDBY));
				ticket.setDescription(rs.getString(SqlConstants.DbAttributes.DESCRIPTION));
				ticket.setNotes(rs.getString(SqlConstants.DbAttributes.NOTES));
				ticket.setActive(rs.getBoolean(SqlConstants.DbAttributes.ACTIVE));
				ticket.setSourceId(rs.getLong(SqlConstants.DbAttributes.SOURCEID));
				ticket.setHelpdeskId(rs.getLong(SqlConstants.DbAttributes.HELPDESKID));
				List<Long> ccList = new ArrayList<>();
				ccList.add(rs.getLong(SqlConstants.DbAttributes.TKTCCUSERID));
				ticket.setCc(ccList);
				List<ChecklistItem> checklistList = new ArrayList<>();
				ChecklistItem checklistItem = new ChecklistItem();
				checklistItem.setId(rs.getLong(SqlConstants.DbAttributes.CHECKLISTID));
				checklistItem.setItem(rs.getString(SqlConstants.DbAttributes.CHECKLISTNAME));
				checklistItem.setChecked(rs.getBoolean(SqlConstants.DbAttributes.TCLCHECKED));
				checklistList.add(checklistItem);
				ticket.setChecklist(checklistList);
				ticketMap.put(rs.getLong(SqlConstants.DbAttributes.TICKETID), ticket);
			}
			return null;
		}
	}

	public class OrgMapper implements RowMapper<Organization> {
		private Organization org = new Organization();
		List<User> adminList = new ArrayList<>();

		@Override
		public Organization mapRow(ResultSet rs, int rowNum) throws SQLException {
			User orgAdmin = new User();
			if (rowNum == 0) {
				getOrg().setId(rs.getLong(SqlConstants.DbAttributes.ID));
				getOrg().setOrgName(rs.getString(SqlConstants.DbAttributes.ORGNAME));
				getOrg().setUrl(rs.getString(SqlConstants.DbAttributes.URL));
				getOrg().setLogo(rs.getString(SqlConstants.DbAttributes.LOGO));
				getOrg().setEmailDomain(rs.getString(SqlConstants.DbAttributes.DOMAIN));
				getOrg().setOrgColor(rs.getString(SqlConstants.DbAttributes.COLOR));
				getOrg().setCreatedBy(rs.getLong(SqlConstants.DbAttributes.CREATEDBY));
				getOrg().setCreatedDate(rs.getString(SqlConstants.DbAttributes.CREATEDDATE));
				getOrg().setIsActive(rs.getBoolean(SqlConstants.DbAttributes.ISACTIVE));
				getOrg().setOrgDescription(rs.getString(SqlConstants.DbAttributes.DESCRIPTION));
			}

			orgAdmin.setId(rs.getLong(SqlConstants.DbAttributes.USERID));
			orgAdmin.setName(rs.getString(SqlConstants.DbAttributes.NAME));
			orgAdmin.setUsername(rs.getString(SqlConstants.DbAttributes.USERNAME));
			orgAdmin.setPhone(rs.getString(SqlConstants.DbAttributes.PHONE));
			adminList.add(orgAdmin);
			getOrg().setAdminDetails(adminList);
			return null;

		}

		public Organization getOrg() {
			return org;
		}

		public void setOrg(Organization org) {
			this.org = org;
		}
	}

}
