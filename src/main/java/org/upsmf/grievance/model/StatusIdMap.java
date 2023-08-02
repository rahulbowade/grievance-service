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
public class StatusIdMap {

	private Long orgId;
	private Long appId;
	private Long helpdeskId;
	private boolean orgAppStatus;
	private boolean helpdeskAppStatus;
	private Long orgAppMapId;
	private Long helpdeskAppMapId;

}
