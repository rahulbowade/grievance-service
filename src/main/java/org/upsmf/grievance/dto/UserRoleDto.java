package org.upsmf.grievance.dto;

import java.util.List;

import org.upsmf.grievance.model.Role;

/**
 * Data Transfer Object which carries the User ID and the Roles associated.
 *
 * @author Darshan Nagesh
 *
 */
public class UserRoleDto {

	private Long userId;
	private List<Role> roles;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

}
