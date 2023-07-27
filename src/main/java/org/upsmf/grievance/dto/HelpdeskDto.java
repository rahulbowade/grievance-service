package org.upsmf.grievance.dto;

import java.util.List;

import org.upsmf.grievance.model.App;
import org.upsmf.grievance.model.User;

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
public class HelpdeskDto {

	private Long id;
	private String name;
	private Long orgId;
	private Long createdBy;
	private String createdDate;
	private Long updatedBy;
	private String updatedDate;
	private Boolean isActive;
	private List<HelpdeskTypeDto> types;
	private List<App> apps;
	private List<User> admins;
	private List<User> users;
	private String description;
	private String color;
	private List<Long> adminIds;
	private List<Long> appIds;
	private List<Long> sourceId;
	private List<Long> userIds;
	private Boolean allowAllUsers;
	private Boolean direct;
	private Boolean playstore;
	private Boolean appstore;
	private Boolean auroraSdk;
}
