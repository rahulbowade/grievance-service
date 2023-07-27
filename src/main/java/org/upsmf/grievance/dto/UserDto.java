package org.upsmf.grievance.dto;

import java.util.List;
import java.util.Set;

import org.upsmf.grievance.model.Action;
import org.upsmf.grievance.model.Role;

/**
 * Data Transfer Object which carries the User Information to the Data Access
 * Layers
 *
 * @author Juhi Agarwal
 *
 */
public class UserDto {

	private long id;

	private String name;

	private String username;

	private String password;

	private Long orgId;

	private List<Role> roles;

	private Set<Action> actions;

	private String statusMsg;

	private String googleEmail;
	private String googleClientId;
	private String portal;
	private String authType;
	private String googleIdToken;

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Set<Action> getActions() {
		return actions;
	}

	public void setActions(Set<Action> actions) {
		this.actions = actions;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}

	public String getGoogleEmail() {
		return googleEmail;
	}

	public void setGoogleEmail(String googleEmail) {
		this.googleEmail = googleEmail;
	}

	public String getGoogleClientId() {
		return googleClientId;
	}

	public void setGoogleClientId(String googleClientId) {
		this.googleClientId = googleClientId;
	}

	public String getPortal() {
		return portal;
	}

	public void setPortal(String portal) {
		this.portal = portal;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public String getGoogleIdToken() {
		return googleIdToken;
	}

	public void setGoogleIdToken(String googleIdToken) {
		this.googleIdToken = googleIdToken;
	}

}
