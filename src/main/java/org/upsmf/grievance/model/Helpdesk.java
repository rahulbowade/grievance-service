package org.upsmf.grievance.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.upsmf.grievance.dto.HelpdeskDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Helpdesk {

	private Long id;
	private String name;
	private Long orgId;
	private Long createdBy;
	private String createdDate;
	private Long updatedBy;
	private String updatedDate;
	private List<Long> adminIds;
	private List<User> adminDetails;
	private String description;
	private String color;
	private List<Long> appIds;
	private List<Long> sourceId;
	private List<Long> userIds;
	private Boolean allowAllUsers;
	private List<User> admins;
	private Boolean direct;
	private Boolean playstore;
	private Boolean appstore;
	private Boolean aurora_sdk;

	public Helpdesk(HelpdeskDto helpdeskDto) {
		this.id = helpdeskDto.getId();
		this.name = helpdeskDto.getName();
		this.orgId = helpdeskDto.getOrgId();
		this.createdBy = helpdeskDto.getCreatedBy();
		this.createdDate = helpdeskDto.getCreatedDate();
		this.updatedBy = helpdeskDto.getUpdatedBy();
		this.updatedDate = helpdeskDto.getUpdatedDate();
		this.isActive = helpdeskDto.getIsActive();
		this.color = helpdeskDto.getColor();
		this.description = helpdeskDto.getDescription();
		this.adminIds = helpdeskDto.getAdminIds();
		this.appIds = helpdeskDto.getAppIds();
		this.sourceId = helpdeskDto.getSourceId();
		this.userIds = helpdeskDto.getUserIds();
		this.allowAllUsers = helpdeskDto.getAllowAllUsers();
		this.admins = helpdeskDto.getAdmins();
		this.direct = helpdeskDto.getDirect();
		this.playstore = helpdeskDto.getPlaystore();
		this.appstore = helpdeskDto.getAppstore();
		this.aurora_sdk = helpdeskDto.getAuroraSdk();
	}

	@JsonProperty("isActive")
	private Boolean isActive;
}
