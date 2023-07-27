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
public class HelpdeskWorkflowDto {

	private Long id;
	private Long typeId;
	private String name;
}
