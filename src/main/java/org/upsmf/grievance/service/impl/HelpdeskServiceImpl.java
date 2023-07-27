package org.upsmf.grievance.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.upsmf.grievance.dao.HelpdeskDao;
import org.upsmf.grievance.dao.SuperAdminDao;
import org.upsmf.grievance.dto.HelpdeskDto;
import org.upsmf.grievance.dto.HelpdeskTypeDto;
import org.upsmf.grievance.dto.HelpdeskWorkflowDto;
import org.upsmf.grievance.executor.MasterDataManager;
import org.upsmf.grievance.model.App;
import org.upsmf.grievance.model.ChecklistItem;
import org.upsmf.grievance.model.HelpDeskApp;
import org.upsmf.grievance.model.Helpdesk;
import org.upsmf.grievance.model.S3Config;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.model.mapper.SqlDataMapper.HelpdeskRowRecordMapper;
import org.upsmf.grievance.service.HelpdeskService;
import org.upsmf.grievance.util.S3FileManager;

@Service
public class HelpdeskServiceImpl implements HelpdeskService {

	private static final String ENCOUNTERED_AN_EXCEPTION = "Encountered an Exception : %s";

	private static final String USERPROFILE = "userprofile";

	public static final Logger LOGGER = LoggerFactory.getLogger(HelpdeskServiceImpl.class);

	@Autowired
	private HelpdeskDao helpdeskDao;

	@Autowired
	private SuperAdminDao superAdminDao;

	@Override
	public boolean createUpdateHelpdesk(HelpdeskDto helpdeskDto, User user) {
		Helpdesk helpdesk = new Helpdesk(helpdeskDto);
		helpdesk.setOrgId(helpdesk.getOrgId());
		helpdesk.setCreatedBy(user.getId());
		helpdesk.setUpdatedBy(user.getId());
		Boolean status = helpdeskDao.createUpdateHelpdesk(helpdesk);
		if (status && helpdeskDto.getId() == null) {
			helpdeskDto.setId(helpdesk.getId());
			configureHelpdesk(helpdeskDto, user);
		}
		return status;
	}

	@Override
	public List<HelpdeskDto> getHelpdesk(Long orgId) {
		return helpdeskDao.getAllHelpdesks(orgId);
	}

	@Override
	public List<HelpdeskDto> getHelpdeskById(Long orgId, Long id) {
		List<HelpdeskDto> helpdeskList = new ArrayList<>();
		try {
			HelpdeskRowRecordMapper mapper = helpdeskDao.getHelpdeskForId(orgId, id);
			createHelpdeskListFromRowRecords(mapper, helpdeskList);
			getAppsForHelpdesk(helpdeskList);
			helpdeskList = helpdeskDao.getHelpdeskAdminUser(helpdeskList);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
		}
		return helpdeskList;
	}

	public void getAppsForHelpdesk(List<HelpdeskDto> helpdeskList) {
		MasterDataManager.getHelpdeskIdFromAppId();
		try {
			for (HelpdeskDto dto : helpdeskList) {
				List<App> appList = new ArrayList<>();
				dto.setApps(appList);
				List<Long> appIds = MasterDataManager.getAppIdsForHelpdesk(dto.getId());
				if (!appIds.isEmpty()) {
					LOGGER.info("app idis  : {}", appIds);
				}
				for (Long appId : appIds) {
					App app = MasterDataManager.getAppFromAppId(appId);
					if (appId > 0) {
						LOGGER.info("app idis  : {}", appIds);
					}
					if (app != null) {
						dto.getApps().add(app);
					}
				}
			}
		} catch (Exception e) {

			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
		}
	}

	public void getUsersForHelpdesk(List<HelpdeskDto> helpdeskList) {
		try {
			for (HelpdeskDto dto : helpdeskList) {
				List<User> userList = helpdeskDao.getUsersForHelpeskId(dto.getId());
				userList = getImageUrl(userList);
				dto.setUsers(userList);
			}
		} catch (Exception e) {

			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
		}
	}

	public void getAdminForHelpdesk(List<HelpdeskDto> helpdeskList) {
		try {
			for (HelpdeskDto dto : helpdeskList) {
				List<User> userList = helpdeskDao.getAdminForHelpeskId(dto.getId());
				userList = getImageUrl(userList);
				dto.setAdmins(userList);
			}
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
		}
	}

