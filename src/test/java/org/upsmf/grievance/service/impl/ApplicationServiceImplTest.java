package org.upsmf.grievance.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.upsmf.grievance.service.impl.ApplicationServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.upsmf.grievance.dao.ApplicationDao;
import org.upsmf.grievance.model.App;
import org.upsmf.grievance.model.Role;
import org.upsmf.grievance.model.ServiceRequest;
import org.upsmf.grievance.model.StatusIdMap;
import org.upsmf.grievance.model.User;

public class ApplicationServiceImplTest {

	@Mock
	ApplicationDao applicationDao;

	@InjectMocks
    ApplicationServiceImpl applicationService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void createAppTest() {
		Mockito.when(applicationDao.createApp(Matchers.any(App.class), Matchers.any(User.class))).thenReturn(new App());
		Mockito.when(applicationDao.updateApp(Matchers.any(App.class), Matchers.any(User.class))).thenReturn(new App());
		assertEquals(new App().getClass(), applicationService.createApp(new App(), new User()).getClass());
		App a = new App();
		a.setId(5L);
		assertEquals(new App().getClass(), applicationService.createApp(a, new User()).getClass());
	}

	@Test
	public void getAppTestTrue() {
		Mockito.when(applicationDao.getApp(Matchers.any(Long.class))).thenReturn(new ArrayList<App>());
		Mockito.when(applicationDao.getAllApps()).thenReturn(new ArrayList<App>());
		Mockito.when(applicationDao.getAppsByOrgId(Matchers.any(Long.class))).thenReturn(new ArrayList<App>());
		assertEquals(0, applicationService.getApp(null, "a", new User()).size());
		assertEquals(0, applicationService.getApp(1L, "a", new User()).size());
		User user = new User();
		Role role = new Role();
		role.setName("SuperAdmin");
		user.setRoles(Arrays.asList(role));
		assertEquals(0, applicationService.getApp(null, "a", user).size());
		user.setOrgId(1L);
		assertEquals(0, applicationService.getApp(null, "a", user).size());
	}

	@Test
	public void mapAppsToHelpdeskTest() {
		Mockito.when(applicationDao.mapAppsToHelpdesk(Matchers.any(StatusIdMap.class))).thenReturn(true);
		assertEquals(true, applicationService.mapAppsToHelpdesk(new StatusIdMap()));
	}

	@Test
	public void getServiceRequestsTest() {
		Mockito.when(applicationDao.getServiceRequests()).thenReturn(new ArrayList<ServiceRequest>());
		assertEquals(0, applicationService.getServiceRequests().size());
	}

	@Test
	public void getAppIdAndAppObjectTest() {
		Mockito.when(applicationDao.getAppIdAndAppObject()).thenReturn(new ArrayList<App>());
		assertEquals(0, applicationService.getAppIdAndAppObject().size());
	}

	@Test
	public void getAppTest() {
		Mockito.when(applicationDao.getAppsByOrgId(Matchers.any(Long.class))).thenReturn(new ArrayList<App>());
		assertEquals(0, applicationService.getApp(1L).size());
	}
}
