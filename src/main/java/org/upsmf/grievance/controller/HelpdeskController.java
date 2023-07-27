package org.upsmf.grievance.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.upsmf.grievance.dto.HelpdeskDto;
import org.upsmf.grievance.executor.MasterDataManager;
import org.upsmf.grievance.model.Helpdesk;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.service.HelpdeskService;
import org.upsmf.grievance.util.PathRoutes;
import org.upsmf.grievance.util.ProjectUtil;
import org.upsmf.grievance.util.ResponseGenerator;
import org.upsmf.grievance.util.Sql;

/**
 * This controller will handle all the helpdesk related API's
 *
 * @author nivetha
 *
 */
@RestController
@RequestMapping(PathRoutes.HELPDESK_URL)
public class HelpdeskController {

	private static final String ID = "id";
	private static final String USER_INFO = "UserInfo";
	private static final String USER_ID = "userId";
	@Autowired
	private HelpdeskService helpdeskService;

	/**
	 * This method will add or update helpdesk of an organization
	 *
	 * @param helpdesk
	 *            Helpdesk
	 * @param user
	 *            User
	 * @return String
	 * @throws JsonProcessingException
	 */
	@PostMapping(PathRoutes.HelpdeskRoutes.CREATE_UPDATE_HELPDESK)
	public String createUpdateHelpdesk(@RequestBody HelpdeskDto helpdeskDto,
			@RequestAttribute(value = USER_INFO) User user) throws JsonProcessingException {

		boolean response = helpdeskService.createUpdateHelpdesk(helpdeskDto, user);

		if (response) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse();
	}

	/**
	 * This method gives all the active helpdesk of an organization
	 *
	 * @param user
	 *            User
	 * @return String
	 * @throws JsonProcessingException
	 */
	@GetMapping(PathRoutes.HelpdeskRoutes.GET_HELPDESK)
	public String getHelpdesk(@RequestParam(value = "orgId", required = true) Long orgId,
			@RequestParam(value = USER_ID, required = false) Long userId,
			@RequestAttribute(value = USER_INFO) User user, @RequestParam(value = ID, required = false) Long id)
			throws JsonProcessingException {
		List<HelpdeskDto> helpdesks;
		List<HelpdeskDto> newHelpdesks = new ArrayList<>();
		if (id != null && id > 0) {
			helpdesks = helpdeskService.getHelpdeskById(orgId, id);
			return ResponseGenerator.successResponse(helpdesks);
		} else {
			helpdesks = helpdeskService.getHelpdesk(orgId);
		}
		try {
			if (!ProjectUtil.isObjectNull(userId)) {
				if (MasterDataManager.getRoleMap().get(MasterDataManager.getUserRoleMap().get(userId)).getName()
						.equalsIgnoreCase(Sql.Common.SUPER_ADMIN)
						|| MasterDataManager.getRoleMap().get(MasterDataManager.getUserRoleMap().get(userId)).getName()
								.equalsIgnoreCase(Sql.Common.ORGADMIN)) {
					return ResponseGenerator.successResponse(helpdesks);
				} else {
					return ResponseGenerator.successResponse(checkIfHelpdeskEmpty(user, helpdesks, newHelpdesks));
				}
			} else {
				return ResponseGenerator.successResponse(checkIfHelpdeskEmpty(user, helpdesks, newHelpdesks));
			}
		} catch (Exception e) {
			return ResponseGenerator.failureResponse();
		}
	}

	private List<HelpdeskDto> checkIfHelpdeskEmpty(User user, List<HelpdeskDto> helpdesks,
			List<HelpdeskDto> newHelpdesks) {
		helpdeskService.getHelpdeskAdminUser(helpdesks);
		for (int i = 0; i < helpdesks.size(); i++) {
			HelpdeskDto helpdeskDto = helpdesks.get(i);
			List<Long> user2 = helpdeskDto.getAdmins().stream().map(User::getId).collect(Collectors.toList());
			List<Long> user3 = helpdeskDto.getUsers().stream().map(User::getId).collect(Collectors.toList());
			List<Long> combinedList = Stream.of(user2, user3).flatMap(x -> x.stream()).collect(Collectors.toList());
			for (Long u : combinedList) {
				if (u.equals(user.getId()) && !newHelpdesks.contains(helpdeskDto)) {
					newHelpdesks.add(helpdeskDto);
				}
			}
		}
		return newHelpdesks;
	}

	/**
	 * This method sets up the configuration for a helpdesk
	 *
	 * @param user
	 *            User
	 * @return String
	 * @throws JsonProcessingException
	 */
	@PostMapping(PathRoutes.HelpdeskRoutes.CONFIGURE_HELPDESK)
	public String configureHelpdesk(@RequestBody HelpdeskDto helpdeskDto,
			@RequestAttribute(value = USER_INFO) User user) throws JsonProcessingException {
		ControllerRequestValidator.validateConfigureHelpdeskData(helpdeskDto);
		boolean response = helpdeskService.configureHelpdesk(helpdeskDto, user);
		if (response) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse();

	}

	@PostMapping(PathRoutes.HelpdeskRoutes.ADD_UPDATE_HELPDESK_ADMINS)
	public String addUpdateHelpdeskAdmins(@RequestBody Helpdesk helpdesk,
			@RequestAttribute(value = USER_INFO) User user) throws JsonProcessingException {
		boolean response = helpdeskService.addUpdateHelpdeskAdmins(helpdesk, user);
		if (response) {
			return ResponseGenerator.successResponse(response);
		}
		return ResponseGenerator.failureResponse();
	}

	@GetMapping(PathRoutes.HelpdeskRoutes.GET_HELPDESK_ADMINS)
	public String getHelpdeskAdmins(@RequestParam(value = ID, required = true) Long id) throws JsonProcessingException {

		List<Long> admins = helpdeskService.getHelpdeskAdmins(id);

		return ResponseGenerator.successResponse(admins);
	}

	@GetMapping(PathRoutes.HelpdeskRoutes.GET_PERFORMANCE_WITH_ACCESSCONTROL)
	public String getPerformanceApiWithAccessControl() throws JsonProcessingException {
		return ResponseGenerator.successResponse("getPerformanceApiWithAccessControl");
	}

	@GetMapping(PathRoutes.HelpdeskRoutes.GET_PERFORMANCE_WITHOUT_ACCESSCONTROL)
	public String getPerformanceApiWithoutAccessControl() throws JsonProcessingException {
		return ResponseGenerator.successResponse("getPerformanceApiWithoutAccessControl");
	}

}
