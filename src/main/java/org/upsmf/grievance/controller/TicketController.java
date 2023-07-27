package org.upsmf.grievance.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.upsmf.grievance.dao.HelpdeskDao;
import org.upsmf.grievance.dto.TicketTypeDto;
import org.upsmf.grievance.model.ActivityLog;
import org.upsmf.grievance.model.Analytics;
import org.upsmf.grievance.model.CommonDataModel;
import org.upsmf.grievance.model.TemplateVersion;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.TicketCount;
import org.upsmf.grievance.model.TicketElastic;
import org.upsmf.grievance.model.Updates;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.model.enums.PriorityLevels;
import org.upsmf.grievance.service.TicketService;
import org.upsmf.grievance.util.Constants;
import org.upsmf.grievance.util.PathRoutes;
import org.upsmf.grievance.util.ResponseGenerator;

/**
 * This controller contains all the ticket related methods
 *
 * @author Juhi Agarwal
 *
 */
@RestController
@RequestMapping(PathRoutes.TICKET_ACTIONS_URL)
public class TicketController {

	private static final String ID = "id";

	private static final String USER_INFO = "UserInfo";

	@Autowired
	private TicketService ticketService;

	@Autowired
	private HelpdeskDao helpdeskDao;

	@PostMapping(PathRoutes.TicketRoutes.ADD_TICKET)
	public String addTicket(@RequestAttribute(value = USER_INFO) User user, @RequestBody Ticket ticket)
			throws JsonProcessingException {
		ticket = ticketService.addTicket(ticket);
		if (ticket != null) {
			return ResponseGenerator.successResponse(ticket);
		}
		return ResponseGenerator.failureResponse();

	}

	@PostMapping(PathRoutes.TicketRoutes.UPLOAD_ATTACHMENT)
	public String uploadAttachment(@RequestAttribute(value = USER_INFO) User user,
			@RequestParam(value = "file", required = false) MultipartFile file, @RequestBody Ticket ticket)
			throws JsonProcessingException {
		ticket.setUserId(user.getId());
		ticket.setOrgId(user.getOrgId());
		boolean response = ticketService.attachmentUpload(file, ticket);
		if (response) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse();

	}

	@PostMapping(PathRoutes.TicketRoutes.UPDATE_TICKET_BASIC)
	public String updateTicketBasic(@RequestAttribute(value = USER_INFO) User user,
			@RequestParam(value = "file", required = false) MultipartFile file, @RequestBody Ticket ticket)
			throws JsonProcessingException {
		ticket.setOrgId(user.getOrgId());
		ticket.setUserId(user.getId());
		boolean response = false;
		response = ticketService.updateTicketBasic(file, ticket);
		if (response) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse();

	}

	@PostMapping(PathRoutes.TicketRoutes.PINNED_TICKET)
	public String pinTicket(@RequestAttribute(value = USER_INFO) User user, @RequestBody Ticket ticket)
			throws JsonProcessingException {
		ticket.setOrgId(user.getOrgId());
		ticket.setUserId(user.getId());
		boolean response = false;
		response = ticketService.pinTicket(ticket);
		if (response) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse();

	}

	@PostMapping(PathRoutes.TicketRoutes.UPDATE_TICKET_TYPE)
	public String updateTicketType(@RequestAttribute(value = USER_INFO) User user,
			@RequestBody TicketTypeDto ticketTypeDto) throws JsonProcessingException {
		ticketTypeDto.setOrgId(user.getOrgId());
		boolean response = ticketService.updateTicketType(ticketTypeDto, user.getId());
		if (response) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse();

	}

	@PostMapping(PathRoutes.TicketRoutes.UPDATE_TICKET_STATUS)
	public String updateTicketStatus(@RequestAttribute(value = USER_INFO) User user, @RequestBody Ticket ticket)
			throws JsonProcessingException {
		ticket.setRequestedBy(user.getId());
		ticket.setOrgId(user.getOrgId());
		boolean response = ticketService.updateTicketStatus(ticket);
		if (response) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse();

	}

