package org.upsmf.grievance.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.upsmf.grievance.service.impl.RoleActionServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.upsmf.grievance.dao.RoleDao;
import org.upsmf.grievance.model.Role;

public class RoleServiceImplTest {

	@Mock
	RoleDao roleDao;

	@InjectMocks
    RoleActionServiceImpl roleService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void saveRoleTest() {
		Mockito.when(roleDao.saveRole(Matchers.any(Role.class))).thenReturn(new Role());
		assertEquals(new Role().getClass(), roleService.saveRole(new Role()).getClass());
	}

	@Test
	public void updateRoleTest() {
		Mockito.when(roleDao.updateRole(Matchers.any(Role.class))).thenReturn(new Role());
		assertEquals(new Role().getClass(), roleService.updateRole(new Role()).getClass());
	}

	@Test
	public void getAllRolesTest() {
		Mockito.when(roleDao.getAllRoles(Matchers.anyLong())).thenReturn(new ArrayList<Role>());
		assertEquals(0, roleService.getAllRoles(1L).size());
	}

	@Test
	public void findByIdTest() {
		Mockito.when(roleDao.findById(Matchers.any(Role.class))).thenReturn("d");
		assertEquals("d", roleService.findById(new Role()));
	}

	@Test
	public void getAllOrgRolesTest() {
		Mockito.when(roleDao.getAllOrgRoles()).thenReturn(new ArrayList<Role>());
		assertEquals(0, roleService.getAllOrgRoles().size());
	}

}