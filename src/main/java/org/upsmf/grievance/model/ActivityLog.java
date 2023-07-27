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
public class ActivityLog implements Comparable<ActivityLog> {

	private Long id;
	private String activity;
	private Long ticketId;
	private String timestamp;
	private Long changesBy;
	private Long helpdeskId;

	@Override
	public int compareTo(ActivityLog o) {
		return this.getId().compareTo(o.getId());
	}
}