	@PostMapping(PathRoutes.TicketRoutes.UPDATE_TICKET_CHECKLIST)
	public String updateTicketChecklist(@RequestAttribute(value = USER_INFO) User user, @RequestBody Ticket ticket)
			throws JsonProcessingException {
		ticket.setOrgId(user.getOrgId());
		boolean response = ticketService.updateTicketChecklist(ticket);
		if (response) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse();

	}

	@GetMapping(PathRoutes.TicketRoutes.GET_ALL_TICKETS)
	public String getTickets(@RequestAttribute(value = Constants.RequestParams.USER_INFO) User user,
			@RequestParam(value = Constants.RequestParams.ID, required = false) Long id,
			@RequestParam(value = Constants.RequestParams.APP_ID, required = false) Long appId)
			throws JsonProcessingException {
		if (id != null && id > 0) {
			Ticket tickets = ticketService.getTicketsById(user, id);
			return ResponseGenerator.successResponse(tickets);
		} else if (appId != null && appId > 0) {
			List<Ticket> tickets = ticketService.getAllTicketsByAppId(appId);
			return ResponseGenerator.successResponse(tickets);
		} else {
			List<Ticket> tickets = ticketService.getAllTicketsByUserId(user.getId());
			return ResponseGenerator.successResponse(tickets);
		}
	}

	@PostMapping(PathRoutes.TicketRoutes.GET_ALL_TICKETS)
	public String getTicketDetailsByHelpdeskId(@RequestAttribute(value = USER_INFO) User user,
			@RequestBody Ticket ticket) throws JsonProcessingException {
		ticket.setUserId(user.getId());
		if (ticket.getHelpdeskId() != null && ticket.getHelpdeskId() > 0) {
			List<TicketElastic> data = ticketService.getTicketDetailsByHelpdeskId(ticket);
			return ResponseGenerator.successResponse(data);
		}
		return ResponseGenerator.failureResponse();
	}

	@PostMapping(PathRoutes.TicketRoutes.GET_TICKET_COUNT_PER_MONTH_PER_USER)
	public String getTicketsCountPerMonthPerUser(@RequestAttribute(value = USER_INFO) User user,
			@RequestBody Analytics analytics) throws JsonProcessingException {
		analytics.setUserId(user.getId());
		if (analytics.getStartDate() != null && analytics.getEndDate() != null && !analytics.getStartDate().isEmpty()
				&& !analytics.getEndDate().isEmpty()) {
			Map<String, Long> data = ticketService.getTicketsCountPerMonthPerUser(analytics);
			return ResponseGenerator.successResponse(data);
		}
		return ResponseGenerator.failureResponse();
	}

	@PostMapping(PathRoutes.TicketRoutes.GET_FEEDBACK_FROM_AURORA_SDK)
	public String getFeedBacksFromAuroraSdk(@RequestAttribute(value = USER_INFO) User user)
			throws JsonProcessingException {
		List<Ticket> tickets = ticketService.getFeedBacksFromAuroraSdk();
		if (tickets != null) {
			return ResponseGenerator.successResponse(tickets);
		}
		return ResponseGenerator.failureResponse();

	}

	@GetMapping(PathRoutes.TicketRoutes.GET_ACTIVITY_LOGS)
	public String getActivityLogsPerTicket(@RequestParam(value = ID, required = true) Long id)
			throws JsonProcessingException {
		List<ActivityLog> activityLog = ticketService.getActivityLogsPerTicket(id);
		if (activityLog != null) {
			return ResponseGenerator.successResponse(activityLog);
		}
		return ResponseGenerator.failureResponse();
	}

	@GetMapping(PathRoutes.TicketRoutes.GET_ACTIVITY_LOGS_PER_USER)
	public String getActivityLogsPerUser(@RequestAttribute(value = USER_INFO) User user)
			throws JsonProcessingException {
		List<ActivityLog> activityLog = ticketService.getActivityLogsPerUser(user.getId());
		if (activityLog != null) {
			return ResponseGenerator.successResponse(activityLog);
		}
		return ResponseGenerator.failureResponse();
	}

	@PostMapping(PathRoutes.TicketRoutes.ADD_NOTES)
	public String updateNotesToTicket(@RequestAttribute(value = USER_INFO) User user, @RequestBody Ticket ticket)
			throws JsonProcessingException {
		ticket.setRequestedBy(user.getId());
		boolean response = ticketService.updateNotesToTicket(ticket);
		if (response) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse();

	}

