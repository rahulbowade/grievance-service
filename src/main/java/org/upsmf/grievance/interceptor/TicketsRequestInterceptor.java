package org.upsmf.grievance.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upsmf.grievance.dao.impl.ApplicationDaoImpl;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.repository.TicketRepository;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

@Service
public class TicketsRequestInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationDaoImpl.class);
	@Autowired
	private TicketRepository ticketRepository;

	public void addData(Ticket ticket) {
		org.upsmf.grievance.model.es.Ticket esTicket = createESTicketObj(ticket);
		ticketRepository.save(esTicket);
	}

	private static org.upsmf.grievance.model.es.Ticket createESTicketObj(Ticket ticket) {
		DateTimeFormatter dateFormat = new DateTimeFormatterBuilder().appendPattern("yyyy-mm-dd hh:mm:ss").toFormatter();
		org.upsmf.grievance.model.es.Ticket esTicket = org.upsmf.grievance.model.es.Ticket.builder()
				.ticketId(ticket.getId())
				.appKey(ticket.getAppKey())
				.appId(ticket.getAppId())
				.cc(ticket.getCc())
				.updatedTime(ticket.getUpdatedTime().toLocalDateTime().format(dateFormat))
				.appName(ticket.getAppName())
				.active(ticket.getActive())
				.ip(ticket.getIp())
				.country(ticket.getCountry())
				.orgId(ticket.getOrgId())
				.createdTime(ticket.getCreatedTime().toLocalDateTime().format(dateFormat))
				.attachmentUrl(ticket.getAttachmentUrl())
				.description(ticket.getDescription())
				.appVersion(ticket.getAppVersion())
				.createdTimeTS(ticket.getCreatedTimeTS())
				.img(ticket.getImg())
				.developerComment(ticket.getDeveloperComment())
				.deviceName(ticket.getDeviceName())
				.from(ticket.getFrom())
				.checklist(ticket.getChecklist())
				.deviceLocale(ticket.getDeviceLocale())
				.feedback(ticket.getFeedback())
				.notes(ticket.getNotes())
				.sv(ticket.getSv())
				.deviceManufacture(ticket.getDeviceManufacture())
				.rate(ticket.getRate())
				.ts(String.valueOf(ticket.getTs()))
				.requestedBy(ticket.getRequestedBy())
				.developerTimestamp(ticket.getDeveloperComment())
				.pinnedTicket(ticket.getPinnedTicket())
				.deviceType(ticket.getDeviceType())
				.feedbackByName(ticket.getFeedbackByName())
				.maxRating(ticket.getMaxRating())
				.helpdeskId(ticket.getHelpdeskId())
				.osType(ticket.getOsType())
				.filterCTUT(ticket.getFilterCTUT())
				.filterStatus(ticket.getFilterStatus())
				.requestedByName(ticket.getRequestedByName())
				.reviewId(ticket.getReviewId())
				.size(ticket.getSize())
				.tags(ticket.getTags())
				.total(ticket.getTotal())
				.type(ticket.getType())
				.operation(ticket.getOperation())
				.osVersion(ticket.getOsVersion())
				.sourceId(ticket.getSourceId())
				.status(ticket.getStatus())
				.priority(ticket.getPriority())
				.searchKeyword(ticket.getSearchKeyword())
				.userId(ticket.getUserId())
				.updates(ticket.getUpdates())
				.selectedTags(ticket.getSelectedTags())
				.updatedTimeTS(ticket.getUpdatedTimeTS())
				.userEvent(ticket.getUserEvent())
				.userName(ticket.getUserName())
				.userTimestamp(String.valueOf(ticket.getUserTimestamp()))
				.workflowStages(ticket.getWorkflowStages())
				.deviceScreenResolution(ticket.getDeviceScreenResolution())
				.description(ticket.getDescription())
				.build();
		return esTicket;
	}

	public void updateTicket(Ticket ticket) {
		// search existing ticket and set ID

		org.upsmf.grievance.model.es.Ticket esTicket = createESTicketObj(ticket);
		ticketRepository.save(esTicket);

	}
}