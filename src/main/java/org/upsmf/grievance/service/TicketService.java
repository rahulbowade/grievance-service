package org.upsmf.grievance.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import org.upsmf.grievance.dto.TicketTypeDto;
import org.upsmf.grievance.model.ActivityLog;
import org.upsmf.grievance.model.Analytics;
import org.upsmf.grievance.model.TemplateVersion;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.TicketCount;
import org.upsmf.grievance.model.TicketElastic;
import org.upsmf.grievance.model.Updates;
import org.upsmf.grievance.model.User;

/**
 * This interface contains all the ticket related service
 *
 * @author Juhi Agarwal
 *
 */
public interface TicketService {

	Ticket addTicket(Ticket ticket);

	Long getTemplatesVersion();

	TemplateVersion getTemplates();

	boolean configureTemplates(TemplateVersion templateVersion);

	boolean updateTicketBasic(MultipartFile file, Ticket ticket);

	/**
	 * Get Method which receives the User Object and the Ticket ID from the
	 * Controller and calls the respective DAO Method to get the Ticket Response for
	 * the specific Ticket which is being requested for
	 *
	 * @param user
	 * @param id
	 * @return
	 */
	Ticket getTicketsById(User user, Long id);

	List<Ticket> getAllTicketsByAppId(Long appId);

	Boolean updateNotesToTicket(Ticket ticket);

	Boolean addUpdateUpdatesToTicket(Updates update);

	List<Updates> getUpdatesForTicket(Long id);

	boolean updateTicketType(TicketTypeDto ticketTypeDto, Long userId);

	boolean updateTicketStatus(Ticket ticket);

	boolean updateTicketChecklist(Ticket ticket);

	boolean sendRepliesToReviews(Updates updates);

	List<ActivityLog> getActivityLogsPerTicket(Long id);

	List<Ticket> getFeedBacksFromAuroraSdk();

	List<Ticket> keepOnlyCreatedAndCopiedToTickets(Long userId, List<Ticket> ticketList);

	Map<String, Long> getTicketsCountPerMonthPerUser(Analytics analytics);

	List<ActivityLog> getActivityLogsPerUser(Long id);

	List<TicketElastic> getTicketDetailsByHelpdeskId(Ticket ticket);

	List<Ticket> getAllTicketsByUserId(Long id);

	boolean attachmentUpload(MultipartFile file, Ticket ticket);

	TicketCount getNoOfTickets(Long userId);

	boolean pinTicket(Ticket ticket);
}
