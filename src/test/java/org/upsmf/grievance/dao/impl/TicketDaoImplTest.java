package org.upsmf.grievance.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.upsmf.grievance.dao.impl.TicketDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;

import org.upsmf.grievance.dto.TicketTypeDto;
import org.upsmf.grievance.dto.TicketWorkflowDto;
import org.upsmf.grievance.model.ActivityLog;
import org.upsmf.grievance.model.ChecklistItem;
import org.upsmf.grievance.model.KeyFactory;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.Updates;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ KeyFactory.class })
public class TicketDaoImplTest {

	@Mock
	JdbcTemplate jdbcTemplate; 

	@InjectMocks
    TicketDaoImpl ticketdao;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this); 
	}

	@Test
	public void deleteWorkflowForTicketTypeTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(1);
		assertEquals(true, ticketdao.deleteWorkflowForTicketType(1L));
	}

	@Test
	public void deleteWorkflowForTicketTypeFalseTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(0);
		assertEquals(false, ticketdao.deleteWorkflowForTicketType(1L));
	}

	@Test
	public void deleteChecklistForTicketTypeTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(1);
		assertEquals(true, ticketdao.deleteChecklistForTicketType(new TicketTypeDto()));
	}

	@Test
	public void deleteChecklistForTicketTypeFalseTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(0);
		assertEquals(false, ticketdao.deleteChecklistForTicketType(new TicketTypeDto()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getWorkflowForTicketTest() {
		TicketWorkflowDto t = new TicketWorkflowDto();
		t.setId(1L);
		List<TicketWorkflowDto> ticketWorkFlow = new ArrayList<>();
		ticketWorkFlow.add(t);
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(ticketWorkFlow);
		assertEquals(1L, ticketdao.getWorkflowForTicket(1L).get(0).getId().longValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void addUpdateUpdatesToTicket() {
		Updates u = new Updates();
		u.setId(1L);
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(1);
		assertEquals(true, ticketdao.addUpdateUpdatesToTicket(u));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void addUpdateUpdatesToTicketNull() {
		Updates u = new Updates();
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(1);
		assertEquals(true, ticketdao.addUpdateUpdatesToTicket(u));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getUpdatesForTicketTest() {
		Updates u = new Updates();
		u.setId(1L);
		List<Updates> update = new ArrayList<>();
		update.add(u);
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(update);
		assertEquals(1L, ticketdao.getUpdatesForTicket(1L).get(0).getId().longValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getTemplatesVersionTest() {
		Updates u = new Updates();
		u.setId(1L);
		List<Updates> update = new ArrayList<>();
		update.add(u);
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), eq(Long.class))).thenReturn(1L);
		assertEquals(1L, ticketdao.getTemplatesVersion().longValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void updateTemplateVersionTest() {
		Updates u = new Updates();
		u.setId(1L);
		List<Updates> update = new ArrayList<>();
		update.add(u);
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(1);
		assertEquals(true, ticketdao.updateTemplateVersion(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getTicketCCTest() {
		List<Long> l = Arrays.asList(1L, 2L, 3L);
		Mockito.when(jdbcTemplate.queryForList(Matchers.anyString(), Matchers.<Object[]>anyVararg(), eq(Long.class)))
				.thenReturn(l);
		assertEquals(3, ticketdao.getTicketCC(1L).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void updateTicketChecklistFalseTest() {
		Ticket ticket = new Ticket();
		List<ChecklistItem> cc = new ArrayList<>();
		ChecklistItem e = new ChecklistItem();
		e.setId(1L);
		cc.add(e);
		ticket.setChecklist(cc);
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(1);
		assertEquals(true, ticketdao.updateTicketChecklist(ticket));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void updateTicketChecklistTest() {
		Ticket ticket = new Ticket();
		List<ChecklistItem> cc = new ArrayList<>();
		ChecklistItem e = new ChecklistItem();
		e.setId(1L);
		cc.add(e);
		ticket.setChecklist(cc);
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(0);
		assertEquals(false, ticketdao.updateTicketChecklist(ticket));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getActivityLogsPerTicketTest() {
		List<ActivityLog> activityLogs = new ArrayList<>();
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(activityLogs);
		assertEquals(activityLogs.getClass(), ticketdao.getActivityLogsPerTicket(1L).getClass());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	@Test
	public void getLastInsertIdTest() {
		KeyHolder keyHolder = mock(KeyHolder.class);
		Mockito.when(keyHolder.getKey()).thenReturn(1L);
		PowerMockito.mockStatic(KeyFactory.class);
		PowerMockito.when(KeyFactory.getkeyHolder()).thenReturn(keyHolder);
		when(jdbcTemplate.update(Matchers.any(PreparedStatementCreator.class), Matchers.any(KeyHolder.class)))
				.thenReturn(1);
		assertEquals(1L, ticketdao.getLastInsertId("d").longValue());
	}

	@Test
	public void addTicketActivityLogTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(1);
		assertNull(ticketdao.addTicketActivityLog(1L, "d", 1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getChecklistItemsForTicketTest() {
		ChecklistItem checklist = new ChecklistItem();
		checklist.setId(1L);
		List<ChecklistItem> checklistItems = new ArrayList<>();
		checklistItems.add(checklist);
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(checklistItems);
		assertEquals(1L, ticketdao.getChecklistItemsForTicket(1L).get(0).getId().longValue());
	}

	@Test
	public void keepOnlyCreatedAndCopiedToTicketsTest() {
		Ticket ticket = new Ticket();
		ticket.setId(1L);
		ticket.setRequestedBy(1L);
		List<Long> cc = Arrays.asList(1L, 2L, 4L);
		ticket.setCc(cc);
		List<Ticket> t = new ArrayList<>();
		t.add(ticket);
		assertEquals(new ArrayList<Ticket>().getClass(), ticketdao.keepOnlyCreatedAndCopiedToTickets(1L, t).getClass());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteWorkflowForTicketTypeNullTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertEquals(false, ticketdao.deleteWorkflowForTicketType(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteChecklistForTicketTypeNullTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertEquals(false, ticketdao.deleteChecklistForTicketType(new TicketTypeDto()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getWorkflowForTicketNullTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(0, ticketdao.getWorkflowForTicket(1L).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void addUpdateUpdatesToNullTicket() {
		Updates u = new Updates();
		u.setId(1L);
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertEquals(false, ticketdao.addUpdateUpdatesToTicket(u));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getUpdatesForTicketNullTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertNull(ticketdao.getUpdatesForTicket(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getTemplatesVersionNullTest() {
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), eq(Long.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(0L, ticketdao.getTemplatesVersion().longValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void updateTemplateVersionNullTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertEquals(false, ticketdao.updateTemplateVersion(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void updateTicketChecklistNullTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertEquals(false, ticketdao.updateTicketChecklist(new Ticket()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getActivityLogsPerTicketNullTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertNull(ticketdao.getActivityLogsPerTicket(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void addTicketActivityLogNullTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertNull(ticketdao.addTicketActivityLog(1L, "d", 1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getChecklistItemsForTicketNullTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(new ArrayList<ChecklistItem>().getClass(), ticketdao.getChecklistItemsForTicket(1L).getClass());
	}

}
