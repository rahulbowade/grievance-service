package org.upsmf.grievance.dto;

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
public class TicketWorkflowDto {

	private Long id;
	private Long workFlowId;
	private Long typeId;
	private String name;
	private String time;
	private Boolean status;
}
