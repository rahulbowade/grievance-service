package org.upsmf.grievance.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.upsmf.grievance.service.impl.TicketServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import org.upsmf.grievance.dao.TicketDao;
import org.upsmf.grievance.dto.TicketTypeDto;
import org.upsmf.grievance.model.ActivityLog;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.TicketElastic;
import org.upsmf.grievance.model.Updates;
import org.upsmf.grievance.model.User;

public class TicketServiceImplTest {

	@Mock
	TicketDao ticketDao;

	@InjectMocks
    TicketServiceImpl ticketService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getTemplatesVersionTest() {
		Mockito.when(ticketDao.getTemplatesVersion()).thenReturn(1L);
		assertEquals(1L, ticketService.getTemplatesVersion().longValue());
	}

	@Test
	public void updateTicketBasicTest() {
		Mockito.when(ticketDao.updateTicketBasic(Matchers.any(MultipartFile.class), Matchers.any(Ticket.class)))
				.thenReturn(true);
		MultipartFile file = null;
		assertEquals(true, ticketService.updateTicketBasic(file, new Ticket()));
	}

	@Test
	public void getTicketsByIdTest() {
		Mockito.when(ticketDao.getTicketsById(Matchers.anyLong(), Matchers.anyLong())).thenReturn(new Ticket());
		assertEquals(new Ticket().getClass(), ticketService.getTicketsById(new User(), 1L).getClass());
	}

	@Test
	public void getAllTicketsByAppIdTest() {
		Mockito.when(ticketDao.getAllTicketsByAppId(Matchers.anyLong())).thenReturn(new ArrayList<Ticket>());
		assertEquals(0, ticketService.getAllTicketsByAppId(1L).size());
	}

	@Test
	public void updateNotesToTicketTest() {
		Mockito.when(ticketDao.updateNotesToTicket(Matchers.anyLong(), Matchers.anyLong(), Matchers.anyString()))
				.thenReturn(true);
		assertEquals(true, ticketService.updateNotesToTicket(new Ticket()));
	}

	@Test
	public void addUpdateUpdatesToTicketTest() {
		Mockito.when(ticketDao.addUpdateUpdatesToTicket(Matchers.any(Updates.class))).thenReturn(true);
		assertEquals(true, ticketService.addUpdateUpdatesToTicket(new Updates()));
	}

	@Test
	public void updateTicketTypeTest() {
		Mockito.when(ticketDao.updateTicketType(Matchers.any(TicketTypeDto.class), Matchers.anyLong()))
				.thenReturn(true);
		assertEquals(true, ticketService.updateTicketType(new TicketTypeDto(), 1L));
	}

	@Test
	public void getUpdatesForTicketTest() {
		Mockito.when(ticketDao.getUpdatesForTicket(Matchers.anyLong())).thenReturn(new ArrayList<Updates>());
		assertEquals(0, ticketService.getUpdatesForTicket(1L).size());
	}

	@Test
	public void updateTicketStatusTest() {
		Mockito.when(ticketDao.updateTicketStatus(Matchers.any(Ticket.class))).thenReturn(true);
		assertEquals(true, ticketService.updateTicketStatus(new Ticket()));
	}

	@Test
	public void updateTicketChecklistTest() {
		Mockito.when(ticketDao.updateTicketStatus(Matchers.any(Ticket.class))).thenReturn(true);
		assertEquals(false, ticketService.updateTicketChecklist(new Ticket()));
	}

	@Test
	public void getActivityLogsPerTicketTest() {
		Mockito.when(ticketDao.getActivityLogsPerTicket(Matchers.anyLong())).thenReturn(new ArrayList<ActivityLog>());
		assertEquals(0, ticketService.getActivityLogsPerTicket(1L).size());
	}

	@Test
	public void getTicketDetailsByHelpdeskIdTest() {
		Mockito.when(ticketDao.getTicketDetailsByHelpdeskId(Matchers.any(Ticket.class)))
				.thenReturn(new ArrayList<TicketElastic>());
		assertEquals(0, ticketService.getTicketDetailsByHelpdeskId(new Ticket()).size());
	}

	/*
	 * @Test public void keepOnlyCreatedAndCopiedToTicketsTest() {
	 * Mockito.when(ticketDao.keepOnlyCreatedAndCopiedToTickets(Mockito.anyLong(),
	 * Mockito.anyListOf(Ticket.class),
	 * Mockito.anyListOf(Ticket.class))).thenReturn(new ArrayList<Ticket>());
	 * assertEquals(0, ticketService .keepOnlyCreatedAndCopiedToTickets(1L, new
	 * ArrayList<Ticket>(), new ArrayList<Ticket>()).size()); }
	 */

}
