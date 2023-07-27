package org.upsmf.grievance.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import org.upsmf.grievance.dao.HelpdeskDao;
import org.upsmf.grievance.dao.TagDao;
import org.upsmf.grievance.executor.MasterDataManager;
import org.upsmf.grievance.interceptor.TicketsRequestInterceptor;
import org.upsmf.grievance.model.Tags;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.util.Constants;
import org.upsmf.grievance.util.Sql;

@Repository(Constants.TAG_DAO)
public class TagDaoImpl implements TagDao {

	private static final String ENCOUNTERED_AN_EXCEPTION_WHILE_FETCHING_ALL_TAGS_BY_ORG_S = "Encountered an Exception while fetching all tags by org :  %s";

	public static final Logger LOGGER = LoggerFactory.getLogger(TagDaoImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private HelpdeskDao helpdeskDao;

	@Autowired
	private TicketsRequestInterceptor ticketsRequestInterceptor;

	@Override
	public List<Tags> getAllTags(Long orgId) {
		try {
			return jdbcTemplate.query(Sql.Tags.GET_ALL_TAG_BY_ORGANISATION, new Object[] { orgId },
					MasterDataManager.rowMapTags);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_FETCHING_ALL_TAGS_BY_ORG_S, e.getMessage()));
		}
		return new ArrayList<>();
	}

	@Override
	public Tags getTagByName(String name, Long id) {
		Tags tag = null;
		try {
			tag = jdbcTemplate.queryForObject(Sql.Tags.GET_TAG_BY_NAME, new Object[] { name, id },
					MasterDataManager.rowMapTags);
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an Exception while fetching tag by name : %s", e.getMessage()));
		}
		return tag;
	}

	@Override
	public Long addTag(Long id, Tags tag) {
		KeyHolder keyHolderForTag = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				String[] returnCol = new String[] { Sql.Tags.TAG_ID };
				PreparedStatement preparedStatement = conn.prepareStatement(Sql.Tags.SAVE_TAG, returnCol);
				preparedStatement.setString(1, tag.getName().trim());
				preparedStatement.setLong(2, id);
				preparedStatement.setLong(3, MasterDataManager.getOrgForUser(id));
				return preparedStatement;
			}
		}, keyHolderForTag);
		return keyHolderForTag.getKey().longValue();
	}

	@Override
	public boolean addTicketTags(Long id, List<Tags> tags) {
		jdbcTemplate.update(Sql.Tags.DELETE_TICKET_TAGS, new Object[] { id });
		int[][] added;
		added = jdbcTemplate.batchUpdate(Sql.Tags.SAVE_TICKET_TAG, tags, tags.size(),
				new ParameterizedPreparedStatementSetter<Tags>() {
					@Override
					public void setValues(PreparedStatement ps, Tags tag) throws SQLException {
						ps.setLong(1, id);
						ps.setLong(2, tag.getId());

					}
				});
		if (added.length > 0) {
			Ticket ticket = new Ticket();
			ticket.setOperation("update");
			ticket.setId(id);
			ticket.setTags(tags);
			ticketsRequestInterceptor.addData(ticket);
			return true;
		}
		return false;
	}

	@Override
	public List<Tags> getAllTicketTags(Long id) {
		try {
			return jdbcTemplate.query(Sql.Tags.GET_ALL_TICKET_TAGS, new Object[] { id }, MasterDataManager.rowMapTags);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_FETCHING_ALL_TAGS_BY_ORG_S, e.getMessage()));
		}
		return new ArrayList<>();
	}

	@Override
	public List<Tags> getHelpdeskTags(Long id, Long userId) {
		List<Long> admins = helpdeskDao.getHelpdeskAdmins(id);
		List<User> user = helpdeskDao.getUsersForHelpeskId(id);
		List<Long> userIds = user.stream().map(User::getId).collect(Collectors.toList());
		if (admins.contains(userId) || userIds.contains(userId)) {
			try {
				return jdbcTemplate.query(Sql.Tags.GET_ALL_TAG_BY_HELPDESK, new Object[] { id },
						MasterDataManager.rowMapTags);
			} catch (Exception e) {
				LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_FETCHING_ALL_TAGS_BY_ORG_S, e.getMessage()));
			}
		} else {
			LOGGER.info("This user is not a part of this helpdesk:");
		}
		return new ArrayList<>();
	}

}