	private List<User> getImageUrl(List<User> userList) {
		S3Config s3values = superAdminDao.getS3Access();
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).getImagePath() != null && !userList.get(i).getImagePath().isEmpty()
					&& userList.get(i).getImagePath().contains(USERPROFILE)) {
				String url = null;
				url = S3FileManager.getPreSignedURL(s3values, userList.get(i).getImagePath());
				userList.get(i).setImagePath(url);
			}
		}
		return userList;
	}

	public void createHelpdeskListFromRowRecords(HelpdeskRowRecordMapper mapper, List<HelpdeskDto> helpdeskList) {
		Iterator<Entry<Long, HelpdeskDto>> itr = mapper.getHelpdeskMap().entrySet().iterator();
		while (itr.hasNext()) {
			Entry<Long, HelpdeskDto> entry = itr.next();
			Long helpdeskId = entry.getKey();
			HelpdeskDto helpdeskDto = entry.getValue();
			List<Long> typeIds = mapper.getHelpdeskTypeMapping().get(helpdeskId);
			List<HelpdeskTypeDto> helpdeskTypes = new ArrayList<>();
			for (Long typeId : typeIds) {
				List<HelpdeskWorkflowDto> workflowStages = new ArrayList<>();
				HelpdeskTypeDto helpdeskTypeDto = mapper.getHelpdeskTypeMap().get(typeId);
				List<Long> workflowStageIds = mapper.getTypeWorkflowMapping().get(helpdeskTypeDto.getId());
				for (Long workflowStageId : workflowStageIds) {
					HelpdeskWorkflowDto workflowStageDto = mapper.getHelpdeskWorkflowMap().get(workflowStageId);
					workflowStages.add(workflowStageDto);
				}
				helpdeskTypeDto.setWorkflowStages(workflowStages);
				List<ChecklistItem> checklistItems = helpdeskDao.getChecklistItemsForHelpdesk(helpdeskDto.getId(),
						helpdeskTypeDto.getId());
				helpdeskTypeDto.setChecklistItems(checklistItems);
				helpdeskTypes.add(helpdeskTypeDto);
			}
			helpdeskDto.setTypes(helpdeskTypes);
			helpdeskList.add(helpdeskDto);
		}
	}

	@Override
	public boolean configureHelpdesk(HelpdeskDto helpdeskDto, User user) {
		if (helpdeskDto.getId() != null) {
			helpdeskDao.deleteTypeForHelpdesk(helpdeskDto.getId());
			List<HelpdeskTypeDto> typeDtoList = helpdeskDto.getTypes();
			for (HelpdeskTypeDto typeDto : typeDtoList) {
				helpdeskDao.deleteWorkflowForHelpdeskType(typeDto.getId());
				helpdeskDao.deleteChecklistForHelpdeskType(helpdeskDto.getId(), typeDto.getId());
				helpdeskDao.addTypeForHelpdesk(typeDto, helpdeskDto.getId());
				helpdeskDao.addWorkflowForHelpdeskType(typeDto);
				helpdeskDao.addChecklistForHelpdeskType(typeDto, helpdeskDto.getId());
			}
		}
		return true;
	}

	@Override
	public List<Long> getHelpdeskAdmins(Long id) {
		return helpdeskDao.getHelpdeskAdmins(id);
	}

	@Override
	public boolean addUpdateHelpdeskAdmins(Helpdesk helpdesk, User user) {
		return helpdeskDao.addUpdateHelpdeskAdmins(helpdesk);
	}

	@Override
	public List<HelpDeskApp> getAppIdAndHelpDeskId() {
		return helpdeskDao.getAppIdAndHelpDeskId();
	}

	@Override
	public List<Helpdesk> getHelpdeskObjectFromHelpdeskId() {
		return helpdeskDao.getHelpdeskObjectFromHelpdeskId();
	}

	@Override
	public List<User> getUsersForHelpeskId(Long id) {
		return helpdeskDao.getUsersForHelpeskId(id);
	}

	@Override
	public void getHelpdeskAdminUser(List<HelpdeskDto> helpdeskList) {
		helpdeskDao.getHelpdeskAdminUser(helpdeskList);
	}
}
