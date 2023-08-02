package org.upsmf.grievance.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import org.upsmf.grievance.dao.SuperAdminDao;
import org.upsmf.grievance.executor.MasterDataManager;
import org.upsmf.grievance.model.KeyFactory;
import org.upsmf.grievance.model.Organization;
import org.upsmf.grievance.model.S3Config;
import org.upsmf.grievance.model.StatusIdMap;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.model.mapper.SqlDataMapper;
import org.upsmf.grievance.model.mapper.SqlDataMapper.OrgMapper;
import org.upsmf.grievance.util.Constants;
import org.upsmf.grievance.util.DateUtil;
import org.upsmf.grievance.util.JsonKey;
import org.upsmf.grievance.util.OneWayHashing;
import org.upsmf.grievance.util.ProjectUtil;
import org.upsmf.grievance.util.S3FileManager;
import org.upsmf.grievance.util.SendMail;
import org.upsmf.grievance.util.Sql;
import org.upsmf.grievance.util.Sql.Apps;
import org.upsmf.grievance.util.Sql.Common;

@Repository(Constants.SUPER_ADMIN_DAO)
public class SuperAdminDaoImpl implements SuperAdminDao {

	private static final String ENCOUNTERED_AN_EXCEPTION_S = "Encountered an Exception :  %s";

	public static final Logger LOGGER = LoggerFactory.getLogger(SuperAdminDaoImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<Organization> getAllOrganization() {
		List<Organization> list = new ArrayList<>();
		try {
			list = jdbcTemplate.query(Sql.Organization.GET_ALL_ORG, new Object[] {},
					MasterDataManager.rowMapOrganizationModel);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return list;
	}

	@Override
	public boolean addAdmin(long userId) {
		try {
			MasterDataManager.getUserOrgMap().get(userId);
			int orgId = getOrganizationByUserId(userId);
			Long roleId = jdbcTemplate.queryForObject(Sql.UserQueries.GET_ROLE_ID_BY_ORG,
					new Object[] { orgId, Sql.Common.GRIEVANCE_ADMIN}, Long.class);

			Integer response = jdbcTemplate.update(Sql.UserQueries.ADD_ADMIN,
					new Object[] { roleId.intValue(), userId });
			if (response > 0) {
				MasterDataManager.getUserRoleMap().put(userId, roleId);
				return true;
			}
			return false;
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an Exception while Adding an Admin :  %s", e.getMessage()));
			return false;
		}

	}

	@Override
	public boolean removeAdmin(long userId) {
		try {
			int orgId = getOrganizationByUserId(userId);
			MasterDataManager.getUserOrgMap().get(userId);
			Long roleId = jdbcTemplate.queryForObject(Sql.UserQueries.GET_ROLE_ID_BY_ORG,
					new Object[] { orgId, Sql.Common.NODAL_OFFICER}, Long.class);
			Integer response = jdbcTemplate.update(Sql.UserQueries.DELETE_ADMIN,
					new Object[] { roleId.intValue(), userId });
			if (response > 0) {
				MasterDataManager.getUserRoleMap().put(userId, roleId);
				return true;
			}
			return false;
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an Exception while removing an Admin :  %s", e.getMessage()));
			return false;
		}

	}

	@Override
	public Organization addOrganization(Organization organization) {
		int insertSuccessful = 0;
		KeyHolder keyHolder = KeyFactory.getkeyHolder();
		List<Organization> list = jdbcTemplate.query(Sql.Organization.GET_ORG_BY_NAME,
				new Object[] { organization.getOrgName() }, MasterDataManager.rowMapOrganizationModel);
		if (list == null || list.isEmpty()) {
			Long userId = newOrgCreateAdmin(organization);
			if (userId == null) {
				return null;
			}
			try {
				insertSuccessful = jdbcTemplate.update(new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						String[] returnValColumn = new String[] { "id" };
						PreparedStatement statement = con.prepareStatement(Sql.Organization.ADD_NEW_ORG,
								returnValColumn);
						statement.setString(1, organization.getOrgName());
						statement.setString(2, organization.getUrl());
						statement.setString(3, organization.getLogo());
						statement.setLong(4, userId);
						statement.setLong(5, userId);
						statement.setString(6, organization.getOrgDescription());
						statement.setString(7, organization.getOrgColor());
						statement.setString(8, organization.getEmailDomain());
						return statement;
					}
				}, keyHolder);
				organization.setId(keyHolder.getKey().longValue());
				MasterDataManager.getOrgIdAndOrgNameMap().put(organization.getId(), organization.getOrgName());
			} catch (Exception e) {
				LOGGER.error(
						String.format("Encountered an Exception while Adding an Organization :  %s", e.getMessage()));
			}
			if (insertSuccessful > 0) {
				jdbcTemplate.update(Sql.UserQueries.NEW_ORG_AUTH, new Object[] { organization.getId(),
						Integer.valueOf(organization.getAuthId()), organization.getUrl(), organization.getEmailDomain() });
				orgSetup(organization, userId);
				return organization;
			}
		}
		return null;
	}

