package org.upsmf.grievance.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import org.upsmf.grievance.dao.ApplicationDao;
import org.upsmf.grievance.dao.HelpdeskDao;
import org.upsmf.grievance.dao.SuperAdminDao;
import org.upsmf.grievance.dto.HelpdeskDto;
import org.upsmf.grievance.dto.HelpdeskTypeDto;
import org.upsmf.grievance.dto.HelpdeskWorkflowDto;
import org.upsmf.grievance.executor.MasterDataManager;
import org.upsmf.grievance.model.ChecklistItem;
import org.upsmf.grievance.model.HelpDeskApp;
import org.upsmf.grievance.model.Helpdesk;
import org.upsmf.grievance.model.KeyFactory;
import org.upsmf.grievance.model.S3Config;
import org.upsmf.grievance.model.StatusIdMap;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.model.mapper.SqlDataMapper;
import org.upsmf.grievance.model.mapper.SqlDataMapper.HelpdeskRowRecordMapper;
import org.upsmf.grievance.util.Constants;
import org.upsmf.grievance.util.DateUtil;
import org.upsmf.grievance.util.JsonKey;
import org.upsmf.grievance.util.ProjectUtil;
import org.upsmf.grievance.util.S3FileManager;
import org.upsmf.grievance.util.SendMail;
import org.upsmf.grievance.util.Sql;

@Repository
public class HelpdeskDaoImpl implements HelpdeskDao {

	private static final String ENCOUNTERED_AN_EXCEPTION_WHILE_FETCHING_USERS_FOR_HELPDESK_S = "Encountered an exception while fetching Users for Helpdesk :  : %s";

	private static final String ENCOUNTERED_AN_EXCEPTION_WHILE_DELETING_WORKFLOW_STAGES_FOR_A_HELPDESK_TYPE_S = "Encountered an Exception while deleting Workflow Stages for a Helpdesk Type :  : %s";

