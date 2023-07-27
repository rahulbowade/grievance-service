package org.upsmf.grievance.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;

import java.util.ArrayList;
import java.util.List;

import org.upsmf.grievance.dao.impl.ApplicationDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import org.upsmf.grievance.model.App;
import org.upsmf.grievance.model.ServiceRequest;
import org.upsmf.grievance.model.StatusIdMap;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.util.S3FileManager;

public class ApplicatioDaoImplTest {

	@Mock
	JdbcTemplate jdbcTemplate; 

	@InjectMocks
    ApplicationDaoImpl applicationDao;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this); 
	}

	@Spy
	private ApplicationDaoImpl appDao;

	@Spy
	private S3FileManager s3;

	@SuppressWarnings("unchecked")
	@Test
	public void updateAppTest() {
		App app = new App();
		app.setActiveStatus(false);
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(1);
		assertEquals(app.getActiveStatus(), applicationDao.updateApp(app, new User()).getActiveStatus());
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	@SuppressWarnings("unchecked")
	@Test
	public void mapAppsToHelpdeskTest() {
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.<Object[]>anyVararg(), eq(Long.class)))
				.thenReturn(0L);
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(1);
		assertEquals(false, applicationDao.mapAppsToHelpdesk(new StatusIdMap()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void mapAppsToHelpdeskNullTest() {
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.<Object[]>anyVararg(), eq(Long.class)))
				.thenReturn(0L);
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertEquals(false, applicationDao.mapAppsToHelpdesk(new StatusIdMap()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAppTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(new ArrayList<App>());
		Mockito.doReturn(new ArrayList<App>()).when(appDao).getLogo(Matchers.any());
		assertEquals(0, applicationDao.getApp(1L).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getLogoTest() {
		List<App> a = new ArrayList<App>();
		App app = new App();
		a.add(app);
		app.setLogo("p");
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.<Object[]>anyVararg(), eq(Long.class)))
				.thenReturn(1L);
		Mockito.doReturn("d").when(appDao).getAppLogo(Matchers.any());
		assertEquals(a.getClass(), applicationDao.getLogo(a).getClass());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAllAppsTest() {
		List<App> a = new ArrayList<App>();
		App app = new App();
		a.add(app);
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(a);
		Mockito.doReturn(a).when(appDao).getLogo(Matchers.any());
		assertEquals(1, applicationDao.getAllApps().size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAppsByOrgIdTest() {
		List<App> a = new ArrayList<App>();
		App app = new App();
		a.add(app);
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(a);
		Mockito.doReturn(a).when(appDao).getLogo(Matchers.any());
		assertEquals(1, applicationDao.getAppsByOrgId(1L).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getServiceRequestsTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(new ArrayList<ServiceRequest>());
		assertEquals(0, applicationDao.getServiceRequests().size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAppIdAndAppObjectTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(new ArrayList<App>());
		assertEquals(0, applicationDao.getAppIdAndAppObject().size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void updateAppNullTest() {
		App app = new App();
		app.setActiveStatus(false);
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertEquals(app.getActiveStatus(), applicationDao.updateApp(app, new User()).getActiveStatus());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAppNullTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		Mockito.doReturn(new ArrayList<App>()).when(appDao).getLogo(Matchers.any());
		assertEquals(null, applicationDao.getApp(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getLogoNullTest() {
		List<App> a = new ArrayList<App>();
		App app = new App();
		a.add(app);
		app.setLogo("p");
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.<Object[]>anyVararg(), eq(Long.class)))
				.thenThrow(NullPointerException.class);
		Mockito.doReturn("d").when(appDao).getAppLogo(Matchers.any());
		assertEquals(a.getClass(), applicationDao.getLogo(a).getClass());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAllAppsNullTest() {
		List<App> a = new ArrayList<App>();
		App app = new App();
		a.add(app);
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		Mockito.doReturn(new ArrayList<App>()).when(appDao).getLogo(Matchers.any());
		assertEquals(null, applicationDao.getAllApps());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAppsByOrgIdNullTest() {
		List<App> a = new ArrayList<App>();
		App app = new App();
		a.add(app);
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		Mockito.doReturn(new ArrayList<App>()).when(appDao).getLogo(Matchers.any());
		assertEquals(new ArrayList<>(), applicationDao.getAppsByOrgId(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getServiceRequestsNullTest() {
		Mockito.when(jdbcTemplate.query(Matchers.anyString(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(null, applicationDao.getServiceRequests());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAppIdAndAppObjectNullTest() {
		Mockito.when(jdbcTemplate.query(Matchers.anyString(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(null, applicationDao.getAppIdAndAppObject());
	}

}
