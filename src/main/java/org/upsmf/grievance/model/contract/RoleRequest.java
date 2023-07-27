package org.upsmf.grievance.model.contract;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * RoleRequest
 */
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class RoleRequest {

	private Boolean enabled;
	private List<Integer> roles = new ArrayList<>();

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public List<Integer> getRoles() {
		return roles;
	}

	public void setRoles(List<Integer> roles) {
		this.roles = roles;
	}

}