	public static final Logger LOGGER = LoggerFactory.getLogger(HelpdeskDaoImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ApplicationDao appDao;

	@Autowired
	private SuperAdminDao superAdminDao;

	@Override
	public boolean createUpdateHelpdesk(Helpdesk helpdesk) {
		KeyHolder keyHolder = KeyFactory.getkeyHolder();
		if (ProjectUtil.isObjectNull(helpdesk.getId())) {
			try {
				jdbcTemplate.update(new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						String[] returnValColumn = new String[] { "id" };
						PreparedStatement statement = con.prepareStatement(Sql.Helpdesk.CREATE_HELPDESK,
								returnValColumn);
						statement.setString(1, helpdesk.getName());
						statement.setLong(2, helpdesk.getOrgId());
						statement.setLong(3, helpdesk.getCreatedBy());
						statement.setString(4, DateUtil.getFormattedDateInUTC(new Date()));
						statement.setBoolean(5, helpdesk.getIsActive());
						statement.setString(6, helpdesk.getColor());
						statement.setString(7, helpdesk.getDescription());
						return statement;
					}
				}, keyHolder);
				helpdesk.setId((Long) keyHolder.getKey());
				setHelpdeskChannels(helpdesk);
				return true;
			} catch (Exception e) {
				LOGGER.error(String.format("Encountered exception in create daoImpl : %s", e.getMessage()));
			}
		} else {
			try {
				jdbcTemplate.update(Sql.Helpdesk.UPDATE_HELPDESK,
						new Object[] { helpdesk.getName(), helpdesk.getUpdatedBy(),
								DateUtil.getFormattedDateInUTC(new Date()), helpdesk.getIsActive(), helpdesk.getColor(),
								helpdesk.getDescription(), helpdesk.getId(), helpdesk.getOrgId() });
				setHelpdeskChannels(helpdesk);
				return true;
			} catch (Exception e) {
				LOGGER.error(String.format("Encountered exception in updating Helpdesk daoImpl : %s", e.getMessage()));
			}
		}
		return false;
	}

	private void setHelpdeskChannels(Helpdesk helpdesk) {
		if (updateAdminAndAppIds(helpdesk) && helpdesk.getDirect() != null && helpdesk.getPlaystore() != null
				&& helpdesk.getAppstore() != null && helpdesk.getAurora_sdk() != null) {
			jdbcTemplate.update(Sql.Helpdesk.UPDATE_HELPDESK_CHANNELS,
					new Object[] { helpdesk.getDirect(), helpdesk.getPlaystore(), helpdesk.getAppstore(),
							helpdesk.getAurora_sdk(), helpdesk.getId(), helpdesk.getOrgId() });
		}
	}

	private boolean updateAdminAndAppIds(Helpdesk helpdesk) {

		if (helpdesk != null) {
			// jdbcTemplate.update(Sql.UserQueries.REMOVE_ALL_HELPDESK_SOURCE, new Object[]
			// { helpdesk.getId() });
			// for (int i = 0; i < helpdesk.getSourceId().size(); i++) {
			// jdbcTemplate.update(Sql.Ticket.ADD_SOURCE_TO_HELPDESK,
			// new Object[] { helpdesk.getId(), helpdesk.getSourceId().get(i) });
			// }
			boolean value = addUpdateHelpdeskAdmins(helpdesk);
			boolean values = addUpdateHelpdeskUsers(helpdesk);
			if (value && values) {
				for (int i = 0; i < helpdesk.getAppIds().size(); i++) {
					StatusIdMap statusIdMap = new StatusIdMap();
					statusIdMap.setHelpdeskId(helpdesk.getId());
					statusIdMap.setHelpdeskAppStatus(true);
					statusIdMap.setAppId(helpdesk.getAppIds().get(i));
					appDao.mapAppsToHelpdesk(statusIdMap);
				}
				MasterDataManager.getHelpdeskObjectFromHelpdeskId();
			}
			return true;
		}
		return false;
	}

	@Override
	public List<HelpdeskDto> getAllHelpdesks(Long orgId) {
		try {
			return jdbcTemplate.query(Sql.Helpdesk.GET_ORG_HELPDESK, new Object[] { orgId, true },
					MasterDataManager.rowMapHelpdeskDto);
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered exception in getOrgHelpdesk daoImpl : %s", e.getMessage()));
			return new ArrayList<>();
		}

	}

	@Override
	public HelpdeskRowRecordMapper getHelpdeskForId(Long orgId, Long id) {
		HelpdeskRowRecordMapper rowMapper = new SqlDataMapper().new HelpdeskRowRecordMapper();
		try {
			jdbcTemplate.query(Sql.Helpdesk.GET_HELPDESK_FOR_ID, new Object[] { id, orgId }, rowMapper);
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered exception in getHelpdeskForId daoImpl : %s", e.getMessage()));
		}
		return rowMapper;
	}

	@Override
	public List<ChecklistItem> getChecklistItemsForHelpdesk(Long helpdeskId, Long typeId) {
		List<ChecklistItem> checklistItems = new ArrayList<>();
		try {
			checklistItems = jdbcTemplate.query(Sql.Helpdesk.GET_CHECKLIST_FOR_HELPDESK,
					new Object[] { helpdeskId, typeId }, new SqlDataMapper().new ChecklistItemMapper());
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered exception in getHelpdeskForId daoImpl : %s", e.getMessage()));
		}
		return checklistItems;
	}

	@Override
	public List<Long> getHelpdeskAdmins(Long id) {
		List<Long> list = null;
		try {
			list = jdbcTemplate.queryForList(Sql.UserQueries.GET_HELPDESK_ADMINS, new Object[] { id }, Long.class);
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an Exception :  : %s", e.getMessage()));
		}
		return list;

	}

	@Override
	public boolean addUpdateHelpdeskAdmins(Helpdesk helpdesk) {
		try {
			List<Long> oldAdmins = getHelpdeskAdmins(helpdesk.getId());
			for (Long admin : helpdesk.getAdminIds()) {
				if (oldAdmins.contains(admin)) {
					oldAdmins.remove(admin);
				}
			}
			for (int i = 0; i < oldAdmins.size(); i++) {
				User user = superAdminDao.userDetailsByUserId(oldAdmins.get(i));
				String email = user.getUsername();
				Map<String, String> keyValue = new HashMap<>();
				keyValue.put(JsonKey.FIRST_NAME, user.getName());
				keyValue.put(JsonKey.HELPDESKNAME, helpdesk.getName());
				String[] emails = email.split(",");
				SendMail.sendMail(keyValue, emails, Constants.DELETE_HELPDESK_ADMIN, "remove_helpdeskadmin.vm");
			}
			jdbcTemplate.update(Sql.UserQueries.REMOVE_ALL_HELPDESK_ADMIN, new Object[] { helpdesk.getId() });
			if (helpdesk != null && helpdesk.getAdminIds() != null) {
				for (int i = 0; i < helpdesk.getAdminIds().size(); i++) {
					jdbcTemplate.update(Sql.UserQueries.ADD_ADMINS_TO_HELPDESK,
							new Object[] { helpdesk.getId(), helpdesk.getAdminIds().get(i) });
					User user = superAdminDao.userDetailsByUserId(helpdesk.getAdminIds().get(i));
					user.setOrgId(MasterDataManager.getUserOrgMap().get(helpdesk.getAdminIds().get(i)));
					String email = user.getUsername();
					Map<String, String> keyValue = new HashMap<>();
					keyValue.put(JsonKey.FIRST_NAME, user.getName());
					keyValue.put(JsonKey.HELPDESKNAME, helpdesk.getName());
					LOGGER.info(helpdesk.getName());
					String[] emails = email.split(",");
					SendMail.sendMail(keyValue, emails, Constants.NEW_HELPDESK_ADMIN, "add-helpdeskadmin-aurora.vm");
				}
			}
			return true;
		} catch (DataAccessException e) {
			LOGGER.error(
					String.format("Encountered an Exception while adding admins to helpdesk :  : %s", e.getMessage()));
		} catch (Exception ex) {
			LOGGER.error(
					String.format("Encountered an Exception while adding admins to helpdesk :  : %s", ex.getMessage()));
		}
		return false;
	}

	@Override
	public Boolean addTypeForHelpdesk(HelpdeskTypeDto dto, Long helpdeskId) {
		Long id = 0l;
		try {
			KeyHolder keyHolder = KeyFactory.getkeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					String[] returnValColumn = new String[] { "id" };
					PreparedStatement statement = con.prepareStatement(Sql.Helpdesk.INSERT_TYPE_FOR_HELPDESK,
							returnValColumn);
					statement.setLong(1, helpdeskId);
					statement.setString(2, dto.getName());
					return statement;
				}
			}, keyHolder);
			id = keyHolder.getKey().longValue();
			dto.setId(id);
		} catch (Exception e) {
			LOGGER.error(
					String.format("Encountered an Exception while adding type for a Helpdesk :  : %s", e.getMessage()));
		}
		return true;
	}

	@Override
	public Boolean addWorkflowForHelpdeskType(HelpdeskTypeDto helpdeskTypeDto) {
		int[] values = null;
		try {
			values = jdbcTemplate.batchUpdate(Sql.Helpdesk.INSERT_WORKFLOW_FOR_HELPDESK_TYPE,
					new BatchPreparedStatementSetter() {
						@Override
						public void setValues(java.sql.PreparedStatement statement, int i) throws SQLException {
							HelpdeskWorkflowDto workflowDto = helpdeskTypeDto.getWorkflowStages().get(i);
							statement.setString(1, workflowDto.getName());
							statement.setLong(2, helpdeskTypeDto.getId());
						}

						@Override
						public int getBatchSize() {
							return helpdeskTypeDto.getWorkflowStages().size();
						}
					});
		} catch (Exception e) {
			LOGGER.error(String.format("Exception Occured while mapping Products to Order : %s", e.getMessage()));

		}
		return (values != null && values.length > 0);

	}

	@Override
	public Boolean addChecklistForHelpdeskType(HelpdeskTypeDto helpdeskTypeDto, Long helpdeskId) {
		Long id = 0l;
		try {
			for (ChecklistItem item : helpdeskTypeDto.getChecklistItems()) {
				KeyHolder keyHolder = KeyFactory.getkeyHolder();
				jdbcTemplate.update(new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						String[] returnValColumn = new String[] { "id" };
						PreparedStatement statement = con.prepareStatement(Sql.Helpdesk.INSERT_CHECKLIST_FOR_HDTYPE,
								returnValColumn);
						statement.setString(1, item.getItem());
						return statement;
					}
				}, keyHolder);
				id = keyHolder.getKey().longValue();
				item.setId(id);
				jdbcTemplate.update(Sql.Helpdesk.MAP_CHECKLIST_HDTYPE,
						new Object[] { helpdeskId, item.getId(), helpdeskTypeDto.getId() });
			}
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an Exception while adding checklist type for a Helpsedk :  : %s",
					e.getMessage()));
		}
		return true;
	}

	@Override
	public boolean addUpdateHelpdeskUsers(Helpdesk helpdesk) {
		try {
			if (helpdesk.getAllowAllUsers()) {
				jdbcTemplate.update(Sql.UserQueries.UPDATE_ALLOW_ALL_USERS,
						new Object[] { helpdesk.getAllowAllUsers(), helpdesk.getId() });
				jdbcTemplate.update(Sql.UserQueries.REMOVE_ALL_USERS_FROM_HELPDESK, new Object[] { helpdesk.getId() });
			} else {
				jdbcTemplate.update(Sql.UserQueries.UPDATE_ALLOW_ALL_USERS,
						new Object[] { helpdesk.getAllowAllUsers(), helpdesk.getId() });
				jdbcTemplate.update(Sql.UserQueries.REMOVE_ALL_USERS_FROM_HELPDESK, new Object[] { helpdesk.getId() });
				for (int i = 0; i < helpdesk.getUserIds().size(); i++) {
					jdbcTemplate.update(Sql.UserQueries.ADD_USERS_TO_HELPDESK,
							new Object[] { helpdesk.getId(), helpdesk.getUserIds().get(i) });
				}
			}
			return true;
		} catch (Exception ex) {
			LOGGER.error(
					String.format("Encountered an Exception while adding users to helpdesk :  : %s", ex.getMessage()));
		}
		return false;
	}

	@Override
	public Boolean deleteTypeForHelpdesk(Long helpdeskId) {
		int status = 0;
		try {
			status = jdbcTemplate.update(Sql.Helpdesk.DELETE_TYPE_FOR_HELPDESK, new Object[] { helpdeskId });
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_DELETING_WORKFLOW_STAGES_FOR_A_HELPDESK_TYPE_S,
					e.getMessage()));
			return false;
		}

		return (status > 0);
	}

	@Override
	public Boolean deleteWorkflowForHelpdeskType(Long typeId) {
		int status = 0;
		try {
			status = jdbcTemplate.update(Sql.Helpdesk.DELETE_WORKFLOW_FOR_HELPDESKTYPE, new Object[] { typeId });
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_DELETING_WORKFLOW_STAGES_FOR_A_HELPDESK_TYPE_S,
					e.getMessage()));
			return false;
		}

		return (status > 0);
	}

	@Override
	public Boolean deleteChecklistForHelpdeskType(Long helpdeskId, Long typeId) {
		int status = 0;
		try {
			status = jdbcTemplate.update(Sql.Helpdesk.DELETE_CHECKLIST_FOR_HDTYPE, new Object[] { helpdeskId, typeId });
		} catch (Exception e) {
			LOGGER.error(
					String.format("Encountered an Exception while deleting Workflow Stages for a Helpdesk Type : : %s ",
							e.getMessage()));
			return false;
		}

		return (status > 0);
	}

	@Override
	public List<HelpDeskApp> getAppIdAndHelpDeskId() {
		List<HelpDeskApp> helpdeskApp = null;
		try {
			helpdeskApp = jdbcTemplate.query(Sql.UserQueries.GET_APP_ID_HELPDESK_ID,
					MasterDataManager.rowMapHelpDeskApp);
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an exception while fetching all roles : %s", e.getMessage()));
		}
		return helpdeskApp;
	}

	@Override
	public List<Helpdesk> getHelpdeskObjectFromHelpdeskId() {
		List<Helpdesk> helpdesk = null;
		try {
			helpdesk = jdbcTemplate.query(Sql.UserQueries.GET_HELPDESK_ID_HELPDESK_OBJECT,
					MasterDataManager.rowMapHelpdesk);
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an exception while fetching all roles : %s", e.getMessage()));
		}
		return helpdesk;
	}

	@Override
	public List<User> getAdminForHelpeskId(Long helpdeskId) {
		List<User> helpdeskUsers = null;
		try {
			helpdeskUsers = jdbcTemplate.query(Sql.Helpdesk.GET_HELPDESK_ADMIN_BY_ID, new Object[] { helpdeskId },
					new SqlDataMapper().new HelpdeskUserMapper());
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_FETCHING_USERS_FOR_HELPDESK_S, e.getMessage()));
		}
		return helpdeskUsers;
	}

	@Override
	public List<User> getUsersForHelpeskId(Long helpdeskId) {
		List<User> helpdeskUsers = new ArrayList<>();
		List<HelpdeskDto> data = new ArrayList<>();
		try {
			data = jdbcTemplate.query(Sql.Helpdesk.GET_HELPDESK_BY_ID, new Object[] { helpdeskId },
					MasterDataManager.rowMapHelpdeskDto);
			if (!data.isEmpty()) {
				HelpdeskDto helpdesk = data.get(0);
				if (helpdesk.getAllowAllUsers()) {
					helpdeskUsers = jdbcTemplate.query(Sql.Helpdesk.GET_USER_DETAILS_FOR_HELPDESK,
							new Object[] { helpdesk.getOrgId() }, new SqlDataMapper().new HelpdeskUserMapper());
				} else {
					helpdeskUsers = jdbcTemplate.query(Sql.Helpdesk.GET_HELPDESK_USER_BY_ID,
							new Object[] { helpdeskId }, new SqlDataMapper().new HelpdeskUserMapper());
				}
			}
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_FETCHING_USERS_FOR_HELPDESK_S, e.getMessage()));
		}
		return helpdeskUsers;
	}

	@Override
	public List<HelpdeskDto> getHelpdeskAdminUser(List<HelpdeskDto> helpdeskList) {
		try {
			for (int i = 0; i < helpdeskList.size(); i++) {
				HelpdeskDto dto = helpdeskList.get(i);
				List<User> admins = jdbcTemplate.query(Sql.Helpdesk.GET_HELPDESK_ADMIN_USER,
						new Object[] { dto.getId() }, MasterDataManager.rowMapUser);
				dto.setUsers(getUsersForHelpeskId(dto.getId()));
				admins = getImageUrl(admins);
				dto.setAdmins(admins);
			}

			return helpdeskList;

		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an exception in getHelpdeskAdminUser :  : %s", e.getMessage()));
			return new ArrayList<>();
		}

	}

	private List<User> getImageUrl(List<User> userList) {
		S3Config s3values = superAdminDao.getS3Access();
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).getImagePath() != null && !userList.get(i).getImagePath().isEmpty()
					&& userList.get(i).getImagePath().contains("userprofile")) {
				String url = null;
				url = S3FileManager.getPreSignedURL(s3values, userList.get(i).getImagePath());
				userList.get(i).setImagePath(url);
			}
		}
		return userList;

	}

	@Override
	public List<Helpdesk> getHelpdeskByUserId(Long userId) {
		List<Helpdesk> helpdeskUsers = null;
		Long orgId = MasterDataManager.getUserOrgMap().get(userId);
		try {
			helpdeskUsers = jdbcTemplate.query(Sql.Helpdesk.GET_HELPDESK_BY_USER_ID,
					new Object[] { userId, userId, orgId }, new SqlDataMapper().new UserHelpdeskMapper());
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_FETCHING_USERS_FOR_HELPDESK_S, e.getMessage()));
		}
		return helpdeskUsers;
	}
}
