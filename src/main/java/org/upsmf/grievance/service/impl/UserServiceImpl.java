package org.upsmf.grievance.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.upsmf.grievance.dao.HelpdeskDao;
import org.upsmf.grievance.dao.RoleDao;
import org.upsmf.grievance.dao.SuperAdminDao;
import org.upsmf.grievance.dao.UserDao;
import org.upsmf.grievance.dto.ChangePasswordDto;
import org.upsmf.grievance.dto.LoginDto;
import org.upsmf.grievance.dto.OrgUserRoleDto;
import org.upsmf.grievance.dto.UserDto;
import org.upsmf.grievance.executor.MasterDataManager;
import org.upsmf.grievance.model.Access;
import org.upsmf.grievance.model.Action;
import org.upsmf.grievance.model.CommonDataModel;
import org.upsmf.grievance.model.HelpDeskApp;
import org.upsmf.grievance.model.Role;
import org.upsmf.grievance.model.S3Config;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.model.mapper.SqlDataMapper.UserRoleMapper;
import org.upsmf.grievance.service.UserService;
import org.upsmf.grievance.util.Constants;
import org.upsmf.grievance.util.JsonKey;
import org.upsmf.grievance.util.ProjectUtil;
import org.upsmf.grievance.util.S3FileManager;
import org.upsmf.grievance.util.SendMail;
import org.upsmf.grievance.util.Sql;

@Service(value = Constants.USER_SERVICE)
public class UserServiceImpl implements UserDetailsService, UserService {
	private static final String ENCOUNTERED_AN_EXCEPTION = "Encountered an Exception : %s";

	private static final String USERPROFILE = "userprofile";

	public static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	RoleDao roleDao;

	@Autowired
	SuperAdminDao superAdminDao;

	@Autowired
	HelpdeskDao helpdeskDao;

	@Value("${image.source.aws}")
	private Boolean imageSource;

	@Override
	public List<Action> findAllActionsByRoleID(List<Integer> roleID) {
		List<Action> completeActions = new ArrayList<>();
		for (int roleid : roleID) {
			completeActions.addAll(userDao.findAllActionsByRoleID(roleid));
		}
		return completeActions;
	}

	@Override
	public Map<String, Object> getUserInfoObjects(String userId) {
		User user = userDao.findByUsername(userId, Boolean.FALSE, Boolean.FALSE);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		Map<String, Object> userInfoObjectMap = new HashMap<>();
		userInfoObjectMap.put("User", user);
		userInfoObjectMap.put("UserDetails", new org.springframework.security.core.userdetails.User(user.getUsername(),
				user.getPassword(), getAuthority()));
		return userInfoObjectMap;
	}

