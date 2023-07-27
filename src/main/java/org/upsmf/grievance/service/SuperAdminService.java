package org.upsmf.grievance.service;

import java.util.List;

import org.upsmf.grievance.model.Organization;
import org.upsmf.grievance.model.StatusIdMap;

public interface SuperAdminService {

	List<Organization> getAllOrganization();

	Organization addOrganization(Organization organization);

	boolean updateOrganizationById(Organization organization);

	Organization getOrganizationById(Long id);

	boolean deleteOrganization(Organization organization);

	boolean addAdmin(long userId);

	boolean removeAdmin(long userId);

	/**
	 * This method will map an application to an organization
	 *
	 * @param statusIdMap
	 * @return
	 */
	boolean mapAppsToOrg(StatusIdMap statusIdMap);

	List<Organization> getOrganizationByUserId(Long userId);

}
