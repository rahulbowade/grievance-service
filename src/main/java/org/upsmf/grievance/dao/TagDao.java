package org.upsmf.grievance.dao;

import java.util.List;

import org.upsmf.grievance.model.Tags;

public interface TagDao {

	List<Tags> getAllTags(Long orgId);

	Tags getTagByName(String trim, Long id);

	Long addTag(Long id, Tags tag);

	boolean addTicketTags(Long id, List<Tags> tags);

	List<Tags> getAllTicketTags(Long id);

	List<Tags> getHelpdeskTags(Long id, Long userId);

}
