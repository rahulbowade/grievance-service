package org.upsmf.grievance.dao;

import java.util.List;

import org.upsmf.grievance.dto.HelpdeskDto;
import org.upsmf.grievance.dto.HelpdeskTypeDto;
import org.upsmf.grievance.model.ChecklistItem;
import org.upsmf.grievance.model.HelpDeskApp;
import org.upsmf.grievance.model.Helpdesk;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.model.mapper.SqlDataMapper.HelpdeskRowRecordMapper;

public interface HelpdeskDao {

	/**
	 * This method will add or update helpdesk of an organization
	 *
	 * @param helpdesk
	 *            Helpdesk
	 * @return boolean
	 */
	boolean createUpdateHelpdesk(Helpdesk app);

	/**
	 * This method gives all the active helpdesk of an organization
	 *
	 * @param orgId
	 *            Long
	 * @return List<Helpdesk>
	 */
	Boolean deleteWorkflowForHelpdeskType(Long typeId);

	Boolean deleteChecklistForHelpdeskType(Long helpdeskId, Long typeId);

	Boolean deleteTypeForHelpdesk(Long helpdeskId);

	Boolean addTypeForHelpdesk(HelpdeskTypeDto helpdeskTypeDto, Long helpdeskId);

	Boolean addChecklistForHelpdeskType(HelpdeskTypeDto helpdeskTypeDto, Long helpdeskId);

	Boolean addWorkflowForHelpdeskType(HelpdeskTypeDto helpdeskTypeDto);

	List<Long> getHelpdeskAdmins(Long id);

	boolean addUpdateHelpdeskAdmins(Helpdesk helpdesk);

	List<HelpdeskDto> getAllHelpdesks(Long orgId);

	HelpdeskRowRecordMapper getHelpdeskForId(Long orgId, Long id);

	List<ChecklistItem> getChecklistItemsForHelpdesk(Long helpdeskId, Long typeId);

	List<HelpDeskApp> getAppIdAndHelpDeskId();

	public List<Helpdesk> getHelpdeskObjectFromHelpdeskId();

	boolean addUpdateHelpdeskUsers(Helpdesk helpdesk);

	List<User> getAdminForHelpeskId(Long helpdeskId);

	List<User> getUsersForHelpeskId(Long helpdeskId);

	List<HelpdeskDto> getHelpdeskAdminUser(List<HelpdeskDto> helpdeskList);

	List<Helpdesk> getHelpdeskByUserId(Long userId);
}
