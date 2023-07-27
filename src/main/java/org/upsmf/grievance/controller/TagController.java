package org.upsmf.grievance.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.upsmf.grievance.dto.TicketTagDto;
import org.upsmf.grievance.model.Tags;
import org.upsmf.grievance.model.TicketsTagsList;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.service.TagService;
import org.upsmf.grievance.util.PathRoutes;
import org.upsmf.grievance.util.ResponseGenerator;

@RestController
@RequestMapping(PathRoutes.TAGS_URL)
public class TagController {

	private static final String ID = "id";
	private static final String ORG_ID = "orgId";
	private static final String USER_INFO = "UserInfo";

	public static final Logger LOGGER = LoggerFactory.getLogger(TagController.class);

	@Autowired
	private TagService tagService;

	@PostMapping(value = PathRoutes.TagRoutes.CREATE_TAG)
	public String createUpdateTag(@RequestAttribute(value = USER_INFO) User user,
			@RequestBody TicketTagDto ticketTagDto) throws JsonProcessingException {
		boolean saved = tagService.saveTags(ticketTagDto, user.getId());
		if (saved) {
			return ResponseGenerator.successResponse(saved);
		}
		return ResponseGenerator.failureResponse();

	}

	@GetMapping(value = PathRoutes.TagRoutes.GET_TAG_BY_ORG_ID)
	public String getOrgTags(@RequestAttribute(value = USER_INFO) User user,
			@RequestParam(value = ORG_ID, required = true) Long orgId) throws JsonProcessingException {
		TicketsTagsList tags = tagService.getAllOrgTags(orgId);
		if (tags != null) {
			return ResponseGenerator.successResponse(tags);
		}
		return ResponseGenerator.failureResponse();
	}

	@GetMapping(value = PathRoutes.TagRoutes.GET_TAG_BY_HELPDESK_ID)
	public String getHelpdeskTags(@RequestAttribute(value = USER_INFO) User user,
			@RequestParam(value = ID, required = true) Long id) throws JsonProcessingException {
		TicketsTagsList tags = tagService.getHelpdeskTags(id, user.getId());
		if (tags.getTags() != null) {
			return ResponseGenerator.successResponse(tags);
		}
		return ResponseGenerator.failureResponse();
	}

	@GetMapping(value = PathRoutes.TagRoutes.GET_ALL_TICKET_TAGS)
	public String getAllTicketTags(@RequestAttribute(value = USER_INFO) User user,
			@RequestParam(value = ID, required = true) Long id) throws JsonProcessingException {
		List<Tags> response = tagService.getAllTicketTags(id);
		if (response != null) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse();
	}

}
