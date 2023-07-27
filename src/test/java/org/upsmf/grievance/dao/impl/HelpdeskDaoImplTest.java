package org.upsmf.grievance.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.upsmf.grievance.dao.impl.HelpdeskDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import org.upsmf.grievance.dto.HelpdeskDto;
import org.upsmf.grievance.model.ChecklistItem;
import org.upsmf.grievance.model.HelpDeskApp;
import org.upsmf.grievance.model.Helpdesk;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.model.mapper.SqlDataMapper;
import org.upsmf.grievance.model.mapper.SqlDataMapper.HelpdeskRowRecordMapper;

public class HelpdeskDaoImplTest {

	@Mock
	JdbcTemplate jdbcTemplate; 

	@InjectMocks
    HelpdeskDaoImpl helpdeskDao;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this); 
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAllHelpdesksTest() {
		List<HelpdeskDto> list = new ArrayList<>();
		HelpdeskDto h = new HelpdeskDto();
		h.setId(1L);
		list.add(h);
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(list);
		assertEquals(1, helpdeskDao.getAllHelpdesks(1L).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHelpdeskForIdTest() {
		HelpdeskRowRecordMapper rowMapper = new SqlDataMapper().new HelpdeskRowRecordMapper();
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(Arrays.asList(new HelpdeskDto()));
		assertEquals(rowMapper.getClass(), helpdeskDao.getHelpdeskForId(1L, 1L).getClass());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getChecklistItemsForHelpdeskTest() {
		List<ChecklistItem> checklistItems = new ArrayList<>();
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(checklistItems);
		assertEquals(checklistItems.getClass(), helpdeskDao.getChecklistItemsForHelpdesk(1L, 1L).getClass());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHelpdeskAdminsTest() {
		Mockito.when(jdbcTemplate.queryForList(Matchers.anyString(), Matchers.<Object[]>anyVararg(), eq(Long.class)))
				.thenReturn(Arrays.asList(1L, 2L));
		assertEquals(2, helpdeskDao.getHelpdeskAdmins(1L).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void addUpdateHelpdeskUsersTest() {
		Helpdesk helpdesk = new Helpdesk();
		helpdesk.setUserIds(Arrays.asList(1L, 2L));
		helpdesk.setAllowAllUsers(false);
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(1);
		assertEquals(true, helpdeskDao.addUpdateHelpdeskUsers(helpdesk));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void addUpdateHelpdeskUsersTrueTest() {
		Helpdesk helpdesk = new Helpdesk();
		helpdesk.setUserIds(Arrays.asList(1L, 2L));
		helpdesk.setAllowAllUsers(true);
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(1);
		assertEquals(true, helpdeskDao.addUpdateHelpdeskUsers(helpdesk));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteTypeForHelpdeskTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(1);
		assertEquals(true, helpdeskDao.deleteTypeForHelpdesk(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteWorkflowForHelpdeskTypeTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(1);
		assertEquals(true, helpdeskDao.deleteWorkflowForHelpdeskType(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteChecklistForHelpdeskTypeTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(1);
		assertEquals(true, helpdeskDao.deleteChecklistForHelpdeskType(1L, 1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteChecklistForHelpdeskTypeFalseTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(0);
		assertEquals(false, helpdeskDao.deleteChecklistForHelpdeskType(1L, 1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAppIdAndHelpDeskIdTest() {
		List<HelpDeskApp> helpdeskApp = new ArrayList<HelpDeskApp>();
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(helpdeskApp);
		assertEquals(0, helpdeskDao.getAppIdAndHelpDeskId().size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHelpdeskObjectFromHelpdeskIdTest() {
		List<Helpdesk> helpdesk = new ArrayList<Helpdesk>();
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(helpdesk);
		assertEquals(0, helpdeskDao.getHelpdeskObjectFromHelpdeskId().size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAdminForHelpeskIdTest() {
		List<User> user = new ArrayList<User>();
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(user);
		assertEquals(0, helpdeskDao.getAdminForHelpeskId(1L).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAllHelpdesksNullTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(new ArrayList<>(), helpdeskDao.getAllHelpdesks(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHelpdeskForIdNullTest() {
		HelpdeskRowRecordMapper rowMapper = new SqlDataMapper().new HelpdeskRowRecordMapper();
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(rowMapper.getClass(), helpdeskDao.getHelpdeskForId(1L, 1L).getClass());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getChecklistItemsForHelpdeskNullTest() {
		List<ChecklistItem> checklistItems = new ArrayList<>();
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(checklistItems.getClass(), helpdeskDao.getChecklistItemsForHelpdesk(1L, 1L).getClass());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHelpdeskAdminsNullTest() {
		Mockito.when(jdbcTemplate.queryForList(Matchers.anyString(), Matchers.<Object[]>anyVararg(), eq(Long.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(null, helpdeskDao.getHelpdeskAdmins(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void addUpdateHelpdeskUsersNullTest() {
		Helpdesk helpdesk = new Helpdesk();
		helpdesk.setUserIds(Arrays.asList(1L, 2L));
		helpdesk.setAllowAllUsers(false);
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertEquals(false, helpdeskDao.addUpdateHelpdeskUsers(helpdesk));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteTypeForHelpdeskNullTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertEquals(false, helpdeskDao.deleteTypeForHelpdesk(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteWorkflowForHelpdeskTypeNullTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertEquals(false, helpdeskDao.deleteWorkflowForHelpdeskType(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteChecklistForHelpdeskTypeNullTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertEquals(false, helpdeskDao.deleteChecklistForHelpdeskType(1L, 1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAppIdAndHelpDeskIdNullTest() {
		Mockito.when(jdbcTemplate.query(Matchers.anyString(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(null, helpdeskDao.getAppIdAndHelpDeskId());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getHelpdeskObjectFromHelpdeskIdNullTest() {
		Mockito.when(jdbcTemplate.query(Matchers.anyString(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(null, helpdeskDao.getHelpdeskObjectFromHelpdeskId());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAdminForHelpeskIdNullTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(null, helpdeskDao.getAdminForHelpeskId(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getUsersForHelpeskIdNullTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(new ArrayList<>(), helpdeskDao.getUsersForHelpeskId(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteTypeForHelpdeskTrueTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(0);
		assertEquals(false, helpdeskDao.deleteTypeForHelpdesk(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteWorkflowForHelpdeskTypeTrueTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(0);
		assertEquals(false, helpdeskDao.deleteWorkflowForHelpdeskType(1L));
	}

}
