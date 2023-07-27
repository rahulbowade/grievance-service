package org.upsmf.grievance.dao;

import java.util.List;
import java.util.Map;

import org.upsmf.grievance.model.Role;

public interface RoleDao {
	/**
	 * This method hits the DB and fetches all the available active roles
	 *
	 * @param fetchData
	 * @return
	 */
	public List<Role> getAllRoles(Long orgId);

	/**
	 * This method is used to save the Role Details in the Database
	 *
	 * @param role
	 * @return
	 */
	public Role saveRole(Role role);

	/**
	 * This method is used to update the Role Details based on the Role ID passed in
	 * the Role Object
	 *
	 * @param role
	 * @return
	 */
	public Role updateRole(Role role);

	/**
	 * This method supplies the ID to Database and fetches the Role for the ID and
	 * returns the Role Object
	 *
	 * @param role
	 * @return
	 */
	public String findById(Role role);

	public List<Role> getAllOrgRoles();

	public void initializeActions();

	public void intializeRolesAndActions();

	public Map<Long, List<String>> getAllActionsForRoles(List<Long> roleIds);

}
