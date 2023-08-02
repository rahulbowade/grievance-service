package org.upsmf.grievance.dao;

import java.util.List;

import org.upsmf.grievance.model.Organization;
import org.upsmf.grievance.model.S3Config;
import org.upsmf.grievance.model.StatusIdMap;
import org.upsmf.grievance.model.User;

public interface SuperAdminDao {

	List<Organization> getAllOrganization();

	Organization addOrganization(Organization organization);

	boolean updateOrganizationById(Organization organization);

	boolean deleteOrganization(Organization organization);

	int getOrganizationByUserId(long authUserId);

	boolean mapUserToOrg(long userId, int organisationId);

	boolean addAdmin(long userId);

	boolean removeAdmin(long userId);

	User userDetailsByUserId(long userId);

	boolean mapAppsToOrg(StatusIdMap statusIdMap);

	List<Organization> getOrganizationByUser(Long userId);

	S3Config getS3Access();

	Organization getOrganizationByIdV2(Long id);

}
