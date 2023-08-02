package org.upsmf.grievance.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.upsmf.grievance.dto.ChangePasswordDto;
import org.upsmf.grievance.dto.LoginDto;
import org.upsmf.grievance.dto.OrgUserRoleDto;
import org.upsmf.grievance.dto.UserDto;
import org.upsmf.grievance.model.Access;
import org.upsmf.grievance.model.Action;
import org.upsmf.grievance.model.HelpDeskApp;
import org.upsmf.grievance.model.Role;
import org.upsmf.grievance.model.User;


public interface UserService {

	/**
	 * This method receives the List of Role IDs from the controller and passes the
	 * same to DAO to fetch the List of Actions allowed and configured for the Role
	 * ID
	 *
	 * @param roleID
	 * @return
	 */
	public List<Action> findAllActionsByRoleID(List<Integer> roleID);

	/**
	 * This method receives the existing User object to update the details in the
	 * Database for the respective User
	 *
	 * @param file
	 *
	 * @param user
	 * @return
	 */
	User update(MultipartFile file, User user);

	/**
	 * This method supports pagination and fetches the User Profiles for the
	 * respective search criteria Search can happen based on Page Number, Number of
	 * Records, Active status of the user Keyword to search the user and also based
	 * on the Roles Assigned to the User
	 *
	 * @param pageNumber
	 * @param numberOfRecords
	 * @param orgId
	 * @param roles
	 * @return
	 */
	List<User> findAll(Long orgId);

	/**
	 * This method receives the String Username to fetch the respective User record
	 * from the Database
	 *
	 * @param username
	 * @return
	 */
	User findOne(String username, Boolean withRoles, Boolean withActions);

	/**
	 * This method receives the Long ID to fetch the respective User Profile from
	 * the database
	 *
	 * @param id
	 * @return
	 */
	User findById(Long id);

	/**
	 * This method receives the User ID and find the corresponding roles for the
	 * User ID and lists out the Roles as a response
	 *
	 * @param userId
	 * @return
	 */
	public List<Role> findAllRolesByUser(Long userId);

	/**
	 * This method receives the User ID and then fetches the Role ID for the same
	 * With the help of Role ID, it fetches the corresponding Actions which are
	 * allowed and mapped. As a result, this responds Action object
	 *
	 * @param userId
	 * @return
	 */

	/**
	 * This method receives the User Role Object. For a User ID and each Role ID in
	 * the list, this method creates a mapping so that User Role Mapping is added
	 *
	 * @param userRole
	 * @return
	 */
	Boolean mapUserToRole(User user);

	Long checkUserNameExists(String username);

	String uploadFile(MultipartFile file, long userId);

	Long getNumberOfUsers(Long role, Boolean active);

	Long getNumberOfRoles();

	Boolean invalidateToken(String authToken);

	Boolean findUserByToken(String authToken);

	Long fetchAuthTokenReference(String authToken);

	Boolean checkUserTokenExists(Long userId, String deviceToken);

	User updateUserImage(User profile);

	public LoginDto login(UserDto userDto);

	public boolean forgotPassword(UserDto userDto);

	public boolean changePassword(ChangePasswordDto changePasswordDto);

	User save(MultipartFile file, long authUserId, User user);

	public List<OrgUserRoleDto> getAllOrgUsers();

	public List<OrgUserRoleDto> getAllUserRoles();

	List<HelpDeskApp> getAppIdAndHelpDeskId();

	public List<User> getUserIdAndUserName();

	String getFile(Long userId) throws IOException;

	public Map<String, Object> getUserInfoObjects(String userId);

	Long saveAnonymousUser(User user);

	public void getReviews() throws IOException;

	public Access getReviewConfig(Long id);

}
