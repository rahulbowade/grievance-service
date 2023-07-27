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
public class TicketElastic {

	private String updatedTime;
	private String description;
	private Long helpdeskId;
	private Long requestedBy;
	private String createdTime;
	private Long id;
	private String status;
	private Long total;
	private Boolean pinnedTicket;
	private String uT;
	private String cT;
	private String[] cc;
	private String[] tags;
}
