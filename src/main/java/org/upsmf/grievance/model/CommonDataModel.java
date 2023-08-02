package org.upsmf.grievance.model;

import org.upsmf.grievance.model.enums.AuthTypes;
import org.upsmf.grievance.model.enums.PriorityLevels;

public class CommonDataModel {
	private int id;
	private String name;
	private String description;
	private long userId;
	private boolean active;
	private long orgId;

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public CommonDataModel() {
	}

	public CommonDataModel(PriorityLevels priorityLevels) {
		this.name = String.valueOf(priorityLevels);
	}

	public CommonDataModel(AuthTypes authTypes) {
		this.id = authTypes.ordinal() + 1;
		this.name = String.valueOf(authTypes);
	}

	public CommonDataModel(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
