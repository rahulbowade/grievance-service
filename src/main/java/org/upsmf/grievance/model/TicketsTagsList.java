package org.upsmf.grievance.model;

import java.util.ArrayList;
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
public class TicketsTagsList {
	private List<Ticket> ticket = new ArrayList<>();
	private List<Tags> tags = new ArrayList<>();
}