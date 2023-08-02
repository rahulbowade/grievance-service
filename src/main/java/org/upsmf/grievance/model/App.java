package org.upsmf.grievance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(includeFieldNames = true)
@Builder
public class App {

	private Long id;
	private String name;
	private String code;
	private String description;
	private String clientName;
	private String version;
	private String appUrl;
	private String logo;
	private Long sourceId;
	private Boolean activeStatus;
	private Long orgId;
	private String createdDate;
	private Long createdBy;
	private String updatedDate;
	private Long updatedBy;
	private String appKey;

}
