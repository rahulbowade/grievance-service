package org.upsmf.grievance.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.upsmf.grievance.dao.RoleDao;
import org.upsmf.grievance.model.Role;
import org.upsmf.grievance.service.RoleActionService;
import org.upsmf.grievance.util.Constants;

@Service(value = Constants.ROLE_ACTION_SERVICE)
public class RoleActionServiceImpl implements RoleActionService {

	@Autowired
	private RoleDao roleDao;

	@Override
	public Role saveRole(Role role) {
		return roleDao.saveRole(role);
	}

	@Override
	public Role updateRole(Role role) {
		return roleDao.updateRole(role);
	}

	@Override
	public List<Role> getAllRoles(Long orgId) {
		return roleDao.getAllRoles(orgId);
	}

	@Override
	public String findById(Role role) {
		return roleDao.findById(role);
	}

	@Override
	public List<Role> getAllOrgRoles() {
		return roleDao.getAllOrgRoles();
	}

	@Override
	public void initializeActions() {
		roleDao.initializeActions();

	}

	@Override
	public void intializeRolesAndActions() {
		roleDao.intializeRolesAndActions();

	}

	@Override
	public Map<Long, List<String>> getAllActionsForRoles(List<Long> roleIds) {
		return roleDao.getAllActionsForRoles(roleIds);
	}

}
