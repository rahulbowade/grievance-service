package org.upsmf.grievance.dto;

import java.util.List;

import org.upsmf.grievance.model.ChecklistItem;

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
public class HelpdeskTypeDto {

	private Long id;
	private Long helpdeskId;
	private String name;
	private List<HelpdeskWorkflowDto> workflowStages;
	private List<ChecklistItem> checklistItems;
}
