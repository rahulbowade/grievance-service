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
public class TicketCount {

	private Long userId;
	private Long id;
	private Long createdTicketCount;
	private Long pinnedTicketCount;
	private Long closedTicketCount;
}