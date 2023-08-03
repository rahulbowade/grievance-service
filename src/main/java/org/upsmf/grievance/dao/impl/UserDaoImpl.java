package org.upsmf.grievance.dao.impl;

import static org.upsmf.grievance.util.Constants.TOKEN_PREFIX;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.velocity.VelocityContext;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.google.gson.Gson;
import org.upsmf.grievance.config.JwtTokenUtil;
import org.upsmf.grievance.dao.ApplicationDao;
import org.upsmf.grievance.dao.RoleDao;
import org.upsmf.grievance.dao.SuperAdminDao;
import org.upsmf.grievance.dao.TicketDao;
import org.upsmf.grievance.dao.UserDao;
import org.upsmf.grievance.dto.ChangePasswordDto;
import org.upsmf.grievance.dto.LoginDto;
import org.upsmf.grievance.dto.OrgUserRoleDto;
import org.upsmf.grievance.dto.UserDto;
import org.upsmf.grievance.executor.MasterDataManager;
import org.upsmf.grievance.interceptor.TicketsRequestInterceptor;
import org.upsmf.grievance.model.Access;
import org.upsmf.grievance.model.AccessResponse;
import org.upsmf.grievance.model.Action;
import org.upsmf.grievance.model.CommonDataModel;
import org.upsmf.grievance.model.HelpDeskApp;
import org.upsmf.grievance.model.KeyFactory;
import org.upsmf.grievance.model.Rev;
import org.upsmf.grievance.model.Role;
import org.upsmf.grievance.model.S3Config;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.model.UserAuthentication;
import org.upsmf.grievance.model.mapper.SqlDataMapper;
import org.upsmf.grievance.model.mapper.SqlDataMapper.RolesUserMapper;
import org.upsmf.grievance.model.mapper.SqlDataMapper.UserDetailsMapper;
import org.upsmf.grievance.model.mapper.SqlDataMapper.UserRoleMapper;
import org.upsmf.grievance.model.mapper.SqlDataMapper.UserRolesActionsMapper;
import org.upsmf.grievance.service.UserService;
import org.upsmf.grievance.util.Constants;
import org.upsmf.grievance.util.DateUtil;
import org.upsmf.grievance.util.JsonKey;
import org.upsmf.grievance.util.OneWayHashing;
import org.upsmf.grievance.util.ProjectUtil;
import org.upsmf.grievance.util.S3FileManager;
import org.upsmf.grievance.util.SendMail;
import org.upsmf.grievance.util.Sql;
import org.upsmf.grievance.util.Sql.UserQueries;

@Repository(Constants.USER_DAO)
public class UserDaoImpl implements UserDao {

	private static final String ENCOUNTERED_AN_EXCEPTION_S = "Encountered an Exception :  %s";

	private static final String EL_STIC123 = "El@stic123";

	private static final String ELASTIC = "elastic";

