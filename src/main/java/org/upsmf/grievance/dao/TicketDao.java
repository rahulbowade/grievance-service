package org.upsmf.grievance.dao;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import org.upsmf.grievance.dto.TicketTypeDto;
import org.upsmf.grievance.dto.TicketWorkflowDto;
import org.upsmf.grievance.model.ActivityLog;
import org.upsmf.grievance.model.Analytics;
import org.upsmf.grievance.model.ChecklistItem;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.TicketCount;
import org.upsmf.grievance.model.TicketElastic;
import org.upsmf.grievance.model.Updates;

/**
 * This interface contains all the ticket related dao
 *
 * @author Juhi Agarwal
 *
 */
public interface TicketDao {

	Ticket addTicket(Ticket ticket);

	Long getTemplatesVersion();

	boolean updateTemplateVersion(Long versionTimeStamp);

	Boolean updateNotesToTicket(Long requestedBy, Long ticketId, String notes);

	Boolean addUpdateUpdatesToTicket(Updates update);

	Boolean deleteWorkflowForTicketType(Long typeId);

	Boolean addChecklistForTicketType(TicketTypeDto ticketTypeDto);

	boolean updateTicketBasic(MultipartFile file, Ticket ticket);

	Ticket getTicketsById(Long userId, Long id);

	List<ChecklistItem> getChecklistItemsForTicket(Long ticketId);

	List<Ticket> getAllTicketsByAppId(Long appId);

	List<TicketWorkflowDto> getWorkflowForTicket(Long ticketId);

	List<Updates> getUpdatesForTicket(Long id);

	boolean updateTicketType(TicketTypeDto ticketTypeDto, Long userId);

	boolean updateTicketStatus(Ticket ticket);

	boolean updateTicketChecklist(Ticket ticket);

	Boolean deleteChecklistForTicketType(TicketTypeDto ticketTypeDto);

	String addDefaultWorkflowForTicketType(TicketTypeDto ticketTypeDto);

	List<ActivityLog> getActivityLogsPerTicket(Long id);

	List<Ticket> getAllTicketsByUserId(Long id);

	List<TicketElastic> getTicketDetailsByHelpdeskId(Ticket ticket);

	boolean pinTicket(Ticket ticket);

	TicketCount getNoOfTickets(Long userId);

	List<ActivityLog> getActivityLogsPerUser(Long id);

	Map<String, Long> getTicketsCountPerMonthPerUser(Analytics analytics);

	boolean attachmentUpload(MultipartFile file, Ticket ticket);

	List<Ticket> keepOnlyCreatedAndCopiedToTickets(Long userId, List<Ticket> ticketList);

	List<Ticket> getFeedBacksFromAuroraSdk();

	boolean sendRepliesToReviews(Updates updates);

}