	@Override
	public UserDetails loadUserByUsername(String userId) {
		User user = userDao.findByUsername(userId, Boolean.FALSE, Boolean.FALSE);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				getAuthority());
	}

	private List<SimpleGrantedAuthority> getAuthority() {
		return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}

	@Override
	public User findOne(String username, Boolean withRoles, Boolean withActions) {
		return userDao.findByUsername(username, withRoles, withActions);
	}

	@Override
	public User findById(Long id) {
		User user = new User();
		List<Role> roleList = new ArrayList<>();
		try {
			List<User> mapper = userDao.findOne(id);
			if (!ProjectUtil.isObjectListNullOrEmpty(mapper)) {
				user = mapper.get(0);
				MasterDataManager.getAllUserRoles();
				if (user != null) {
					List<Long> roles = MasterDataManager.getUserRoleListMap().get(user.getId());
					for (int i = 0; i < roles.size(); i++) {
						roleList.add(MasterDataManager.getRoleMap().get(roles.get(i)));
					}
					user.setRoles(roleList);
					user.setOrgId(jdbcTemplate.queryForObject(Sql.Common.GET_ORG_ID_BY_USER_ID, new Object[] { id },
							Long.class));
					user.setOrganization(superAdminDao.getOrganizationByUser(id));
					user.setHelpdesk(helpdeskDao.getHelpdeskByUserId(id));
					String image = jdbcTemplate.queryForObject(Sql.Common.GET_IMAGE_PATH, new Object[] { id },
							String.class);
					user.setImagePath(image);
					setUserImage(user);
					return user;
				}
			}
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
		}
		return null;
	}

	private void setUserImage(User user) {
		if (!StringUtils.isBlank(user.getImagePath())) {
			try {
				if (user.getImagePath().contains(USERPROFILE)) {
					S3Config s3values = superAdminDao.getS3Access();
					String url = null;
					url = S3FileManager.getPreSignedURL(s3values, user.getImagePath());
					user.setImagePath(url);
				} else {
					String data = getFile(user.getId());
					user.setImagePath(data);
				}
			} catch (Exception e) {
				LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
			}
		}
	}

	@Override
	public User save(MultipartFile file, long authUserId, User user) {
		user.setUpdatedBy(authUserId);
		user = userDao.insertIntoUser(user);
		if (user.getId() == 0) {
			return null;
		} else {
			LOGGER.info("userId: {}", user.getId());
		}
		UserDto userDto = new UserDto();
		userDto.setName(user.getName());
		userDto.setUsername(user.getUsername());
		userDto.setId(user.getId());
		userDao.customAuth(userDto);

		boolean userRoleInsert = userDao.mapUserToRole(user.getId(), user.getRoles());
		if (!userRoleInsert && user.getId() > 0 && user.getId() != null) {
			LOGGER.info("Inserting into tt_user_role FAILED for user: {}", user.getId());
		}

		if (user.getOrgId() != null) {
			boolean userCompInsert = superAdminDao.mapUserToOrg(user.getId(), user.getOrgId().intValue());
			if (!userCompInsert && user.getId() > 0 && user.getId() != null) {
				LOGGER.info("Inserting into tt_user_comp FAILED for user: {}", user.getId());
			} else {
				MasterDataManager.getUserOrgMap().put(user.getId(), user.getOrgId());
			}
		}

		insertProfilePicture(file, user);
		return user;
	}

	@Override
	public Long saveAnonymousUser(User user) {
		Long userId = checkUserNameExists(user.getUsername());
		if (userId == 0) {
			userId = userDao.insertAnonymousUser(user);
			if (userId == 0) {
				return null;
			} else if (userId > 0 && userId != null) {
				LOGGER.info("userId: {}", userId);
			}
			if (user.getOrgId() != null) {
				boolean userCompInsert = superAdminDao.mapUserToOrg(userId, user.getOrgId().intValue());
				if (!userCompInsert && userId > 0 && userId != null) {
					LOGGER.info("Inserting into tt_user_comp FAILED for user: {}", userId);
				}
			}
		}
		return userId;
	}

	private void insertProfilePicture(MultipartFile file, User user) {
		try {
			if (imageSource) {
				if (user.getImagePath() != null) {
					String value = getImagePathValue(user);
					pathValue(user, value);
				}
			} else {
				String value = null;
				if (!file.isEmpty()) {
					value = uploadFile(file, user.getId());
				}
				jdbcTemplate.update(Sql.INSERT_PROFILE_PICTURE, new Object[] { value, user.getId() });
			}
		} catch (Exception e) {

			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
		}
	}

	private void pathValue(User user, String value) {
		try {
			jdbcTemplate.update(Sql.INSERT_PROFILE_PICTURE, new Object[] { value, user.getId() });
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
		}
	}

	@Override
	public User update(MultipartFile file, User user) {
		try {
			if (imageSource) {
				if (user.getImagePath() != null) {
					String value = null;
					value = getImagePathValue(user);
					user.setImagePath(value);
				}
			} else if (!file.isEmpty()) {
				String value = uploadFile(file, user.getId());
				user.setImagePath(value);
			}
		} catch (Exception e) {

			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
		}
		return userDao.update(user);
	}

	private String getImagePathValue(User user) {
		String value = null;
		try {
			Long organization = MasterDataManager.getUserOrgMap().get(user.getId());
			value = S3FileManager.filePath(user.getImagePath(), USERPROFILE, user.getId(), organization);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
		}
		return value;
	}

	@Override
	public List<Role> findAllRolesByUser(Long userId) {
		UserRoleMapper mapper = userDao.findAllRolesByUser(userId);
		List<Role> roleList = new ArrayList<>();
		Iterator<Entry<Long, Role>> itr = mapper.getRoleMap().entrySet().iterator();
		while (itr.hasNext()) {
			roleList.add(itr.next().getValue());
		}
		return roleList;
	}

	@Override
	public Boolean mapUserToRole(User user) {
		return userDao.mapUserToRole(user.getId(), user.getRoles());
	}

	@Override
	public User updateUserImage(User profile) {
		return userDao.updateUserImage(profile);
	}

	@Override
	public Long checkUserNameExists(String username) {
		return userDao.checkUserNameExists(username);
	}

	@Override
	public boolean changePassword(ChangePasswordDto changePasswordDto) {
		if (changePasswordDto.getOldPass().equals(changePasswordDto.getNewPass())) {

			return true;
		}
		LOGGER.info("Entering Dao from service");
		return userDao.changePassword(changePasswordDto);
	}

	@Override
	public boolean forgotPassword(UserDto userDto) {
		Map<String, String> keyValue = new HashMap<>();
		boolean response = false;
		long userId = userDao.forgotPassword(userDto);
		if (userId > 0) {
			response = true;
			String[] emails = { userDto.getUsername() };
			String randomPassword = ProjectUtil.getRandomStringVal();
			LOGGER.info(randomPassword);
			userDao.saveForgotPassword(userId, randomPassword);
			keyValue.put(JsonKey.PSWRD, randomPassword);
			SendMail.sendMail(keyValue, emails, Constants.PSWORD_REGENERATED, Constants.FORGOT_PSWORD_VM_FILE);
		}
		return response;
	}

	@Override
	public String uploadFile(MultipartFile file, long userId) {
		return null;
	}

	@Override
	public String getFile(Long userId) throws IOException {
		try {
			String imagePath = null;
			List<User> u = userDao.findOne(userId);
			if (!ProjectUtil.isObjectNull(u.get(0))) {
				imagePath = u.get(0).getImagePath();
			}
			if (!ProjectUtil.isStringNullOrEmpty(imagePath)) {
				Path path = Paths.get(Constants.UPLOADED_FOLDER + imagePath);
				readb(path);
			}
			return "http://aurora-images.tarento.com/images" + imagePath;
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
		}
		return null;
	}

	private void readb(Path path) {
		try {
			Files.readAllBytes(path);
		} catch (final IOException e) {

		}
	}

	@Override
	public Long getNumberOfUsers(Long role, Boolean active) {
		return userDao.getNumberOfUsers(role, active);
	}

	@Override
	public Long getNumberOfRoles() {
		return userDao.getNumberOfRoles();
	}

	@Override
	public Boolean invalidateToken(String authToken) {
		return userDao.invalidateToken(authToken);
	}

	@Override
	public Boolean findUserByToken(String authToken) {
		return userDao.findUserByToken(authToken);
	}

	@Override
	public Boolean checkUserTokenExists(Long userId, String deviceToken) {
		return userDao.checkUserTokenExists(userId, deviceToken);
	}

	@Override
	public Long fetchAuthTokenReference(String authToken) {
		return userDao.fetchAuthTokenReference(authToken);
	}

	@Override
	public LoginDto login(UserDto userDto) {
		User userObject = userDao.getUserDetailsByEmail(userDto.getUsername());
		if (userObject == null) {
			LOGGER.info("getUserDetailsByEmail method is returning Null");
			return null;
		}
		userDto.setUsername(userObject.getUsername());
		userDto.setOrgId(userObject.getOrgId());
		userDto.setAuthType(userObject.getAuthType());
		userDto.setId(userObject.getId());
		userDao.getAuthDomain(userDto);
		if (authenticate(userDto)) {
			LoginDto loginData = userDao.login(userDto);
			if (!StringUtils.isBlank(userObject.getImagePath()) && userObject.getImagePath().contains(USERPROFILE)) {
				S3Config s3values = superAdminDao.getS3Access();
				String url = S3FileManager.getPreSignedURL(s3values, userObject.getImagePath());
				loginData.setImageUrl(url);
			}
			for (int i = 0; i < loginData.getRoles().size(); i++) {
				if (loginData.getRoles().get(i).getId() == Constants.ADMIN_ID) {
					boolean check = userDao.getFirstAdminsOfOrg(userObject.getId());
					if (check && userDao.onBoardingCheck(userObject.getOrgId(), userObject.getId())) {
						return loginData;
					}
					break;
				}
			}
			return loginData;
		}
		return null;

	}

	private boolean authenticate(UserDto userDto) {
		if (userDto.getGoogleEmail() != null) {
			return googleAuth(userDto);
		}
		CommonDataModel authDomainMap;
		authDomainMap = userDao.getAuthDomain(userDto);
		if (authDomainMap == null) {
			LOGGER.error("Method Name: authenticate, Message: authDomainMap is Null");
			return false;
		}
		switch (authDomainMap.getDescription()) {
		case Constants.GOOGLE_AUTH:
			return googleAuth(userDto);
		case Constants.CUSTOM_AUTH:
			return customAuth(userDto);
		default:
			return false;
		}
	}

	private boolean customAuth(UserDto userDto) {
		return userDao.isPasswordMatch(userDto.getId(), userDto.getPassword());
	}

	private boolean googleAuth(UserDto userDto) {
		try {
			final HttpTransport transport = new NetHttpTransport();
			final JsonFactory jsonFactory = new JacksonFactory();
			String email = null;
			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
					.setAudience(Collections.singletonList(userDto.getGoogleClientId())).build();

			GoogleIdToken idToken = verifier.verify(userDto.getGoogleIdToken());
			if (idToken != null) {
				Payload payload = idToken.getPayload();
				email = payload.getEmail();
			}
			if (StringUtils.isNotBlank(email) && email.trim().equalsIgnoreCase(userDto.getGoogleEmail())) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return false;

	}

	@Override
	public List<User> findAll(Long orgId) {
		return userDao.findAll(orgId);
	}

	@Override
	public List<OrgUserRoleDto> getAllOrgUsers() {
		return userDao.getAllOrgUsers();
	}

	@Override
	public List<OrgUserRoleDto> getAllUserRoles() {
		return userDao.getAllUserRoles();
	}

	@Override
	public List<HelpDeskApp> getAppIdAndHelpDeskId() {
		return userDao.getAppIdAndHelpDeskId();
	}

	@Override
	public List<User> getUserIdAndUserName() {
		return userDao.getUserIdAndUserName();
	}

	@Override
	public void getReviews() throws IOException {
		userDao.getReviews();
	}

	@Override
	public Access getReviewConfig(Long id) {
		return userDao.getReviewConfig(id);
	}

}