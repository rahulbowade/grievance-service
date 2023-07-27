package org.upsmf.grievance.dao.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.upsmf.grievance.dao.impl.SuperAdminDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import org.upsmf.grievance.model.Organization;
import org.upsmf.grievance.model.StatusIdMap;
import org.upsmf.grievance.model.User;

public class SuperAdminDaoImplTest {

	@Mock
	JdbcTemplate jdbcTemplate; 

	@InjectMocks
    SuperAdminDaoImpl superAdminDao;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this); 
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAllOrganizationTest() {
		List<Organization> list = new ArrayList<>();
		Organization org = new Organization();
		org.setId(1L);
		list.add(org);
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(list);
		assertEquals(1, superAdminDao.getAllOrganization().size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteOrganizationTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(1);
		assertEquals(true, superAdminDao.deleteOrganization(new Organization()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteOrganizationFalseTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(0);
		assertEquals(false, superAdminDao.deleteOrganization(new Organization()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getOrganizationByUserIdTest() {
		List<Organization> list = new ArrayList<>();
		Organization org = new Organization();
		org.setId(1L);
		list.add(org);
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(list);
		assertEquals(1, superAdminDao.getOrganizationByUserId(1l));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void mapUserToOrgTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(1);
		assertEquals(true, superAdminDao.mapUserToOrg(1L, 2));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void mapUserToOrgFalseTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(0);
		assertEquals(false, superAdminDao.mapUserToOrg(1L, 2));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void userDetailsByUserIdTest() {
		List<User> list = new ArrayList<>();
		User user = new User();
		user.setId(4L);
		list.add(user);
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(list);
		assertEquals(4L, superAdminDao.userDetailsByUserId(1L).getId().longValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void mapAppsToOrgTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(1);
		assertEquals(true, superAdminDao.mapAppsToOrg(new StatusIdMap()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getOrganizationByUserTest() {
		List<Organization> list = new ArrayList<>();
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(list);
		assertEquals(0, list.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAllOrganizationNullTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(new ArrayList<>(), superAdminDao.getAllOrganization());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deleteOrganizationNullTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertEquals(false, superAdminDao.deleteOrganization(new Organization()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getOrganizationByUserIdNullTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(0, superAdminDao.getOrganizationByUserId(1l));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void mapUserToOrgNullTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertEquals(false, superAdminDao.mapUserToOrg(1L, 2));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void userDetailsByUserIdNullTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(null, superAdminDao.userDetailsByUserId(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void mapAppsToOrgNullTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertEquals(false, superAdminDao.mapAppsToOrg(new StatusIdMap()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getOrganizationByUserNullTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(new ArrayList<>(), superAdminDao.getOrganizationByUser(1L));
	}
}
