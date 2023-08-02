package org.upsmf.grievance.service;

import java.util.List;

import org.upsmf.grievance.dto.HelpdeskDto;
import org.upsmf.grievance.model.HelpDeskApp;
import org.upsmf.grievance.model.Helpdesk;
import org.upsmf.grievance.model.User;

public interface HelpdeskService {

	/**
	 * This method will add or update helpdesk of an organization
	 *
	 * @param helpdesk
	 *            Helpdesk
	 * @return boolean
	 */
	boolean createUpdateHelpdesk(HelpdeskDto helpdeskDto, User user);

	/**
	 * This method gives all the active helpdesk of an organization
	 *
	 * @param orgId
	 *            Long
	 * @return List<Helpdesk>
	 */
	List<HelpdeskDto> getHelpdesk(Long orgId);

	List<HelpdeskDto> getHelpdeskById(Long orgId, Long id);

	boolean configureHelpdesk(HelpdeskDto helpdeskDto, User user);

	List<Long> getHelpdeskAdmins(Long id);

	boolean addUpdateHelpdeskAdmins(Helpdesk helpdesk, User user);

	List<HelpDeskApp> getAppIdAndHelpDeskId();

	public List<Helpdesk> getHelpdeskObjectFromHelpdeskId();

	List<User> getUsersForHelpeskId(Long id);

	void getHelpdeskAdminUser(List<HelpdeskDto> helpdeskList);
}
