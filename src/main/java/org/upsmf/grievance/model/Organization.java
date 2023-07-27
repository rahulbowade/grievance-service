package org.upsmf.grievance.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(includeFieldNames = true)
public class Organization {
	private Long id;
	private Long userId;
	private String orgName;
	private String url;
	private String logo;
	private Long createdBy;
	private String createdDate;
	private Long updatedBy;
	private String updatedDate;
	private Boolean isActive;
	private List<User> adminDetails;
	private String authId;
	private String emailDomain;
	private String orgDescription;
	private String orgColor;
	private String baseUrl;

}
