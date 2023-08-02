package org.upsmf.grievance.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import org.upsmf.grievance.dao.RoleDao;
import org.upsmf.grievance.dao.SuperAdminDao;
import org.upsmf.grievance.executor.MasterDataManager;
import org.upsmf.grievance.model.Organization;
import org.upsmf.grievance.model.Role;
import org.upsmf.grievance.model.mapper.SqlDataMapper;
import org.upsmf.grievance.model.mapper.SqlDataMapper.RoleActionMapper;
import org.upsmf.grievance.util.Constants;
import org.upsmf.grievance.util.ProjectUtil;
import org.upsmf.grievance.util.Sql;
import org.upsmf.grievance.util.Sql.Common;
import org.upsmf.grievance.util.Sql.RoleAction;

@Repository(Constants.ROLE_DAO)
public class RoleDaoImpl implements RoleDao {

	private static final String ENCOUNTERED_AN_EXCEPTION_WHILE_SAVING_THE_ROLE_DETAILS_S = "Encountered an exception while saving the Role Details  : %s";

	private static final String ENCOUNTERED_AN_EXCEPTION_WHILE_FETCHING_ALL_ROLES_S = "Encountered an exception while fetching all roles %s";

	private static final String ENCOUNTERED_AN_EXCEPTION_S = "Encountered an Exception : %s";

	public static final Logger LOGGER = LoggerFactory.getLogger(RoleDaoImpl.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	private SuperAdminDao superAdminDao;

	@Override
	public Role saveRole(Role role) {
		int saveRole = 0;
		try {
			saveRole = jdbcTemplate.update(RoleAction.SAVE_NEW_ROLE, new Object[] { role.getName(), role.getOrgId() });
		} catch (Exception ex) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_SAVING_THE_ROLE_DETAILS_S, ex.getMessage()));
		}
		if (saveRole > 0) {
			return role;
		}
		return null;
	}

	@Override
	public Role updateRole(Role role) {
		int updateRole = 0;
		try {
			updateRole = jdbcTemplate.update(RoleAction.UPDATE_ROLE,
					new Object[] { role.getName(), role.getId(), role.getOrgId() });
		} catch (Exception ex) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_SAVING_THE_ROLE_DETAILS_S, ex.getMessage()));
		}
		if (updateRole > 0) {
			return role;
		}
		return null;
	}

	@Override
	public List<Role> getAllRoles(Long orgId) {
		List<Role> roleList = null;
		String query = RoleAction.GET_ALL_ROLES + Common.WHERE_CLAUSE + RoleAction.ORG_ID_CONDITION;
		try {
			roleList = jdbcTemplate.query(query, new Object[] { orgId }, MasterDataManager.rowMapRole);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_FETCHING_ALL_ROLES_S, e.getMessage()));
		}
		return roleList;
	}

	@Override
	public String findById(Role role) {
		String roleList = null;
		try {
			roleList = jdbcTemplate.queryForObject(RoleAction.SELECT_ROLES_ON_ID,
					new Object[] { role.getId(), role.getOrgId() }, String.class);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_FETCHING_ALL_ROLES_S, e.getMessage()));
		}
		if (roleList != null) {
			return roleList;
		}
		return null;
	}

	@Override
	public List<Role> getAllOrgRoles() {
		List<Role> roleList = null;
		try {
			roleList = jdbcTemplate.query(RoleAction.GET_ALL_ROLES, MasterDataManager.rowMapRole);
			return roleList;
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_FETCHING_ALL_ROLES_S, e.getMessage()));
		}
		return new ArrayList<>();
	}

	@Override
	public void initializeActions() {

		try {
			Iterator<Entry<Integer, List<String>>> it = Constants.getActions().entrySet().iterator();
			while (it.hasNext()) {
				Entry<Integer, List<String>> pair = it.next();
				Boolean value = jdbcTemplate.queryForObject(Sql.Organization.CHECK_IF_ACTION_EXISTS,
						new Object[] { pair.getKey() }, Boolean.class);
				if (!value) {
					if (!ProjectUtil.isObjectListNullOrEmpty(Constants.getActions().get(pair.getKey()))
							&& Constants.getActions().get(pair.getKey()).size() == 3) {
						jdbcTemplate.update(Sql.UserQueries.INSERT_ACTION,
								new Object[] { pair.getKey(), Constants.getActions().get(pair.getKey()).get(0),
										Constants.getActions().get(pair.getKey()).get(1),
										Constants.getActions().get(pair.getKey()).get(2) });
					}
				} else if (!ProjectUtil.isObjectListNullOrEmpty(Constants.getActions().get(pair.getKey()))
						&& Constants.getActions().get(pair.getKey()).size() == 3) {
					jdbcTemplate.update(Sql.UserQueries.UPDATE_ACTION,
							new Object[] { Constants.getActions().get(pair.getKey()).get(0),
									Constants.getActions().get(pair.getKey()).get(1),
									Constants.getActions().get(pair.getKey()).get(2), pair.getKey() });
				}

			}
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}

	}

	@Override
	public void intializeRolesAndActions() {
		Long roleId = null;
		List<Organization> organizations = superAdminDao.getAllOrganization();
		for (int i = 0; i < organizations.size(); i++) {
			for (Constants.userRole roleName : Constants.userRole.values()) {
				try {
					roleId = jdbcTemplate.queryForObject(Sql.Organization.GET_ROLE_ID_FROM_ORG,
							new Object[] { String.valueOf(roleName), organizations.get(i).getId() }, Long.class);
					jdbcTemplate.update(Sql.Organization.DELETE_ACTION, new Object[] { roleId });
					List<Integer> actions;
					switch (String.valueOf(roleName)) {
					case Common.SUPER_ADMIN:
						actions = Constants.getSuperadminactions();
						break;
					case Common.GRIEVANCE_ADMIN:
						actions = Constants.getGrievanceAdminActions();
						break;
					case Common.NODAL_OFFICER:
						actions = Constants.getNodalOfficerActions();
						break;
					default:
						actions = new ArrayList<>();
					}
					for (int j = 0; j < actions.size(); j++) {
						jdbcTemplate.update(Sql.Organization.ADD_ROLE_PERMISSION,
								new Object[] { roleId, actions.get(j) });
					}
				} catch (Exception e) {
					LOGGER.info(String.format("Role Doesn't exist as per mapped, Please check. %s", roleId.toString()));
				}
			}
		}

	}

	@Override
	public Map<Long, List<String>> getAllActionsForRoles(List<Long> roleIds) {
		RoleActionMapper mapper = new SqlDataMapper().new RoleActionMapper();
		String queryToExecute = RoleAction.GET_ALL_ACTIONS_FOR_ROLES + getIdQuery(roleIds);
		try {
			jdbcTemplate.query(queryToExecute, mapper);
			return mapper.getRoleActionsMap();
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_FETCHING_ALL_ROLES_S, e.getMessage()));
		}
		return null;
	}

	private static String getIdQuery(final List<Long> idList) {
		final StringBuilder query = new StringBuilder("(");
		if (!idList.isEmpty()) {
			query.append(idList.get(0).toString());
			for (int i = 1; i < idList.size(); i++) {
				query.append(", " + idList.get(i));
			}
		}
		return query.append(")").toString();
	}
}
