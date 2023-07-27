package org.upsmf.grievance.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.upsmf.grievance.dao.TagDao;
import org.upsmf.grievance.dto.TicketTagDto;
import org.upsmf.grievance.model.Tags;
import org.upsmf.grievance.model.TicketsTagsList;
import org.upsmf.grievance.service.TagService;
import org.upsmf.grievance.util.Constants;

@Service(value = Constants.TAG_SERVICE)
public class TagServiceImpl implements TagService {

	@Autowired
	private TagDao tagDao;

	@Override
	public boolean saveTags(TicketTagDto ticketTagDto, Long id) {
		for (Tags tag : ticketTagDto.getTags()) {
			Tags savedTag = tagDao.getTagByName(tag.getName().trim(), id);
			if (savedTag == null) {
				tag.setId(tagDao.addTag(id, tag));
			} else {
				tag.setId(savedTag.getId());
			}
		}
		return tagDao.addTicketTags(ticketTagDto.getId(), ticketTagDto.getTags());
	}

	@Override
	public TicketsTagsList getAllOrgTags(Long orgId) {
		TicketsTagsList list = new TicketsTagsList();
		list.setTags(tagDao.getAllTags(orgId));
		return list;
	}

	@Override
	public List<Tags> getAllTicketTags(Long id) {
		return tagDao.getAllTicketTags(id);
	}

	@Override
	public TicketsTagsList getHelpdeskTags(Long id, Long userId) {
		TicketsTagsList list = new TicketsTagsList();
		list.setTags(tagDao.getHelpdeskTags(id, userId));
		return list;
	}

}