	@PostMapping(PathRoutes.TicketRoutes.ADD_UPDATE_UPDATES)
	public String addUpdateUpdatesToTicket(@RequestAttribute(value = USER_INFO) User user, @RequestBody Updates updates)
			throws JsonProcessingException {
		Long id = user.getId();
		updates.setCreatedBy(id);
		List<Ticket> ticketList = new ArrayList<>();
		Ticket ticket = ticketService.getTicketsById(user, updates.getTicketId());
		List<Long> admins = helpdeskDao.getHelpdeskAdmins(ticket.getHelpdeskId());
		Boolean admin = false;
		if (admins.contains(id)) {
			admin = true;
		}
		ticketList.add(ticket);
		List<Ticket> tickets = ticketService.keepOnlyCreatedAndCopiedToTickets(id, ticketList);
		if (tickets != null && !tickets.isEmpty() || admin) {
			boolean response = ticketService.addUpdateUpdatesToTicket(updates);
			if (response) {
				return ResponseGenerator.successResponse(response);
			}
		}
		return ResponseGenerator.failureResponse();

	}

	@GetMapping(PathRoutes.TicketRoutes.GET_UPDATES)
	public String getUpdatesForTicket(@RequestParam(value = ID, required = true) Long id)
			throws JsonProcessingException {
		List<Updates> update = ticketService.getUpdatesForTicket(id);
		if (update != null) {
			return ResponseGenerator.successResponse(update);
		}
		return ResponseGenerator.failureResponse();
	}

	@GetMapping(value = PathRoutes.TicketRoutes.GET_PRIORITY_LEVELS)
	public String getPriorityLevels(@RequestAttribute(value = USER_INFO) User user) throws JsonProcessingException {
		final List<CommonDataModel> priorityList = new ArrayList<>();
		for (final PriorityLevels key : PriorityLevels.values()) {
			priorityList.add(new CommonDataModel(key));
		}
		return ResponseGenerator.successResponse(priorityList);
	}

	@GetMapping(value = PathRoutes.TicketRoutes.GET_TEMPLATES_VERSION)
	public String getTemplatesVersion(@RequestAttribute(value = USER_INFO, required = false) User user)
			throws JsonProcessingException {
		Long version = ticketService.getTemplatesVersion();
		TemplateVersion templateVersion = new TemplateVersion();
		templateVersion.setTemplates(null);
		templateVersion.setVersion(version);
		return ResponseGenerator.successResponse(templateVersion);
	}

	@GetMapping(value = PathRoutes.TicketRoutes.GET_TEMPLATES)
	public String getTemplates(@RequestAttribute(value = USER_INFO, required = false) User user)
			throws JsonProcessingException {
		TemplateVersion templateVersion = ticketService.getTemplates();
		return ResponseGenerator.successResponse(templateVersion);
	}

	@GetMapping(value = PathRoutes.TicketRoutes.GET_NO_OF_TICKETS)
	public String getNoOfTicketsPerUser(@RequestAttribute(value = USER_INFO, required = false) User user)
			throws JsonProcessingException {
		TicketCount tc = ticketService.getNoOfTickets(user.getId());
		return ResponseGenerator.successResponse(tc);
	}

	@PostMapping(PathRoutes.TicketRoutes.CONFIGURE_TEMPLATE)
	public String configureTemplates(@RequestBody TemplateVersion templateVersion,
			@RequestAttribute(value = USER_INFO) User user) throws JsonProcessingException {
		boolean response = ticketService.configureTemplates(templateVersion);
		if (response) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse();
	}

	@PostMapping(PathRoutes.TicketRoutes.SEND_REPLY_TO_REVIEWS)
	public String sendRepliesToReviews(@RequestAttribute(value = USER_INFO) User user, @RequestBody Updates updates)
			throws JsonProcessingException {
		Long id = user.getId();
		updates.setCreatedBy(id);
		boolean response = ticketService.sendRepliesToReviews(updates);
		if (response) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse();

	}

}
