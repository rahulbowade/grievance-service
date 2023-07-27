package org.upsmf.grievance.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;

import java.util.ArrayList;

import org.upsmf.grievance.dao.impl.RoleDaoImpl;
import org.upsmf.grievance.dao.impl.SuperAdminDaoImpl;
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

import org.upsmf.grievance.model.Role;

public class RoleDaoImplTest {

	@Mock
	JdbcTemplate jdbcTemplate; 

	@InjectMocks
    RoleDaoImpl roleDao;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this); 
	}

	@Spy
	private RoleDaoImpl role;

	@Spy
	private SuperAdminDaoImpl superAdmin;

	@SuppressWarnings("unchecked")
	@Test
	public void saveRoleTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(1);
		assertEquals(new Role().getClass(), roleDao.saveRole(new Role()).getClass());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void updateRoleTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(1);
		assertEquals(new Role().getClass(), roleDao.updateRole(new Role()).getClass());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAllRolesTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(new ArrayList<Role>());
		assertEquals(0, roleDao.getAllRoles(1L).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void findByIdTest() {
		Mockito.when(
				jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.<Object[]>anyVararg(), eq(String.class)))
				.thenReturn("d");
		assertEquals("d", roleDao.findById(new Role()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAllOrgRolesTest() {
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.any(RowMapper.class)))
				.thenReturn(new ArrayList<Role>());
		assertEquals(0, roleDao.getAllOrgRoles().size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void saveRoleNullTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertEquals(null, roleDao.saveRole(new Role()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void updateRoleNullTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertEquals(null, roleDao.updateRole(new Role()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAllRolesNullTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(null, roleDao.getAllRoles(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void findByIdNullTest() {
		Mockito.when(
				jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.<Object[]>anyVararg(), eq(String.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(null, roleDao.findById(new Role()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAllOrgRolesNullTest() {
		Mockito.when(jdbcTemplate.query(Matchers.anyString(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(new ArrayList<>(), roleDao.getAllOrgRoles());
	}

}
