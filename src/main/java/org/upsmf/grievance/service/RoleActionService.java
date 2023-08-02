package org.upsmf.grievance.service;

import java.util.List;
import java.util.Map;

import org.upsmf.grievance.model.Role;

/**
 * This interface handles the service layer of business operation logic
 * implementation for all the Role and its Action related transactions
 *
 * @author Darshan Nagesh
 *
 */
public interface RoleActionService {

	/**
	 * This method receives the request with details related to a new role and
	 * passes on to the DAO layer to save in DB
	 *
	 * @param role
	 * @return
	 */
	Role saveRole(Role role);

	/**
	 * This method receives the request with details related to a new role and
	 * passes on to the DAO layer to update the role information in DB
	 *
	 * @param role
	 * @return
	 */
	Role updateRole(Role role);

	/**
	 * This method fetches all the Roles available in the system
	 *
	 * @param fetchData
	 * @return
	 */
	List<Role> getAllRoles(Long orgId);

	/**
	 * This method receives the ID from Controller to pass on the same to DAO to
	 * fetch the Role Object from Database This returns the Role Object for the
	 * respective Role ID
	 *
	 * @param role
	 * @return
	 */
	String findById(Role role);

	List<Role> getAllOrgRoles();

	void initializeActions();

	void intializeRolesAndActions();

	Map<Long, List<String>> getAllActionsForRoles(List<Long> roleIds);

}