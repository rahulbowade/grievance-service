package org.upsmf.grievance.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.upsmf.grievance.dao.impl.SuperAdminDaoImpl;
import org.upsmf.grievance.dao.impl.UserDaoImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import org.upsmf.grievance.dto.OrgUserRoleDto;
import org.upsmf.grievance.dto.UserDto;
import org.upsmf.grievance.model.Action;
import org.upsmf.grievance.model.HelpDeskApp;
import org.upsmf.grievance.model.MasterData;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.model.UserAuthentication;
import org.upsmf.grievance.model.mapper.SqlDataMapper;
import org.upsmf.grievance.model.mapper.SqlDataMapper.UserDetailsMapper;
import org.upsmf.grievance.model.mapper.SqlDataMapper.UserRoleMapper;

@RunWith(MockitoJUnitRunner.class)
public class UserDaoImplTest {

	@Mock
	JdbcTemplate jdbcTemplate; 

	@Spy
	private SuperAdminDaoImpl superAdminDao;

	@InjectMocks
    UserDaoImpl userdao;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this); 
	}

	@SuppressWarnings("unchecked")
	@Test
	public void findAllActionsByRoleIdTest() {
		Action action = new Action();
		action.setId(1L);
		List<Action> actions = new ArrayList<>();
		actions.add(action);
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.any(Object[].class), Matchers.any(RowMapper.class)))
				.thenReturn(actions);
		List<Action> act = userdao.findAllActionsByRoleID(1);
		assertEquals(1, act.size());
	}

	@Test
	public void getAuthIdTest() {
		User user = new User();
		user.setId(1L);
		List<User> users = new ArrayList<>();
		users.add(user);
		Mockito.doReturn(2).when(superAdminDao).getOrganizationByUserId(1L);
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.any(Object[].class), eq(Integer.class)))
				.thenReturn(1);
		assertEquals(1, userdao.getAuthId(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void findOneTest() {
		User user = new User();
		user.setId(1L);
		user.setName("jay");
		List<User> users = new ArrayList<>();
		users.add(user);
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.any(Object[].class), Matchers.any(RowMapper.class)))
				.thenReturn(users);
		assertEquals(new ArrayList<>().getClass(), userdao.findOne(1L).getClass());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void findOneuserAuthenticationTest() {
		UserAuthentication user = new UserAuthentication();
		user.setAuthToken("abcd");
		List<UserAuthentication> users = new ArrayList<>();
		users.add(user);
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.any(Object[].class), Matchers.any(RowMapper.class)))
				.thenReturn(users);
		assertEquals("abcd", userdao.findOneUserAuthentication(1L).getAuthToken());
	}

	@Test
	public void insertIntoPasswordTest() {
		UserAuthentication user = new UserAuthentication();
		user.setAuthToken("abcd");
		List<UserAuthentication> users = new ArrayList<>();
		users.add(user);
		Mockito.when(jdbcTemplate.update(anyString(), Matchers.<Object[]>anyVararg())).thenReturn(5);
		assertEquals(5, userdao.insertIntoPassword(1L, "d"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void findAllRolesByUserTest() {
		UserRoleMapper mapper = new SqlDataMapper().new UserRoleMapper();
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.any(Object[].class), Matchers.any(RowMapper.class)))
				.thenReturn(null);
		assertEquals(mapper.getClass(), userdao.findAllRolesByUser(1L).getClass());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void findListOfUsersTest() {
		List<Long> users = Arrays.asList(1L, 2L);
		UserDetailsMapper mapper = new SqlDataMapper().new UserDetailsMapper();
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.any(Object[].class), Matchers.any(RowMapper.class)))
				.thenReturn(null);
		assertEquals(mapper.getClass(), userdao.findListOfUsers(users).getClass());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void findAllTest() {
		User user = new User();
		user.setId(1L);
		user.setName("jay");
		List<User> users = new ArrayList<>();
		users.add(user);
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.any(Object[].class), Matchers.any(RowMapper.class)))
				.thenReturn(users);
		assertEquals("jay", userdao.findAll(1L).get(0).getName());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getNumberOfUsersNTest() {
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.any(Object[].class), eq(Long.class)))
				.thenReturn(5L);
		assertEquals(5L, userdao.getNumberOfUsers(null, false).longValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getNumberOfUsersNNTest() {
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.any(Object[].class), eq(Long.class)))
				.thenReturn(5L);
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), eq(Long.class))).thenReturn(5L);
		assertEquals(5L, userdao.getNumberOfUsers(null, null).longValue());
	}

	@Test
	public void checkUserNameExistsTest() {
		UserAuthentication user = new UserAuthentication();
		user.setAuthToken("abcd");
		List<UserAuthentication> users = new ArrayList<>();
		users.add(user);
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.any(Object[].class), eq(Long.class)))
				.thenReturn(5L);
		assertEquals(5L, userdao.checkUserNameExists("d").longValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getNumberOfRolesTest() {
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), eq(Long.class))).thenReturn(5L);
		assertEquals(5L, userdao.getNumberOfRoles().longValue());
	}

	@Test
	public void invalidateTokenTest() {
		Mockito.when(jdbcTemplate.update(anyString(), Matchers.<Object[]>anyVararg())).thenReturn(5);
		assertEquals(true, userdao.invalidateToken("d"));
	}

	@Test
	public void findUserByTokenTest() {
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.any(Object[].class), eq(Long.class)))
				.thenReturn(5L);
		assertEquals(true, userdao.findUserByToken("d"));
	}

	@Test
	public void checkUserTokenExistsTest() {
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.<Object[]>anyVararg(), eq(Long.class)))
				.thenReturn(5L);
		assertEquals(true, userdao.checkUserTokenExists(1L, "d"));
	}

	@Test
	public void fetchAuthTokenReferenceTest() {
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.<Object[]>anyVararg(), eq(Long.class)))
				.thenReturn(5L);
		assertEquals(5L, userdao.fetchAuthTokenReference("d a").longValue());
	}

	@Test
	public void getFirstAdminsOfOrgTest() {
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), eq(Long.class), Matchers.<Object[]>anyVararg()))
				.thenReturn(5L);
		assertEquals(true, userdao.getFirstAdminsOfOrg(1L));
	}

	@Test
	public void getFirstAdminsOfOrgNTest() {
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), eq(Long.class), Matchers.<Object[]>anyVararg()))
				.thenReturn(null);
		assertEquals(false, userdao.getFirstAdminsOfOrg(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void onBoardingCheckTest() {
		List<MasterData> masterDataCheckList = new ArrayList<>();
		MasterData m = new MasterData();
		m.setField("u");
		m.setCount(5L);
		masterDataCheckList.add(m);
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(masterDataCheckList);
		assertEquals(false, userdao.onBoardingCheck(1L, 1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getUserProfileTest() {
		User user = new User();
		user.setId(1L);
		user.setName("jay");
		List<User> users = new ArrayList<>();
		users.add(user);
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(users);
		assertEquals(user.getId(), userdao.getUserProfile(1L).getId());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAllOrgUsersTest() {
		List<OrgUserRoleDto> orgUserList = new ArrayList<>();
		OrgUserRoleDto o = new OrgUserRoleDto();
		o.setId(5L);
		orgUserList.add(o);
		Mockito.when(jdbcTemplate.query(Matchers.anyString(), Matchers.any(RowMapper.class))).thenReturn(orgUserList);
		assertEquals(1, userdao.getAllOrgUsers().size());
	}

	/*
	 * @SuppressWarnings("unchecked")
	 *
	 * @Test public void getAllUserRoles() { List<OrgUserRoleDto> orgUserList = new
	 * ArrayList<>(); OrgUserRoleDto o = new OrgUserRoleDto(); o.setId(5L);
	 * orgUserList.add(o); Mockito.when(jdbcTemplate.query(Mockito.anyString(),
	 * Mockito.any(RowMapper.class))).thenReturn(orgUserList); assertEquals(1,
	 * userdao.getAllUserRoles().size()); }
	 */

	@SuppressWarnings("unchecked")
	@Test
	public void getAppIdAndHelpDeskId() {
		List<HelpDeskApp> helpdeskAppList = new ArrayList<>();
		HelpDeskApp o = new HelpDeskApp();
		o.setAppId(5L);
		helpdeskAppList.add(o);
		Mockito.when(jdbcTemplate.query(Matchers.anyString(), Matchers.any(RowMapper.class)))
				.thenReturn(helpdeskAppList);
		assertEquals(1, userdao.getAppIdAndHelpDeskId().size());
	}

	@Test
	public void isPasswordMatchTest() {
		Mockito.when(
				jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.<Object[]>anyVararg(), eq(String.class)))
				.thenReturn("abcd");
		assertEquals(false, userdao.isPasswordMatch(1L, "d"));
	}

	@Test
	public void forgotPasswordTest() {
		UserDto userDto = new UserDto();
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.<Object[]>anyVararg(), eq(Long.class)))
				.thenReturn(2L);
		assertEquals(2L, userdao.forgotPassword(userDto));
	}

	@Test
	public void saveForgotPasswordTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg())).thenReturn(2);
		assertEquals(true, userdao.saveForgotPassword(1, "d"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getUserDetailsByEmailTest() {
		User user = new User();
		user.setId(1L);
		user.setName("jay");
		List<User> users = new ArrayList<>();
		users.add(user);
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenReturn(users);
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.<Object[]>anyVararg(), eq(Long.class)))
				.thenReturn(2L);
		assertEquals(2L, userdao.getUserDetailsByEmail("d").getOrgId().longValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void findAllActionsByRoleIdNullTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.any(Object[].class), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(0, userdao.findAllActionsByRoleID(1).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAuthIdNullTest() {
		Mockito.doReturn(2).when(superAdminDao).getOrganizationByUserId(1L);
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.any(Object[].class), eq(Integer.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(0, userdao.getAuthId(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void findOneNullTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.any(Object[].class), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(new ArrayList<>().getClass(), userdao.findOne(1L).getClass());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void findOneuserAuthenticationNullTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.any(Object[].class), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertNull(userdao.findOneUserAuthentication(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void findAllRolesByUserNullTest() {
		UserRoleMapper mapper = new SqlDataMapper().new UserRoleMapper();
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.any(Object[].class), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(mapper.getClass(), userdao.findAllRolesByUser(1L).getClass());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void findListOfUsersNullTest() {
		List<Long> users = Arrays.asList(1L, 2L);
		UserDetailsMapper mapper = new SqlDataMapper().new UserDetailsMapper();
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.any(Object[].class), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(mapper.getClass(), userdao.findListOfUsers(users).getClass());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void findAllNullTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.any(Object[].class), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertNull(userdao.findAll(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getNumberOfUsersNullTest() {
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.any(Object[].class), eq(Long.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(0L, userdao.getNumberOfUsers(1L, false).longValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void checkUserNameExistsNullTest() {
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.any(Object[].class), eq(Long.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(0L, userdao.checkUserNameExists("d").longValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getNumberOfRolesNullTest() {
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), eq(Long.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(0L, userdao.getNumberOfRoles().longValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void invalidateTokenNullTest() {
		Mockito.when(jdbcTemplate.update(anyString(), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertEquals(false, userdao.invalidateToken("d"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void findUserByTokenNullTest() {
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.any(Object[].class), eq(Long.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(false, userdao.findUserByToken("d"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void checkUserTokenExistsNullTest() {
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.<Object[]>anyVararg(), eq(Long.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(false, userdao.checkUserTokenExists(1L, "d"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void fetchAuthTokenReferenceNullTest() {
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.<Object[]>anyVararg(), eq(Long.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(0L, userdao.fetchAuthTokenReference("d a").longValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getFirstAdminsOfOrgNullTest() {
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), eq(Long.class), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertEquals(false, userdao.getFirstAdminsOfOrg(1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void onBoardingCheckNullTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(false, userdao.onBoardingCheck(1L, 1L));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getAllOrgUsersNullTest() {
		Mockito.when(jdbcTemplate.query(Matchers.anyString(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertNull(userdao.getAllOrgUsers());
	}

	/*
	 * @SuppressWarnings("unchecked")
	 *
	 * @Test public void getAllUserRolesNullTest() {
	 * Mockito.when(jdbcTemplate.query(Mockito.anyString(),
	 * Mockito.any(RowMapper.class))) .thenThrow(NullPointerException.class);
	 * assertNull(userdao.getAllUserRoles()); }
	 */

	@SuppressWarnings("unchecked")
	@Test
	public void getAppIdAndHelpDeskIdNullTest() {
		Mockito.when(jdbcTemplate.query(Matchers.anyString(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		assertNull(userdao.getAppIdAndHelpDeskId());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void isPasswordMatchNullTest() {
		Mockito.when(
				jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.<Object[]>anyVararg(), eq(String.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(false, userdao.isPasswordMatch(1L, "d"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void forgotPasswordNullTest() {
		UserDto userDto = new UserDto();
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.<Object[]>anyVararg(), eq(Long.class)))
				.thenThrow(NullPointerException.class);
		assertEquals(0, userdao.forgotPassword(userDto));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void saveForgotPasswordNullTest() {
		Mockito.when(jdbcTemplate.update(Matchers.anyString(), Matchers.<Object[]>anyVararg()))
				.thenThrow(NullPointerException.class);
		assertEquals(false, userdao.saveForgotPassword(1, "d"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getUserDetailsByEmailNullTest() {
		Mockito.when(
				jdbcTemplate.query(Matchers.anyString(), Matchers.<Object[]>anyVararg(), Matchers.any(RowMapper.class)))
				.thenThrow(NullPointerException.class);
		Mockito.when(jdbcTemplate.queryForObject(Matchers.anyString(), Matchers.<Object[]>anyVararg(), eq(Long.class)))
				.thenThrow(NullPointerException.class);
		assertNull(userdao.getUserDetailsByEmail("d"));
	}

}
