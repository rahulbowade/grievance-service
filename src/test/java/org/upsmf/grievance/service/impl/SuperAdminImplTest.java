package org.upsmf.grievance.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.upsmf.grievance.service.impl.SuperAdminServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.upsmf.grievance.dao.SuperAdminDao;
import org.upsmf.grievance.model.Organization;
import org.upsmf.grievance.model.StatusIdMap;

public class SuperAdminImplTest {

	@Mock
	SuperAdminDao superAdminDao;

	@InjectMocks
    SuperAdminServiceImpl superAdminService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getAllOrganizationTest() {
		Mockito.when(superAdminDao.getAllOrganization()).thenReturn(new ArrayList<Organization>());
		assertEquals(0, superAdminService.getAllOrganization().size());
	}

	@Test
	public void addOrganizationTest() {
		Mockito.when(superAdminDao.addOrganization(Matchers.any(Organization.class))).thenReturn(new Organization());
		assertEquals(new Organization().getClass(), superAdminService.addOrganization(new Organization()).getClass());
	}

	@Test
	public void updateOrganizationByIdTest() {
		Mockito.when(superAdminDao.updateOrganizationById(Matchers.any(Organization.class))).thenReturn(true);
		assertEquals(true, superAdminService.updateOrganizationById(new Organization()));
	}

	/*
	 * @Test public void getOrganizationByIdTest() {
	 * Mockito.when(superAdminDao.getOrganizationById(Mockito.anyLong())).thenReturn
	 * (new Organization()); assertEquals(new Organization().getClass(),
	 * superAdminService.getOrganizationById(1L).getClass()); }
	 */

	@Test
	public void deleteOrganizationTest() {
		Mockito.when(superAdminDao.deleteOrganization(Matchers.any(Organization.class))).thenReturn(true);
		assertEquals(true, superAdminService.deleteOrganization(new Organization()));
	}

	@Test
	public void mapAppsToOrgTest() {
		Mockito.when(superAdminDao.mapAppsToOrg(Matchers.any(StatusIdMap.class))).thenReturn(true);
		assertEquals(true, superAdminService.mapAppsToOrg(new StatusIdMap()));
	}

	@Test
	public void getOrganizationByUserIdTest() {
		Mockito.when(superAdminDao.getOrganizationByUser(Matchers.anyLong())).thenReturn(new ArrayList<Organization>());
		assertEquals(0, superAdminService.getOrganizationByUserId(1L).size());
	}
}