	public Long newOrgCreateAdmin(final Organization org) {
		KeyHolder keyHolder = KeyFactory.getkeyHolder();
		try {
			Long list = jdbcTemplate.queryForObject(Sql.UserQueries.USER_DATA,
					new Object[] { org.getAdminDetails().get(0).getUsername() }, Long.class);
			if (list == 0) {
				jdbcTemplate.update(new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
						String[] returnValColumn = new String[] { "id" };
						PreparedStatement statement = connection.prepareStatement(Sql.Organization.NEW_GRIEVANCE_ADMIN_USER,
								returnValColumn);
						statement.setString(1, org.getAdminDetails().get(0).getName());
						statement.setString(2, org.getAdminDetails().get(0).getUsername());
						statement.setString(3, org.getAdminDetails().get(0).getPhone());
						statement.setString(4, org.getAdminDetails().get(0).getImagePath());
						return statement;
					}
				}, keyHolder);
				return keyHolder.getKey().longValue();
			}
			MasterDataManager.getAllUserRoles();
		} catch (Exception e) {
			LOGGER.error(
					String.format("Encountered an Exception while Adding an Admin for New Org :  %s", e.getMessage()));
		}

		return null;
	}

	public void orgSetup(final Organization org, Long newGrievanceAdminUserId) {
		Long id = org.getId();
		if (id > 0) {
			LOGGER.info("Org Id: {}", id);
		}
		if (newGrievanceAdminUserId > 0) {
			LOGGER.info("User Id : {}", newGrievanceAdminUserId);
		}
		String password = null;
		int adminRoleId = 0;
		try {
			for (Constants.userRole roleName : Constants.userRole.values()) {
				Boolean value = jdbcTemplate.queryForObject(Sql.Organization.CHECK_IF_ROLE_EXISTS,
						new Object[] { String.valueOf(roleName), id }, Boolean.class);
				if (!value) {
					jdbcTemplate.update(Sql.Organization.ADD_ORG_ROLES, new Object[] { String.valueOf(roleName), id });
				}
				List<Integer> actions = new ArrayList<>();
				Integer roleId = jdbcTemplate.queryForObject(Sql.Organization.GET_ROLE_ID_BY_ORG,
						new Object[] { id, String.valueOf(roleName) }, Integer.class);
				if (String.valueOf(roleName).equals(Common.GRIEVANCE_ADMIN)) {
					adminRoleId = roleId;
					actions = Constants.getGrievanceAdminActions();
				}
				if (String.valueOf(roleName).equals(Common.NODAL_OFFICER)) {
					actions = Constants.getNodalOfficerActions();
				}
				for (int i = 0; i < actions.size(); i++) {
					jdbcTemplate.update(Sql.Organization.ADD_ROLE_PERMISSION, new Object[] { roleId, actions.get(i) });
				}
			}
			jdbcTemplate.update(Sql.Organization.NEW_GRIEVANCE_ADMIN_ROLE, new Object[] { newGrievanceAdminUserId, adminRoleId });
			MasterDataManager.getAllUserRoles();
			jdbcTemplate.update(Sql.Organization.FIRST_ADMIN_COMP, new Object[] { id, newGrievanceAdminUserId });
			MasterDataManager.getUserOrgMap().put(newGrievanceAdminUserId, id);
			password = ProjectUtil.getRandomStringVal().trim();
			if (!StringUtils.isBlank(password)) {
				LOGGER.info("New Admin Password : {}", password);
			}
			String encodedPwd = OneWayHashing.encryptVal(password);
			jdbcTemplate.update(Sql.Organization.NEW_GRIEVANCE_ADMIN_PSWRD, new Object[] { encodedPwd, newGrievanceAdminUserId });
			if (!StringUtils.isBlank(password)) {
				LOGGER.info("Password : {}", password);
			}
			sendAdminMail(org.getAdminDetails().get(0).getUsername(), org.getAdminDetails().get(0).getName(), password);
		} catch (Exception e) {
			LOGGER.error(
					String.format("Encountered an Exception while setting up an Organization :  %s", e.getMessage()));
		}

	}

	private void sendAdminMail(String email, String firstName, String password) {
		VelocityContext context = new VelocityContext();
		try {
			context.put(JsonKey.MAIL_SUBJECT, "Welcome to Aurora-Desk!");
			context.put(JsonKey.MAIL_BODY, "You have been successfully added as the Org Admin to the system"
					+ " Please find your username and password");
			context.put(JsonKey.PSWRD, password);
			context.put(JsonKey.USER_NAME, email);
			context.put(JsonKey.FIRST_NAME, firstName);

			SendMail.sendMail(new String[] { email }, "Welcome To Aurora-Desk", context, "email_template.vm");
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an Exception while sending an Admin Mail :  %s", e.getMessage()));
		}
	}

	@Override
	public S3Config getS3Access() {
		S3Config exp = null;
		try {
			exp = jdbcTemplate.query(Sql.GET_S3_ACCESS, MasterDataManager.rowMapS3Config).get(0);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return exp;
	}

	@Override
	public boolean updateOrganizationById(Organization org) {
		int value = 0;
		try {
			value = jdbcTemplate.update(Sql.Organization.UPDATE_ORG,
					new Object[] { org.getOrgName(), org.getLogo(), DateUtil.getFormattedDateInUTC(new Date()),
							org.getUserId(), org.getUrl(), org.getOrgDescription(), org.getOrgColor(),
							org.getEmailDomain(), org.getId() });
		} catch (Exception e) {
			LOGGER.error(
					String.format("Encountered an Exception while updating Organization by ID :  %s", e.getMessage()));
		}
		return (value > 0);
	}

	private String getOrgLogo(Organization org) {
		if (org.getLogo() != null) {
			try {
				S3Config s3values = jdbcTemplate.query(Sql.GET_S3_ACCESS, MasterDataManager.rowMapS3Config).get(0);
				String url = null;
				url = S3FileManager.getPreSignedURL(s3values, org.getLogo());
				return url;
			} catch (Exception e) {
				return "";
			}
		}
		return "";
	}

	@Override
	public boolean deleteOrganization(Organization organization) {
		try {
			int value = jdbcTemplate.update(Sql.Organization.DELETE_ORG, new Object[] { organization.getUserId(),
					DateUtil.getFormattedDateInUTC(new Date()), organization.getId() });
			return (value > 0);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return false;
	}

	@Override
	public int getOrganizationByUserId(long authUserId) {
		try {
			return jdbcTemplate.query(Sql.Organization.GET_ORG_BY_USERID, new Object[] { authUserId },
					MasterDataManager.rowMapOrganizationModel).get(0).getId().intValue();
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return 0;
	}

	@Override
	public boolean mapUserToOrg(long userId, int organisationId) {
		try {
			Integer response = jdbcTemplate.update(Sql.UserQueries.MAP_USER_TO_ORG,
					new Object[] { userId, organisationId });
			return (response > 0);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return false;
	}

	@Override
	public User userDetailsByUserId(long userId) {
		try {
			List<User> list = jdbcTemplate.query(Sql.UserQueries.USER, new Object[] { userId },
					MasterDataManager.rowMapUser);
			return list.get(0);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return null;
	}

	@Override
	public boolean mapAppsToOrg(StatusIdMap statusIdMap) {

		try {
			LOGGER.debug("Query to execute : " + Apps.DELETE_ORG_APP);
			Integer response1 = jdbcTemplate.update(Sql.Apps.DELETE_ORG_APP,
					new Object[] { statusIdMap.getAppId(), statusIdMap.getOrgId() });
			Integer response = jdbcTemplate.update(Sql.Apps.MAP_APP_TO_ORG,
					new Object[] { statusIdMap.getAppId(), statusIdMap.getOrgId() });
			return (response > 0 && response1 > 0);
		} catch (Exception e) {
			LOGGER.error(String.format("Exception in mapAppToOrg daoImpl %s", e.getMessage()));
			return false;

		}
	}

	@Override
	public List<Organization> getOrganizationByUser(Long userId) {
		try {
			List<Organization> org = jdbcTemplate.query(Sql.Organization.GET_ORG_BY_USERID, new Object[] { userId },
					MasterDataManager.rowMapOrganizationModel);
			for (int i = 0; i < org.size(); i++) {
				String url = getOrgLogo(org.get(i));
				if (!url.isEmpty()) {
					org.get(i).setLogo(url);
				} else {
					org.get(i).setLogo(null);
				}
			}
			return org;
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
			return new ArrayList<>();
		}
	}

	@Override
	public Organization getOrganizationByIdV2(Long id) {
		try {
			OrgMapper orgMapper = new SqlDataMapper().new OrgMapper();
			jdbcTemplate.query(Sql.Organization.ORG_BY_ID, new Object[] { true, true, id, id, Sql.Common.GRIEVANCE_ADMIN},
					orgMapper);
			String url = getOrgLogo(orgMapper.getOrg());
			if (!url.isEmpty()) {
				orgMapper.getOrg().setLogo(url);
			} else {
				orgMapper.getOrg().setLogo(null);
			}
			return orgMapper.getOrg();
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an Exception in getOrganizationByIdV2  :  %s", e.getMessage()));
		}
		return null;
	}

}
