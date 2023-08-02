package org.upsmf.grievance.service;

import java.util.List;

import org.upsmf.grievance.dto.TicketTagDto;
import org.upsmf.grievance.model.Tags;
import org.upsmf.grievance.model.TicketsTagsList;

public interface TagService {

	public boolean saveTags(TicketTagDto ticketTagDto, Long id);

	public TicketsTagsList getAllOrgTags(Long orgId);

	public List<Tags> getAllTicketTags(Long id);

	public TicketsTagsList getHelpdeskTags(Long id, Long userId);

}
