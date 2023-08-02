package org.upsmf.grievance.dao;

import java.io.IOException;
import java.util.List;

import org.upsmf.grievance.dto.ChangePasswordDto;
import org.upsmf.grievance.dto.LoginDto;
import org.upsmf.grievance.dto.OrgUserRoleDto;
import org.upsmf.grievance.dto.UserDto;
import org.upsmf.grievance.model.Access;
import org.upsmf.grievance.model.Action;
import org.upsmf.grievance.model.CommonDataModel;
import org.upsmf.grievance.model.HelpDeskApp;
import org.upsmf.grievance.model.Role;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.model.UserAuthentication;
import org.upsmf.grievance.model.mapper.SqlDataMapper.UserDetailsMapper;
import org.upsmf.grievance.model.mapper.SqlDataMapper.UserRoleMapper;

public interface UserDao {

	/**
	 * This method is used to fetch the User high level Object from the database
	 * based on the username parameter which is being passed
	 *
	 * @param username
	 * @return
	 */
	public User findByUsername(String username, Boolean withRoles, Boolean withActions);

	/**
	 * This method is used to fetch the User Detailed object from the database based
	 * on the User ID which is being passed
	 *
	 * @param id
	 * @return
	 */
	public List<User> findOne(Long id);

	/**
	 * This method receives the User ID and the List of Roles which are associated
	 * with the user Save the same to database for further Role Based Access
	 *
	 * @param list
	 * @param userId
	 *
	 * @param userRole
	 * @return
	 */
	public Boolean mapUserToRole(long userId, List<Role> list);

	/**
	 * This method receives the User Profile Object with updated Image URL and
	 * updates the same against the User ID
	 *
	 * @param profile
	 * @return
	 */
	public User updateUserImage(User profile);

	/**
	 * While adding a new user to the system, this method is called with Email ID
	 * and Phone Number to verify whether there already exists a user with same
	 * username as that of the Email ID and Phone Number This method responds with a
	 * long value of the User ID if exists and returns 0 in the case of negative
	 * scenario
	 *
	 * @param emailId
	 * @param phoneNo
	 * @return
	 */
	public Long checkUserNameExists(String username);

	/**
	 * On receiving the Role ID, this method fetches the Actions which are mapped to
	 * that role
	 *
	 * @param roleID
	 * @return
	 */
	public List<Action> findAllActionsByRoleID(Integer roleID);

	/**
	 * In order to show the count of Users available in the system, this method is
	 * invoked The method responds with the count of users available in the system
	 *
	 * @return
	 */
	public Long getNumberOfUsers(Long role, Boolean active);

	/**
	 * In order to show the count of Roles available in the system, this method is
	 * invoked The method responds with the count of roles available in the system
	 *
	 * @return
	 */
	public Long getNumberOfRoles();

	/**
	 * This method receives the JWT Auth Token and invalidates the token from the
	 * Jwt Token Store and also removes the entry of the Token from the Database
	 *
	 * @param authToken
	 * @return
	 */
	public Boolean invalidateToken(String authToken);

	/**
	 * This method receives the Auth Token and finds out whether there is an active
	 * user for that Authentication Token Auth Token in this method is the JWT Token
	 *
	 * @param authToken
	 * @return
	 */
	public Boolean findUserByToken(String authToken);

	/**
	 * This method receives the Auth Token of the FCM and verifies whether the token
	 * is already registered against any User ID or not.
	 *
	 * @param userId
	 * @param deviceToken
	 * @return
	 */
	public Boolean checkUserTokenExists(Long userId, String deviceToken);

	/**
	 * This method receives the Device Token and the User ID and updates it against
	 * the record which is already available in the system database
	 *
	 * @param userId
	 * @param deviceToken
	 * @return
	 */

	public UserAuthentication findOneUserAuthentication(Long id);

	public UserDetailsMapper findListOfUsers(List<Long> userIdList);

	public User update(User user);

	public UserRoleMapper findAllRolesByUser(Long userId);

	public Long fetchAuthTokenReference(String authToken);

	int insertIntoPassword(Long id, String password);

	int getAuthId(Long userId);

	boolean customAuth(UserDto userDto);

	User insertIntoUser(final User user);

	Long insertAnonymousUser(User user);

	public boolean changePassword(ChangePasswordDto changePasswordDto);

	public long forgotPassword(UserDto userDto);

	boolean saveForgotPassword(long userId, String password);

	public User getUserDetailsByEmail(String username);

	public LoginDto login(UserDto userDto);

	public CommonDataModel getAuthDomain(UserDto userDto);

	public boolean getFirstAdminsOfOrg(Long id);

	public Boolean onBoardingCheck(Long orgId, Long id);

	boolean isPasswordMatch(long userId, String password);

	List<User> findAll(Long orgId);

	List<OrgUserRoleDto> getAllOrgUsers();

	List<OrgUserRoleDto> getAllUserRoles();

	public List<HelpDeskApp> getAppIdAndHelpDeskId();

	public List<User> getUserIdAndUserName();

	public void getReviews() throws IOException;

	Access getReviewConfig(Long orgId);

}
