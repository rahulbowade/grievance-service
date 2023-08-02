package org.upsmf.grievance.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class Action {

	private Long id;
	private String name;
	private String displayName;
	private String url;
	private String queryParams;
	private String serviceCode;
}
