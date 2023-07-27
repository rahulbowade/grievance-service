package org.upsmf.grievance.model;

import java.util.List;

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
public class HelpdeskType {
	private Long id;
	private Long helpdeskId;
	private String name;
	private List<ChecklistItem> checklistItems;
}
