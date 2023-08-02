package org.upsmf.grievance.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.upsmf.grievance.dao.SuperAdminDao;
import org.upsmf.grievance.executor.MasterDataManager;
import org.upsmf.grievance.model.Organization;
import org.upsmf.grievance.model.StatusIdMap;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.service.SuperAdminService;
import org.upsmf.grievance.util.Constants;
import org.upsmf.grievance.util.JsonKey;
import org.upsmf.grievance.util.SendMail;

@Service(value = Constants.SUPER_ADMIN_SERVICE)
public class SuperAdminServiceImpl implements SuperAdminService {
	public static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private SuperAdminDao superAdminDao;

	@Override
	public List<Organization> getAllOrganization() {
		return superAdminDao.getAllOrganization();
	}

	@Override
	public Organization addOrganization(Organization organization) {
		return superAdminDao.addOrganization(organization);
	}

	@Override
	public boolean updateOrganizationById(Organization organization) {

		return superAdminDao.updateOrganizationById(organization);
	}

	@Override
	public Organization getOrganizationById(Long id) {
		return superAdminDao.getOrganizationByIdV2(id);
	}

	@Override
	public boolean deleteOrganization(Organization organization) {

		return superAdminDao.deleteOrganization(organization);
	}

	@Override
	public boolean addAdmin(long userId) {
		boolean value = superAdminDao.addAdmin(userId);
		if (value) {
			User user = superAdminDao.userDetailsByUserId(userId);
			user.setOrgId(MasterDataManager.getUserOrgMap().get(userId));
			String email = user.getUsername();
			Map<String, String> keyValue = new HashMap<>();
			keyValue.put(JsonKey.FIRST_NAME, user.getName());
			keyValue.put(JsonKey.LAST_NAME, user.getUsername());
			keyValue.put(JsonKey.ORGNAME, MasterDataManager.getOrgIdAndOrgNameMap().get(user.getOrgId()));
			LOGGER.info(MasterDataManager.getOrgIdAndOrgNameMap().get(user.getOrgId()));
			String[] emails = email.split(",");
			SendMail.sendMail(keyValue, emails, Constants.NEW_ADMIN, Constants.ADD_ADMIN_VM_FILE);
		}
		return value;
	}

	@Override
	public boolean removeAdmin(long userId) {
		boolean value = superAdminDao.removeAdmin(userId);
		if (value) {
			User user = superAdminDao.userDetailsByUserId(userId);
			user.setOrgId(MasterDataManager.getUserOrgMap().get(userId));
			String email = user.getUsername();
			Map<String, String> keyValue = new HashMap<>();
			keyValue.put(JsonKey.FIRST_NAME, user.getName());
			keyValue.put(JsonKey.ORGNAME, MasterDataManager.getOrgIdAndOrgNameMap().get(user.getOrgId()));
			LOGGER.info(MasterDataManager.getOrgIdAndOrgNameMap().get(user.getOrgId()));
			String[] emails = email.split(",");
			SendMail.sendMail(keyValue, emails, Constants.DELETE_ADMIN, Constants.DELETE_ADMIN_VM_FILE);
		}
		return value;
	}

	@Override
	public boolean mapAppsToOrg(StatusIdMap statusIdMap) {
		return superAdminDao.mapAppsToOrg(statusIdMap);

	}

	@Override
	public List<Organization> getOrganizationByUserId(Long userId) {

		return superAdminDao.getOrganizationByUser(userId);
	}
}
