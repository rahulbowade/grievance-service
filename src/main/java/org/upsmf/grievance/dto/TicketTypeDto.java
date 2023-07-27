package org.upsmf.grievance.dto;

import java.util.List;

import org.upsmf.grievance.model.ChecklistItem;
import org.upsmf.grievance.model.Ticket;

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
public class TicketTypeDto {

	private Long id;
	private Long helpdeskId;
	private String name;
	private Long orgId;
	private Long typeId;
	private List<TicketWorkflowDto> workflowStages;
	private List<ChecklistItem> checklistItems;

	public TicketTypeDto(Ticket ticket) {
		this.id = ticket.getId();
		this.orgId = ticket.getOrgId();
		this.helpdeskId = ticket.getHelpdeskId();
		this.typeId = ticket.getType();
	}
}
