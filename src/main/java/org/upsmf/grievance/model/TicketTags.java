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
public class TicketTags {

	private Long id;
	private List<Long> tag;
	private Long userId;
	private boolean isActive = true;
	private Long orgId;

}