	public static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);

	@Value("${elk.data.up}")
	private boolean elkDataUp;

	@Value("${elasticsearch.url}")
	private String elasticsearchUrl;

	@Value("${elasticsearch.index}")
	private String elasticsearchIndex;

	@Value("${elasticsearch.type}")
	private String elasticsearchType;

	@Autowired
	private TicketsRequestInterceptor ticketsRequestInterceptor;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	RoleDao roleDao;

	@Autowired
	JwtTokenUtil jwtTokenUtil;

	@Autowired
	UserService userService;

	@Autowired
	SuperAdminDao superAdminDao;

	@Autowired
	TicketDao ticketDao;

	@Autowired
	ApplicationDao applicationDao;

	public UserDaoImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<Action> findAllActionsByRoleID(Integer roleID) {
		List<Action> actions = new ArrayList<>();
		try {
			actions = jdbcTemplate.query(UserQueries.GET_USER_ACTIONS, new Object[] { roleID },
					MasterDataManager.rowMapAction);
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an Exception while fetching all the actions by Role ID  %s",
					e.getMessage()));
		}
		return actions;
	}

	@Override
	public User findByUsername(String username, Boolean withRoles, Boolean withActions) {
		User user = null;
		try {
			if (withRoles && withActions) {
				UserRolesActionsMapper mapper = new SqlDataMapper().new UserRolesActionsMapper();
				user = jdbcTemplate
						.query(UserQueries.SELECT_USER_ROLES_ACTIONS_ON_USERNAME, new Object[] { username }, mapper)
						.get(0);
				user.setRoles(mapper.getRoleMap().values().stream().collect(Collectors.toList()));
				user.setActions(mapper.getActionMap().values().stream().collect(Collectors.toList()));
			} else if (withRoles) {
				user = jdbcTemplate.query(UserQueries.SELECT_USER_ROLES_ON_USERNAME, new Object[] { username },
						new SqlDataMapper().new UserRolesMapper()).get(0);
				user.setRoles(new SqlDataMapper().new UserRolesMapper().getRoleMap().values().stream()
						.collect(Collectors.toList()));
			} else {
				user = jdbcTemplate.query(UserQueries.SELECT_USER_ON_USERNAME, new Object[] { username },
						new SqlDataMapper().new UserMapper()).get(0);
			}
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an Exception while fetching the User by Username :  %s",
					e.getMessage()));
		}
		return user;
	}

	@Override
	public List<User> findOne(Long id) {
		List<User> data = new ArrayList<>();
		try {
			data = jdbcTemplate.query(UserQueries.USER_PROFILE_FETCH, new Object[] { id },
					new SqlDataMapper().new UserDetailsMapper());
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an exception while fetching the User By ID :  %s", e.getMessage()));
		}
		return data;
	}

	@Override
	public UserAuthentication findOneUserAuthentication(Long id) {
		UserAuthentication user = null;
		try {
			user = jdbcTemplate.query(UserQueries.GET_USER_AUTH_DETAILS, new Object[] { id },
					new SqlDataMapper().new UserAuthenticationMapper()).get(0);
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an exception while fetching the Users Auth Details :  %s",
					e.getMessage()));
		}
		return user;
	}

	@Override
	public User insertIntoUser(final User user) {
		Long id = (long) 0;
		try {
			KeyHolder keyHolder = KeyFactory.getkeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					String[] returnValColumn = new String[] { "id" };
					PreparedStatement statement = con.prepareStatement(UserQueries.SAVE_USER, returnValColumn);
					statement.setString(1, user.getName());
					statement.setString(2, user.getUsername());
					statement.setString(3, user.getPhone());
					statement.setString(4, user.getImagePath());
					return statement;
				}
			}, keyHolder);
			id = keyHolder.getKey().longValue();
			user.setId(id);
			MasterDataManager.getUserIdAndUserNameMap().put(id, user.getName());
		} catch (InvalidDataAccessApiUsageException e) {
			LOGGER.error(String.format("Encountered an Invalid Data Access Exception while creating a new User:  %s",
					e.getMessage()));
		} catch (DataAccessException e) {
			LOGGER.error(String.format("Encountered a Data Access Exception while creating a new User :  %s",
					e.getMessage()));
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered a Exception while creating a new User :  %s", e.getMessage()));
		}
		return user;
	}

	@Override
	public Long insertAnonymousUser(final User user) {
		Long id = (long) 0;
		String anonymous = "Anonymous";
		try {
			KeyHolder keyHolder = KeyFactory.getkeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					String[] returnValColumn = new String[] { "id" };
					PreparedStatement statement = con.prepareStatement(UserQueries.SAVE_ANONYMOUS_USER,
							returnValColumn);
					statement.setString(1, anonymous);
					statement.setString(2, user.getUsername());
					statement.setString(3, "");
					statement.setString(4, "");
					statement.setBoolean(5, true);
					return statement;
				}
			}, keyHolder);
			id = keyHolder.getKey().longValue();
		} catch (Exception e) {
			LOGGER.error(
					String.format("Encountered a Exception while creating a Anonymous User :  %s", e.getMessage()));
		}
		return id;
	}

	@Override
	public boolean customAuth(UserDto user) {
		int row = 0;
		VelocityContext context = new VelocityContext();
		try {
			String password = ProjectUtil.getRandomStringVal();
			if (!StringUtils.isBlank(password)) {
				LOGGER.info("Password : " + password);
			}
			String encodedPwd = OneWayHashing.encryptVal(password);
			user.setPassword(encodedPwd);
			row = insertIntoPassword(user.getId(), encodedPwd);
			if (row > 0) {
				context.put(JsonKey.MAIL_SUBJECT, "You're a Aurora-Desk User now!");
				context.put(JsonKey.MAIL_BODY, "You have been successfully added as the user to the system"
						+ " Please find your username and password");
				context.put(JsonKey.PSWRD, password);
				context.put(JsonKey.USER_NAME, user.getUsername());
				context.put(JsonKey.FIRST_NAME, user.getName());
				SendMail.sendMail(new String[] { user.getUsername() }, "User Added, Password Generated", context,
						"email_template.vm");
			}
			return true;
		} catch (Exception e) {
			LOGGER.error(String.format("Password insertion failed for user:  %s", user.getId()));
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return false;
	}

	private UserAuthentication save(final UserAuthentication user) {
		UserAuthentication user1 = new UserAuthentication();
		try {
			KeyHolder keyHolder = KeyFactory.getkeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					String[] returnValColumn = new String[] { "id" };
					PreparedStatement statement = con.prepareStatement(UserQueries.SAVE_USER_AUTHENTICATION,
							returnValColumn);
					statement.setLong(1, user.getUserId());
					statement.setString(2, user.getAuthToken());
					return statement;
				}
			}, keyHolder);
			Long id = keyHolder.getKey().longValue();
			user1 = this.findOneUserAuthentication(id);

		} catch (Exception e) {
			LOGGER.error(
					String.format("Encountered an exception while saving User Authentication :  %s", e.getMessage()));
		}
		return user1;
	}

	@Override
	public int getAuthId(Long userId) {
		int orgId = 0;
		orgId = superAdminDao.getOrganizationByUserId(userId);
		if (orgId > 0) {
			try {
				return jdbcTemplate.queryForObject(Sql.UserQueries.GET_AUTH_TYPE_ID, new Object[] { orgId },
						Integer.class);
			} catch (Exception e) {
				LOGGER.error(String.format("Encountered a Exception while getting an Authentication ID:  %s",
						e.getMessage()));
			}
		}
		return 0;
	}

	@Override
	public int insertIntoPassword(Long id, String password) {
		return jdbcTemplate.update(Sql.UserQueries.INSERT_PD, new Object[] { password, id.longValue() });
	}

	@Override
	public User update(final User user) {
		try {
			if (user.getImagePath() != null) {
				updateProfilePic(user.getId(), user.getImagePath());
			}
		} catch (Exception e) {
			LOGGER.error(String.format("error while updating profile pic  %s", e.getMessage()));
		}
		try {
			jdbcTemplate.update(UserQueries.UPDATE_USER, new Object[] { user.getName(), user.getUsername(),
					user.getPhone(), user.getIsActive(), user.getImagePath(), user.getId() });
			MasterDataManager.getUserIdAndUserName();
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an error while updating User Object :  %s", e.getMessage()));
		}
		return user;
	}

	private String updateProfilePic(Long id, String imagePath) {
		if (imagePath != null) {
			Long organization = MasterDataManager.getUserOrgMap().get(id);
			String profilePicPath = null;
			profilePicPath = S3FileManager.filePath(imagePath, "userprofile", id, organization);
			jdbcTemplate.update(Sql.INSERT_PROFILE_PICTURE, new Object[] { profilePicPath, id });
			return profilePicPath;
		}
		return null;
	}

	@Override
	public UserRoleMapper findAllRolesByUser(Long userId) {
		UserRoleMapper mapper = new SqlDataMapper().new UserRoleMapper();
		try {
			jdbcTemplate.query(UserQueries.GET_ROLES_FOR_USER, new Object[] { userId }, mapper);
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an exception while fetching the Roles for a User :  %s",
					e.getMessage()));
		}

		return mapper;
	}

	@Override
	public Boolean mapUserToRole(long userId, List<Role> roleList) {
		try {
			jdbcTemplate.update(UserQueries.REMOVE_USER_ROLE_MAP, new Object[] { userId });
		} catch (Exception ex) {
			LOGGER.error(String.format("Encountered an exception while removing the User Role mapping :  %s",
					ex.getMessage()));
		}

		try {
			jdbcTemplate.batchUpdate(UserQueries.MAP_USER_TO_ROLE, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(java.sql.PreparedStatement statement, int i) throws SQLException {
					Role role = roleList.get(i);
					statement.setLong(1, userId);
					statement.setLong(2, role.getId());
				}

				@Override
				public int getBatchSize() {
					return roleList.size();
				}
			});
			MasterDataManager.getUserRoleMap().clear();
			MasterDataManager.getRoleUserMap().clear();
			MasterDataManager.getAllUserRoles();
			return true;

		} catch (Exception ex) {
			LOGGER.error("Exception Occured while adding Roles to User :  %s", ex);
			return false;
		}

	}

	@Override
	public User updateUserImage(User profile) {
		try {
			KeyHolder keyHolder = KeyFactory.getkeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					String[] returnValColumn = new String[] { "id" };
					PreparedStatement statement = con.prepareStatement(UserQueries.UPDATE_USER_PROFILE_PROFILE_IMAGE,
							returnValColumn);
					statement.setString(1, profile.getImagePath());
					statement.setLong(2, profile.getId());
					return statement;
				}
			}, keyHolder);
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an error while updating User Profile image %s", e.getMessage()));
		}
		return profile;
	}

	@Override
	public Long checkUserNameExists(String username) {
		Long userId;
		try {
			userId = jdbcTemplate.queryForObject("SELECT \"user\".id FROM \"user\" WHERE username =?", new Object[] { username },
					Long.class);
		} catch (Exception e) {
			return 0l;
		}
		return userId;
	}

	@Override
	public UserDetailsMapper findListOfUsers(List<Long> userIdList) {
		UserDetailsMapper mapper = new SqlDataMapper().new UserDetailsMapper();
		String query = buildMyQuery(userIdList);
		LOGGER.info(String.format("Query to execute for fetching the User Profile : %s", query));
		try {
			jdbcTemplate.query(query, new Object[] {}, mapper);
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an exception while fetching the User By ID :  %s", e.getMessage()));
		}
		return mapper;
	}

	private String buildMyQuery(List<Long> userIdList) {
		StringBuilder builder = new StringBuilder(UserQueries.USER_PROFILE_FETCH);
		if (!userIdList.isEmpty()) {
			builder.append("(");
			for (int i = 0; i < userIdList.size(); i++) {
				if (i == 0 && i == userIdList.size() - 1) {
					builder.append(userIdList.get(i));
				} else if (i == userIdList.size() - 1) {
					builder.append(userIdList.get(i));
				} else {
					builder.append(userIdList.get(i) + ",");
				}
			}
			builder.append(")");
		}
		return builder.toString();
	}

	@Override
	public List<User> findAll(Long orgId) {
		List<User> user = null;
		try {
			if (orgId > 0) {
				user = jdbcTemplate.query(Sql.Common.GET_ALL_USERS_BY_ORG, new Object[] { orgId },
						new SqlDataMapper().new UserDetailsMapper());
			}
			setImageUrlFromImagePath(user);
			MasterDataManager.getAllUserRoles();
			for (int i = 0; i < user.size(); i++) {
				List<Long> roles = MasterDataManager.getUserRoleListMap().get(user.get(i).getId());
				List<Role> roleList = new ArrayList<>();
				for (int j = 0; j < roles.size(); j++) {
					roleList.add(MasterDataManager.getRoleMap().get(roles.get(j)));
				}
				user.get(i).setRoles(roleList);
			}
		} catch (Exception e) {
			LOGGER.error(
					String.format("Encountered an exception while fetching the User Profile :  %s", e.getMessage()));
		}
		return user;
	}

	private void setImageUrlFromImagePath(List<User> user) {
		for (int i = 0; i < user.size(); i++) {
			if (user.get(i).getImagePath() != null) {
				S3Config s3values = superAdminDao.getS3Access();
				String url = null;
				url = S3FileManager.getPreSignedURL(s3values, user.get(i).getImagePath());
				user.get(i).setImagePath(url);
			}
		}
	}

	@Override
	public Long getNumberOfUsers(Long role, Boolean active) {
		Long numberOfUsers = 0L;
		try {
			if (role != null) {
				numberOfUsers = jdbcTemplate.queryForObject(UserQueries.GET_USER_COUNT_FOR_ROLE, new Object[] { role },
						Long.class);
			} else if (active != null) {
				numberOfUsers = jdbcTemplate.queryForObject(UserQueries.GET_USER_COUNT_ON_ACTIVE_STATUS,
						new Object[] { active }, Long.class);
			} else {
				numberOfUsers = jdbcTemplate.queryForObject(UserQueries.GET_USER_COUNT, Long.class);
			}
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an Exception while fetching count of Users :  %s", e.getMessage()));
		}
		return numberOfUsers;
	}

	@Override
	public Long getNumberOfRoles() {
		Long numberOfRoles = 0L;
		try {
			numberOfRoles = jdbcTemplate.queryForObject(UserQueries.GET_ROLE_COUNT, Long.class);
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an Exception while fetching count of Roles :  %s", e.getMessage()));
		}
		return numberOfRoles;
	}

	@Override
	public Boolean invalidateToken(String authToken) {
		try {
			jdbcTemplate.update(UserQueries.INVALIDATE_TOKEN, new Object[] { authToken });
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an error while invalidating Auth Token :  %s", e.getMessage()));
			return false;
		}
		return true;
	}

	@Override
	public Boolean findUserByToken(String authToken) {
		Long countOfUsers = 0L;
		authToken = authToken.replace(TOKEN_PREFIX, "");
		try {
			countOfUsers = jdbcTemplate.queryForObject(UserQueries.SELECT_USER_BY_TOKEN, new Object[] { authToken },
					Long.class);
		} catch (Exception e) {
			LOGGER.error(
					String.format("Encountered an Exception while fetching User by auth token:  %s", e.getMessage()));
		}
		return (countOfUsers > 0);
	}

	@Override
	public Boolean checkUserTokenExists(Long userId, String deviceToken) {
		Long available = 0L;
		try {
			available = jdbcTemplate.queryForObject(UserQueries.CHECK_USER_DEVICE_TOKEN,
					new Object[] { userId, deviceToken }, Long.class);
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an Exception while fetching User Device by Device token:  %s",
					e.getMessage()));
		}
		return (available > 0);
	}

	@Override
	public Long fetchAuthTokenReference(String authToken) {
		authToken = authToken.split(" ")[1];
		Long authTokenRef = 0L;
		try {
			authTokenRef = jdbcTemplate.queryForObject(UserQueries.FETCH_AUTH_TOKEN_REF, new Object[] { authToken },
					Long.class);
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an Exception while fetching User Device by Device token:  %s",
					e.getMessage()));
		}
		return authTokenRef;
	}

	@Override
	public boolean changePassword(ChangePasswordDto changePasswordDto) {
		boolean resposne = false;
		if (isPasswordMatch(changePasswordDto.getUserId(), changePasswordDto.getOldPass())) {
			int count = jdbcTemplate.update(Sql.Common.UPDATE_PSWRD,
					new Object[] { OneWayHashing.encryptVal(changePasswordDto.getNewPass()),
							new Timestamp(new Date().getTime()), changePasswordDto.getUserId(),
							OneWayHashing.encryptVal(changePasswordDto.getOldPass()) });
			if (count > 0) {
				resposne = true;
			}
		}
		return resposne;
	}

	@Override
	public boolean isPasswordMatch(long userId, String password) {
		boolean response = false;
		String storedPassword = ""; //$NON-NLS-1$
		try {
			storedPassword = jdbcTemplate.queryForObject(Sql.Common.CHECK_OLD_PSWRD, new Object[] { userId },
					String.class);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		if (storedPassword.equals(OneWayHashing.encryptVal(password))) {
			response = true;
		}
		return response;
	}

	@Override
	public long forgotPassword(UserDto userDto) {
		long userId = 0;
		try {
			userId = jdbcTemplate.queryForObject(Sql.Common.CHECK_USER_BY_USERNAME,
					new Object[] { userDto.getUsername() }, Long.class);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}

		return userId;
	}

	@Override
	public boolean saveForgotPassword(long userId, String password) {
		boolean response = false;
		int count = 0;
		try {
			String encryptVal = OneWayHashing.encryptVal(password);
			count = jdbcTemplate.update(Sql.Common.SAVE_FORGOT_PSWRD,
					new Object[] { encryptVal, new Timestamp(new Date().getTime()), userId });
			LOGGER.info("Password : {}", encryptVal);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		if (count > 0) {
			response = true;
		}
		return response;
	}

	@Override
	public User getUserDetailsByEmail(String username) {
		User user = null;
		try {
			user = jdbcTemplate
					.query(Sql.Common.GET_USER_DETAIL_BY_EMAIL, new Object[] { username }, MasterDataManager.rowMapUser)
					.get(0);
			user.setOrgId(jdbcTemplate.queryForObject(Sql.Common.GET_ORG_ID_BY_USER_ID, new Object[] { user.getId() },
					Long.class));
		} catch (Exception e) {
			LOGGER.error(String.format("getUserDetailsByEmail method :  %s", e.getMessage())); //$NON-NLS-1$
		}
		return user;
	}

	@Override
	public LoginDto login(UserDto userDto) {
		final String token = jwtTokenUtil.generateToken(userDto);
		UserAuthentication userAuthentication = new UserAuthentication();
		userAuthentication.setUserId(userDto.getId());
		userAuthentication.setAuthToken(token);
		save(userAuthentication);
		LoginDto loginDto = new LoginDto();
		loginDto.setAuthToken(token);
		loginDto.setUserId(userDto.getId());
		List<Role> roles = userService.findAllRolesByUser(userDto.getId());
		loginDto.setRoles(roles);
		return loginDto;

	}

	@Override
	public CommonDataModel getAuthDomain(UserDto userDto) {
		CommonDataModel authDomain = null;
		try {
			authDomain = jdbcTemplate
					.query(createSqlForAuth(userDto.getOrgId()), MasterDataManager.rowMapCommonDataModel).get(0);
		} catch (DataAccessException e) {
			LOGGER.error(String.format("Encountered an Exception while getting Auth Domain :  %s", e.getMessage()));
		}
		return authDomain;
	}

	private final String createSqlForAuth(long orgId) {
		StringBuilder queryBuilder = new StringBuilder(
				UserQueries.QUERY1 + UserQueries.QUERY2 + orgId + UserQueries.QUERY3);
		return queryBuilder.toString();
	}

	@Override
	public boolean getFirstAdminsOfOrg(Long id) {
		try {
			Long value = null;
			value = jdbcTemplate.queryForObject(Sql.UserQueries.CHECK_FIRST_ADMIN, Long.class, new Object[] { id });
			return (value != null);
		} catch (Exception e) {
			LOGGER.error(String.format("Error while check first admin  %s", e.getMessage())); //$NON-NLS-1$
			return false;
		}
	}

	@Override
	public Boolean onBoardingCheck(Long orgId, Long userId) {
		try {
			Long check = jdbcTemplate.queryForObject(Sql.UserQueries.GET_MASTER_DATA_CHECK, new Object[] { orgId },
					Long.class);
			if (check > 0) {
				User user = getUserProfile(userId);
				return (user.getUsername() != null && user.getName() != null);
			}
		} catch (Exception e) {
			LOGGER.error(String.format("Error while check master Data  %s", e.getMessage())); //$NON-NLS-1$
		}
		return false;
	}

	public User getUserProfile(Long userId) {
		return jdbcTemplate
				.query(Sql.UserQueries.USER_PROFILE_FETCH, new Object[] { userId }, MasterDataManager.rowMapUser)
				.get(0);
	}

	@Override
	public List<OrgUserRoleDto> getAllOrgUsers() {
		List<OrgUserRoleDto> orgUserList = null;
		try {
			orgUserList = jdbcTemplate.query(Sql.UserQueries.GET_USER_ORG_MAP, new SqlDataMapper().new UserOrgMapper());
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an exception while fetching all roles %s", e.getMessage()));
		}
		return orgUserList;
	}

	@Override
	public List<OrgUserRoleDto> getAllUserRoles() {
		RolesUserMapper mapper = new SqlDataMapper().new RolesUserMapper();
		List<OrgUserRoleDto> userRoleList = new ArrayList<>();
		try {
			jdbcTemplate.query(Sql.UserQueries.GET_USER_ROLE_MAP, mapper);
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an exception while fetching all roles %s", e.getMessage()));
		}

		for (Map.Entry<Long, OrgUserRoleDto> entry : mapper.getUserMap().entrySet()) {
			userRoleList.add(entry.getValue());
		}
		return userRoleList;
	}

	@Override
	public List<HelpDeskApp> getAppIdAndHelpDeskId() {
		List<HelpDeskApp> helpdeskApp = null;
		try {
			helpdeskApp = jdbcTemplate.query(Sql.UserQueries.GET_APP_ID_HELPDESK_ID,
					new SqlDataMapper().new HelpDeskAppMapper());
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return helpdeskApp;
	}

	@Override
	public List<User> getUserIdAndUserName() {
		try {
			return jdbcTemplate.query(Sql.UserQueries.GET_USER_ID_AND_USER_NAME, new Object[] {},
					MasterDataManager.rowMapUser);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
			return new ArrayList<>();
		}
	}

	@Override
	public Access getReviewConfig(Long orgId) {
		Access exp = null;
		try {
			exp = jdbcTemplate.query(Sql.GET_CONFIG, new Object[] { orgId }, MasterDataManager.rowMapAccess).get(0);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return exp;
	}

	@Override
	public void getReviews() throws IOException {
		try {
			for (int i = 0; i < superAdminDao.getAllOrganization().size(); i++) {
				Long id = superAdminDao.getAllOrganization().get(i).getId();
				Access exp = getReviewConfig(id);
				AccessResponse accessResponse = new AccessResponse();
				if (!ProjectUtil.isObjectNull(exp)) {
					HttpHeaders headers = new HttpHeaders();
					RestTemplate restTemplate = new RestTemplate();
					headers.setContentType(MediaType.APPLICATION_JSON);
					Map<String, Object> map = new HashMap<>();
					map.put(Constants.GRANT_TYPE, Constants.REFRESH_TOKEN);
					map.put(Constants.CLIENT_ID, exp.getClientId());
					map.put(Constants.CLIENT_SECRET, exp.getClientSecret());
					map.put(Constants.REFRESH_TOKEN, exp.getRefreshToken());
					HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);
					ResponseEntity<String> response = restTemplate
							.postForEntity(Constants.HTTPS_ACCOUNTS_GOOGLE_COM_O_OAUTH2_TOKEN, request, String.class);
					if (!StringUtils.isBlank(response.getBody())) {
						List<Object> chainrSpecJSON = JsonUtils.classpathToList(Constants.ACCESSTOKENSPEC_JSON);
						Chainr chainr = Chainr.fromSpec(chainrSpecJSON);
						Object transformedOutput = chainr.transform(JsonUtils.jsonToObject(response.getBody()));
						Gson g = new Gson();
						accessResponse = g.fromJson(JsonUtils.toJsonString(transformedOutput), AccessResponse.class);
					}
					if (!StringUtils.isBlank(accessResponse.getAccessToken())) {
						List<String> distinctAppNames = applicationDao.getDistinctAppNames(id);
						for (int j = 0; j < distinctAppNames.size(); j++) {
							Rev reviews = new Rev();
							String appName = distinctAppNames.get(j);
							integrateReviews(accessResponse, restTemplate, reviews, appName);
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
	}

	private void integrateReviews(AccessResponse accessResponse, RestTemplate restTemplate, Rev reviews, String appName)
			throws ParseException, IOException {
		try {
			final String uri = Constants.HTTPS_WWW_GOOGLEAPIS_COM_ANDROIDPUBLISHER_V3_APPLICATIONS + appName
					+ Constants.REVIEWS2;
			HttpHeaders header = new HttpHeaders();
			header.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			header.set(Constants.HEADER_STRING, Constants.TOKEN_PREFIX + accessResponse.getAccessToken());
			HttpEntity<String> entity = new HttpEntity<>(Constants.PARAMETERS, header);
			ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
			if (!StringUtils.isBlank(result.getBody())) {
				Gson g = new Gson();
				reviews = g.fromJson(result.getBody(), Rev.class);
			}
			if (!reviews.getReviews().isEmpty()) {
				Ticket t;
				List<Ticket> mapper = new ArrayList<>();
				RestHighLevelClient client = connectToElasticSearch();
				for (int k = 0; k < reviews.getReviews().size(); k++) {
					List<Object> chainrSpecJSON = JsonUtils.classpathToList(Constants.REVIEWSPEC_JSON);
					Chainr chainr = Chainr.fromSpec(chainrSpecJSON);
					Gson gson = new Gson();
					String inputJSON = gson.toJson(reviews.getReviews().get(k));
					Object transformedOutput = chainr.transform(JsonUtils.jsonToObject(inputJSON));
					Gson g = new Gson();
					t = g.fromJson(JsonUtils.toJsonString(transformedOutput), Ticket.class);
					t.setAppName(appName);
					mapper.add(t);
				}
				BulkRequest req = new BulkRequest();
				for (int l = 0; l < mapper.size(); l++) {
					Ticket tkt = ticketDao.addTicket(mapper.get(l));
					if (tkt != null) {
						Map<String, Object> jsonMap = ticketsRequestInterceptor.createJsonMap(tkt);
						req.add(new IndexRequest(elasticsearchIndex, elasticsearchType, tkt.getId().toString())
								.source(jsonMap));
					}
				}
				client.bulk(req, RequestOptions.DEFAULT);
				client.close();
			}
		} catch (RestClientException e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
	}

	private RestHighLevelClient connectToElasticSearch() {
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(ELASTIC, EL_STIC123));

		HttpClientConfigCallback httpClientConfigCallback = new RestClientBuilder.HttpClientConfigCallback() {
			@Override
			public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
				return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
			}
		};
		return new RestHighLevelClient(RestClient.builder(new HttpHost(elasticsearchUrl))
				.setHttpClientConfigCallback(httpClientConfigCallback));
	}

}
