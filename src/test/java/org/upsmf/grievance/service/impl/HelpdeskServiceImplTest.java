package org.upsmf.grievance.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.upsmf.grievance.service.impl.HelpdeskServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import org.upsmf.grievance.dao.HelpdeskDao;
import org.upsmf.grievance.dto.HelpdeskDto;
import org.upsmf.grievance.dto.HelpdeskTypeDto;
import org.upsmf.grievance.model.HelpDeskApp;
import org.upsmf.grievance.model.Helpdesk;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.model.mapper.SqlDataMapper.HelpdeskRowRecordMapper;

public class HelpdeskServiceImplTest {

	@Mock
	HelpdeskDao helpdeskDao;

	@Spy
    HelpdeskServiceImpl helpdesk;

	@InjectMocks
	HelpdeskServiceImpl helpdeskService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void createUpdateHelpdeskTest() {
		Mockito.when(helpdeskDao.createUpdateHelpdesk(Matchers.any(Helpdesk.class))).thenReturn(true);
		Mockito.doReturn(true).when(helpdesk).configureHelpdesk(Matchers.any(HelpdeskDto.class),
				Matchers.any(User.class));
		assertEquals(true, helpdeskService.createUpdateHelpdesk(new HelpdeskDto(), new User()));
	}

	@Test
	public void getHelpdeskByIdTest() {
		Mockito.when(helpdeskDao.getHelpdeskForId(Matchers.anyLong(), Matchers.anyLong())).thenReturn(null);
		Mockito.doNothing().when(helpdesk).createHelpdeskListFromRowRecords(Matchers.any(HelpdeskRowRecordMapper.class),
				Matchers.anyListOf(HelpdeskDto.class));
		Mockito.doNothing().when(helpdesk).getAppsForHelpdesk(Matchers.anyListOf(HelpdeskDto.class));
		Mockito.doNothing().when(helpdesk).getAdminForHelpdesk(Matchers.anyListOf(HelpdeskDto.class));
		Mockito.doNothing().when(helpdesk).getUsersForHelpdesk(Matchers.anyListOf(HelpdeskDto.class));
		assertEquals(0, helpdeskService.getHelpdeskById(1L, 1L).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void configureHelpdeskTest() {
		List<HelpdeskDto> helpdeskList = new ArrayList<>();
		HelpdeskDto helpdeskDto = new HelpdeskDto();
		helpdeskDto.setId(1L);
		HelpdeskTypeDto type = new HelpdeskTypeDto();
		type.setId(1L);
		List<HelpdeskTypeDto> types = new ArrayList<>();
		types.add(type);
		helpdeskDto.setTypes(types);
		helpdeskList.add(helpdeskDto);
		Mockito.when(helpdeskDao.deleteTypeForHelpdesk(Matchers.anyLong())).thenReturn(true);
		Mockito.when(helpdeskDao.deleteWorkflowForHelpdeskType(Matchers.anyLong())).thenReturn(true);
		Mockito.when(helpdeskDao.deleteChecklistForHelpdeskType(Matchers.anyLong(), Matchers.anyLong()))
				.thenReturn(true);
		Mockito.when(helpdeskDao.addTypeForHelpdesk(Matchers.any(HelpdeskTypeDto.class), Matchers.anyLong()))
				.thenReturn(true);
		Mockito.when(helpdeskDao.addWorkflowForHelpdeskType(Matchers.any(HelpdeskTypeDto.class))).thenReturn(true);
		Mockito.when(helpdeskDao.addChecklistForHelpdeskType(Matchers.any(HelpdeskTypeDto.class), Matchers.anyLong()))
				.thenReturn(true);
		assertTrue(helpdeskService.configureHelpdesk(helpdeskDto, new User()));
	}

	@Test
	public void getHelpdeskTest() {
		Mockito.when(helpdeskDao.getAllHelpdesks(Matchers.anyLong())).thenReturn(new ArrayList<HelpdeskDto>());
		assertEquals(0, helpdeskService.getHelpdesk(1L).size());
	}

	@Test
	public void addUpdateHelpdeskAdminsTest() {
		Mockito.when(helpdeskDao.addUpdateHelpdeskAdmins(Matchers.any(Helpdesk.class))).thenReturn(true);
		assertEquals(true, helpdeskService.addUpdateHelpdeskAdmins(new Helpdesk(), new User()));
	}

	@Test
	public void getHelpdeskAdminsTest() {
		Mockito.when(helpdeskDao.getHelpdeskAdmins(Matchers.anyLong())).thenReturn(new ArrayList<Long>());
		assertEquals(0, helpdeskService.getHelpdeskAdmins(1L).size());
	}

	@Test
	public void getAppIdAndHelpDeskIdTest() {
		Mockito.when(helpdeskDao.getAppIdAndHelpDeskId()).thenReturn(new ArrayList<HelpDeskApp>());
		assertEquals(0, helpdeskService.getAppIdAndHelpDeskId().size());
	}

	@Test
	public void getHelpdeskObjectFromHelpdeskIdTest() {
		Mockito.when(helpdeskDao.getHelpdeskObjectFromHelpdeskId()).thenReturn(new ArrayList<Helpdesk>());
		assertEquals(0, helpdeskService.getHelpdeskObjectFromHelpdeskId().size());
	}

}