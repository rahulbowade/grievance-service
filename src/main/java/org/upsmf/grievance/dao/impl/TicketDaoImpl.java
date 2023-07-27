package org.upsmf.grievance.dao.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.google.gson.Gson;
import org.upsmf.grievance.dao.HelpdeskDao;
import org.upsmf.grievance.dao.SuperAdminDao;
import org.upsmf.grievance.dao.TagDao;
import org.upsmf.grievance.dao.TicketDao;
import org.upsmf.grievance.dto.HelpdeskDto;
import org.upsmf.grievance.dto.HelpdeskTypeDto;
import org.upsmf.grievance.dto.HelpdeskWorkflowDto;
import org.upsmf.grievance.dto.TicketTypeDto;
import org.upsmf.grievance.dto.TicketWorkflowDto;
import org.upsmf.grievance.executor.MasterDataManager;
import org.upsmf.grievance.interceptor.TicketsRequestInterceptor;
import org.upsmf.grievance.model.Access;
import org.upsmf.grievance.model.AccessResponse;
import org.upsmf.grievance.model.ActivityLog;
import org.upsmf.grievance.model.Analytics;
import org.upsmf.grievance.model.ChecklistItem;
import org.upsmf.grievance.model.Helpdesk;
import org.upsmf.grievance.model.KeyFactory;
import org.upsmf.grievance.model.S3Config;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.TicketCount;
import org.upsmf.grievance.model.TicketElastic;
import org.upsmf.grievance.model.Updates;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.model.mapper.SqlDataMapper;
import org.upsmf.grievance.model.mapper.SqlDataMapper.HelpdeskRowRecordMapper;
import org.upsmf.grievance.model.mapper.SqlDataMapper.TicketWorkFlowMapperV2;
import org.upsmf.grievance.service.HelpdeskService;
import org.upsmf.grievance.service.UserService;
import org.upsmf.grievance.util.Constants;
import org.upsmf.grievance.util.DateUtil;
import org.upsmf.grievance.util.JsonKey;
import org.upsmf.grievance.util.ProjectUtil;
import org.upsmf.grievance.util.S3FileManager;
import org.upsmf.grievance.util.SendMail;
import org.upsmf.grievance.util.Sql;
import org.upsmf.grievance.util.Sql.Apps;

@Repository(Constants.TICKET_DAO)
public class TicketDaoImpl implements TicketDao {
	private static final String EL_STIC123 = "El@stic123";

	private static final String ELASTIC = "elastic";

	private static final String T = "T";

	private static final String COUNT = "count";

	private static final String CREATED_TIME = "createdTime";

	private static final String EVENT = "event";

	private static final String FEEDBACK_D1 = "feedback-d1";

	private static final String REPLY = ":reply";

	private static final String REVIEWS = "/reviews/";

	private static final String REPLY_TEXT = "replyText";

	private static final String ENCOUNTERED_AN_EXCEPTION_WHILE_UPDATING_TICKET_S = "Encountered an Exception while updating ticket :  %s";

	private static final String UPDATE = "update";

	private static final String ENCOUNTERED_AN_EXCEPTION_WHILE_DELETING_WORKFLOW_STAGES_FOR_A_HELPDESK_TYPE = "Encountered an Exception while deleting Workflow Stages for a Helpdesk Type : %s";

	private static final String ENCOUNTERED_AN_EXCEPTION_S = "Encountered an Exception :  %s";

	public static final Logger LOGGER = LoggerFactory.getLogger(TicketDaoImpl.class);

	private Boolean attachmentSource;
	@Value("${elk.data.up}")
	private boolean elkDataUp;

	@Value("${elasticsearch.url}")
	private String elasticsearchUrl;

	@Value("${elasticsearch.index}")
	private String elasticsearchIndex;

	@Value("${elasticsearch.type}")
	private String elasticsearchType;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private HelpdeskDao helpdeskDao;

	@Autowired
	private HelpdeskService helpdeskService;

	@Autowired
	private SuperAdminDao superAdminDao;

	@Autowired
	private UserService userService;

	@Autowired
	private TagDao tagDao;

	@Autowired
	private TicketsRequestInterceptor ticketsRequestInterceptor;

	public TicketDaoImpl(@Value("${image.source.attachment.aws}") Boolean attachmentSource, JdbcTemplate jdbcTemplate,
			HelpdeskDao helpdeskDao, HelpdeskService helpdeskService, SuperAdminDao superAdminDao) {
		this.attachmentSource = attachmentSource;
		this.jdbcTemplate = jdbcTemplate;
		this.helpdeskDao = helpdeskDao;
		this.helpdeskService = helpdeskService;
		this.superAdminDao = superAdminDao;
	}

	@Override
	public Ticket addTicket(Ticket ticket) {
		if (ProjectUtil.isObjectNull(intializeAddTicket(ticket))) {
			return null;
		}
		KeyHolder keyHolder = KeyFactory.getkeyHolder();
		try {
			if (ProjectUtil.isObjectNull(getApps(ticket))) {
				return null;
			}
			Long orgId = jdbcTemplate.queryForObject(Apps.GET_ORG_ID_FROM_APP_ID, new Object[] { ticket.getAppId() },
					Long.class);
			ticket.setOrgId(orgId);
			if (ticket.getHelpdeskId() == null) {
				Long helpdeskId = jdbcTemplate.queryForObject(Apps.GET_HELPDESK_ID_FROM_APP_ID,
						new Object[] { ticket.getAppId() }, Long.class);
				ticket.setHelpdeskId(helpdeskId);
			}
			if (sourceId(ticket)) {
				ticket.setSourceId(3L);
			}
			if (ticket.getHelpdeskId() != null) {
				Helpdesk helpdesk = jdbcTemplate.query(Sql.UserQueries.GET_HELPDESK_CHANNELS,
						new Object[] { ticket.getHelpdeskId() }, MasterDataManager.rowMapHelpdesk).get(0);
				boolean val = val(ticket, helpdesk);
				if (val) {
					return null;
				}
			}
			if (ticket.getRequestedBy() == null) {
				setUsername(ticket);
			}
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					String[] returnValColumn = new String[] { "id" };
					PreparedStatement statement = connection.prepareStatement(Sql.Ticket.ADD_TICKET, returnValColumn);
					ticket.setCreatedTime(DateUtil.getFormattedDateInUTC(new Date()));
					ticket.setUpdatedTime(DateUtil.getFormattedDateInUTC(new Date()));
					ticket.setCreatedTimeTS(new Date().getTime());
					ticket.setUpdatedTimeTS(new Date().getTime());
					statement.setString(1, ticket.getCreatedTime());
					if (!StringUtils.isBlank(String.valueOf(ticket.getRate()))) {
						statement.setLong(2, ticket.getRate());
					} else {
						statement.setLong(2, 0L);
					}
					if (!StringUtils.isBlank(String.valueOf(ticket.getMaxRating()))) {
						statement.setLong(3, ticket.getMaxRating());
					} else {
						statement.setLong(3, 0L);
					}
					if (!StringUtils.isBlank(ticket.getPriority())) {
						statement.setString(4, ticket.getPriority());
					} else {
						statement.setString(4, "p3");
					}
					if (ticket.getRequestedBy() != null) {
						statement.setLong(5, ticket.getRequestedBy());
					}
					if (ticket.getDescription() != null) {
						statement.setString(6, ticket.getDescription());
					} else if (ticket.getFeedback() != null) {
						ticket.setDescription(ticket.getFeedback());
						statement.setString(6, ticket.getFeedback());
					} else {
						statement.setString(6, "");
					}
					if (ticket.getType() != null) {
						statement.setLong(7, ticket.getType());
					} else {
						Long id = jdbcTemplate.queryForObject(Sql.Ticket.GET_DEFAULT_TICKET_TYPE,
								new Object[] { ticket.getHelpdeskId() }, Long.class);
						ticket.setType(id);
						if (id > 0) {
							statement.setLong(7, id);
						}
					}
					statement.setString(8, ticket.getUpdatedTime());
					statement.setBoolean(9, false);
					return statement;
				}
			}, keyHolder);
			ticket.setId(keyHolder.getKey().longValue());
			if (ticket.getReviewId() != null) {
				jdbcTemplate.update(Sql.Ticket.UPDATE_TICKET_REVIEW_ID,
						new Object[] { ticket.getReviewId(), ticket.getId() });
				boolean v = value(ticket);
				if (v) {
					jdbcTemplate.update(Sql.Ticket.ADD_UPDATES,
							new Object[] { ticket.getDeveloperComment(), ticket.getRequestedBy(), ticket.getId(),
									convertFromTimestampToUTC(ticket.getDeveloperTimestamp()) });
				}
			}
			ticket.setSourceId(ticket.getSourceId());
			mapTicketToHelpdesk(ticket);
			addCc(ticket);
			TicketTypeDto ticketTypeDto = new TicketTypeDto(ticket);
			String value = addDefaultWorkflowForTicketType(ticketTypeDto);
			Boolean value1 = addChecklistForTicketType(ticketTypeDto);
			ticket.setActive(true);
			ticket.setOperation("save");
			ticket.setStatus(value);
			if (ticket.getSourceId().equals(3L)) {
				sendTicketEmail(ticket);
				ticketsRequestInterceptor.addData(ticket);
			}
			if (!value1) {
				return null;
			}
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
			return null;
		}
		addTicketActivityLog(ticket.getId(), "New ticket has been created", ticket.getRequestedBy());
		return ticket;
	}

	public void setUsername(Ticket ticket) {
		User user = new User();
		if (!StringUtils.isBlank(ticket.getUserName())) {
			user.setUsername(ticket.getUserName());
		} else {
			user.setUsername("anonymous" + genText() + "@mail.com");
		}
		user.setOrgId(ticket.getOrgId());
		ticket.setRequestedBy(userService.saveAnonymousUser(user));
	}

	public boolean sourceId(Ticket ticket) {
		return ticket.getSourceId() == null || ticket.getSourceId().toString().isEmpty();
	}

	public boolean value(Ticket ticket) {
		return ticket.getDeveloperComment() != null && ticket.getDeveloperTimestamp() != null;
	}

	public boolean val(Ticket ticket, Helpdesk helpdesk) {
		return !((helpdesk.getDirect() && ticket.getSourceId() == 3L)
				|| (helpdesk.getPlaystore() && ticket.getSourceId() == 4L)
				|| (helpdesk.getAppstore() && ticket.getSourceId() == 5L)
				|| (helpdesk.getAurora_sdk() && ticket.getSourceId() == 1L));
	}

	private Ticket getApps(Ticket ticket) {
		if (ticket.getAppId() == null && ticket.getAppName() != null) {
			Long id = jdbcTemplate.queryForObject(Apps.CHECK_IF_APP_NAME_EXISTS, new Object[] { ticket.getAppName() },
					Long.class);
			if (id > 0) {
				id = jdbcTemplate.queryForObject(Apps.GET_APP_ID_APP_KEY, new Object[] { ticket.getAppName() },
						Long.class);
				ticket.setAppId(id);
			} else {
				LOGGER.info("There is no app Url in the system as sent in the feedback, therefore no ticket is created "
						+ ticket.getAppName());
				return null;
			}
		} else if (ticket.getAppId() == null && ticket.getAppKey() != null) {
			Long id = jdbcTemplate.queryForObject(Apps.CHECK_GET_APP_ID_FROM_APP_KEY,
					new Object[] { ticket.getAppName() }, Long.class);
			if (id != 0) {
				id = jdbcTemplate.queryForObject(Apps.GET_APP_ID_FROM_APP_KEY, new Object[] { ticket.getAppKey() },
						Long.class);
				ticket.setAppId(id);
			} else {
				LOGGER.info(
						"There is no app related to the app key sent in the feedback, therefore no ticket is created "
								+ ticket.getAppKey());
				return null;
			}
		}
		return ticket;
	}

	private Ticket intializeAddTicket(Ticket ticket) {
		if (!StringUtils.isBlank(ticket.getFeedback()) && !StringUtils.isBlank(ticket.getUserName())) {
			if (jdbcTemplate.queryForObject(Apps.CHECK_IF_TICKET_EXIST,
					new Object[] { ticket.getFeedback(), ticket.getUserName() }, Long.class) != 0) {
				if (ticket.getSourceId() == 4L && ticket.getReviewId() != null) {
					if (jdbcTemplate.queryForObject(Apps.CHECK_IF_TICKET_EXISTS,
							new Object[] { ticket.getReviewId(), ticket.getUserName() }, Long.class) != 0) {
						if (value(ticket)
								&& jdbcTemplate.queryForObject(Apps.CHECK_IF_UPDATE_EXISTS,
										new Object[] { ticket.getDeveloperComment(),
												convertFromTimestampToUTC(ticket.getDeveloperTimestamp()) },
										Long.class) == 0) {
							jdbcTemplate.update(Sql.Ticket.ADD_UPDATES,
									new Object[] { ticket.getDeveloperComment(), ticket.getRequestedBy(),
											ticket.getId(),
											convertFromTimestampToUTC(ticket.getDeveloperTimestamp()) });
						}
						return null;
					} else {
						return null;
					}
				}
			} else if (ticket.getSourceId() == 4L && ticket.getReviewId() != null
					&& jdbcTemplate.queryForObject(Apps.CHECK_IF_TICKET_EXISTS,
							new Object[] { ticket.getReviewId(), ticket.getUserName() }, Long.class) != 0) {
				if (ticket.getUserTimestamp() != null && jdbcTemplate.queryForObject(Apps.CHECK_IF_UPDATE_EXISTS,
						new Object[] { ticket.getFeedback(), convertFromTimestampToUTC(ticket.getUserTimestamp()) },
						Long.class) == 0) {
					jdbcTemplate.update(Sql.Ticket.ADD_UPDATES,
							new Object[] { ticket.getFeedback(), ticket.getUserName(), ticket.getId(),
									convertFromTimestampToUTC(ticket.getUserTimestamp()) });
				}
				if (value(ticket)
						&& jdbcTemplate
								.queryForObject(Apps.CHECK_IF_UPDATE_EXISTS,
										new Object[] { ticket.getDeveloperComment(),
												convertFromTimestampToUTC(ticket.getDeveloperTimestamp()) },
										Long.class) == 0) {
					jdbcTemplate.update(Sql.Ticket.ADD_UPDATES,
							new Object[] { ticket.getDeveloperComment(), ticket.getRequestedBy(), ticket.getId(),
									convertFromTimestampToUTC(ticket.getDeveloperTimestamp()) });
				}
				return null;
			}
		}
		return ticket;
	}

	public String genText() {
		String randomText = "abcdefghijklmnopqrstuvwxyz123456789";
		int length = 5;
		return RandomStringUtils.random(length, randomText);
	}

	private void sendTicketEmail(Ticket ticket) {
		try {
			User user = superAdminDao.userDetailsByUserId(ticket.getRequestedBy());
			user.setOrgId(MasterDataManager.getUserOrgMap().get(ticket.getRequestedBy()));
			String email = user.getUsername();
			Map<String, String> keyValue = new HashMap<>();
			keyValue.put(JsonKey.FIRST_NAME, user.getName());
			keyValue.put(JsonKey.ID, ticket.getId().toString());
			keyValue.put(JsonKey.HELPDESKID, ticket.getHelpdeskId().toString());
			keyValue.put(JsonKey.HELPDESKNAME,
					MasterDataManager.getHelpdeskIdHelpdeskObjectMapping().get(ticket.getHelpdeskId()).getName());
			String[] emails = email.split(",");
			SendMail.sendMail(keyValue, emails, Constants.TICKETCREATION, "new-ticket-createdby-aurora.vm");
		} catch (ResourceNotFoundException e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
	}

	private void mapTicketToHelpdesk(Ticket ticket) {
		if (ticket.getAppId() != null) {
			jdbcTemplate.update(Sql.Ticket.ADD_TICKET_TO_HELPDESK,
					new Object[] { ticket.getId(), ticket.getSourceId(), ticket.getHelpdeskId(), ticket.getAppId() });
		}

	}

	@Override
	public boolean attachmentUpload(MultipartFile file, Ticket ticket) {
		List<Long> ticketIds = jdbcTemplate.queryForList(Sql.Ticket.GET_CREATED_AND_COPIED_TO_TICKET_IDS,
				new Object[] { ticket.getUserId(), ticket.getUserId() }, Long.class);
		Long helpdeskId = jdbcTemplate.queryForObject(Sql.Ticket.GET_HELPDESK_ID_FOR_TICKET,
				new Object[] { ticket.getId() }, Long.class);
		List<User> admins = new ArrayList<>();
		if (helpdeskId != null) {
			admins = helpdeskDao.getAdminForHelpeskId(helpdeskId);
		}
		List<Long> userIdList = admins.stream().map(User::getId).collect(Collectors.toList());
		if (ticketIds.contains(ticket.getId()) || userIdList.contains(ticket.getUserId())) {
			if (attachmentSource) {
				return fetchAttach(ticket);
			} else if (file != null) {
				return fetchAtt(file, ticket);
			}
		}
		return false;
	}

	public boolean fetchAttach(Ticket ticket) {
		try {
			if (ticket.getAttachmentUrl() != null) {
				jdbcTemplate.update(Sql.UserQueries.REMOVE_ALL_TICKET_ATTACHMENT, new Object[] { ticket.getId() });
				for (int i = 0; i < ticket.getAttachmentUrl().size(); i++) {
					String val = getImagePathValue(ticket, ticket.getAttachmentUrl().get(i));
					addAttachmentToTicket(ticket, val);
				}
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
			return false;
		}
	}

	public boolean fetchAtt(MultipartFile file, Ticket ticket) {
		try {
			String val = uploadFile(file, ticket.getId());
			Long attachmentId = getLastInsertId(val);
			if (attachmentId > 0) {
				jdbcTemplate.update(Sql.ADD_ATTACHMENT_TO_TICKET, new Object[] { ticket.getId(), attachmentId });
			}
			return true;
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
			return false;
		}
	}

	private void addAttachmentToTicket(Ticket ticket, String val) {
		try {
			Long attachmentId = getLastInsertId(val);
			if (attachmentId > 0) {
				jdbcTemplate.update(Sql.ADD_ATTACHMENT_TO_TICKET, new Object[] { ticket.getId(), attachmentId });
			}
		} catch (Exception e) {
			LOGGER.error("Erorr while uploading the attachment");
		}
	}

	public Long getLastInsertId(String val) {
		Long id = (long) 0;
		try {
			KeyHolder keyHolder = KeyFactory.getkeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					String[] returnValColumn = new String[] { "id" };
					PreparedStatement statement = con.prepareStatement(Sql.INSERT_ATTACHMENT, returnValColumn);
					statement.setString(1, val);
					return statement;
				}
			}, keyHolder);
			id = keyHolder.getKey().longValue();
		} catch (Exception e) {

			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return id;
	}

	public String getImagePathValue(Ticket ticket, String url) {
		String value = null;
		try {
			Long organization = MasterDataManager.getUserOrgMap().get(ticket.getUserId());
			value = S3FileManager.attachementfilePath(url, "attachment", ticket.getId(), organization);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return value;
	}

	public void addCc(Ticket ticket) {
		try {
			if (ticket.getCc() != null) {
				Long id = ticket.getId();
				Long helpdeskId = ticket.getHelpdeskId();
				List<Long> oldCC = getTicketCC(id);
				for (Long admin : ticket.getCc()) {
					if (oldCC.contains(admin)) {
						oldCC.remove(admin);
					}
				}
				for (int i = 0; i < oldCC.size(); i++) {
					User user = superAdminDao.userDetailsByUserId(oldCC.get(i));
					String email = user.getUsername();
					Map<String, String> keyValue = new HashMap<>();
					keyValue.put(JsonKey.FIRST_NAME, user.getName());
					keyValue.put(JsonKey.ID, id.toString());
					keyValue.put(JsonKey.HELPDESKID, helpdeskId.toString());
					keyValue.put(JsonKey.HELPDESKNAME,
							MasterDataManager.getHelpdeskIdHelpdeskObjectMapping().get(helpdeskId).getName());
					String[] emails = email.split(",");
					SendMail.sendMail(keyValue, emails, Constants.REMOVEDFROMCOPIEDTO,
							"remove-copied-to-ticket-aurora.vm");
				}
				jdbcTemplate.update(Sql.UserQueries.REMOVE_ALL_TICKET_CC, new Object[] { id });
				for (int i = 0; i < ticket.getCc().size(); i++) {
					jdbcTemplate.update(Sql.UserQueries.ADD_CC_TO_TICKET, new Object[] { id, ticket.getCc().get(i) });
					User user = superAdminDao.userDetailsByUserId(ticket.getCc().get(i));
					user.setOrgId(MasterDataManager.getUserOrgMap().get(ticket.getCc().get(i)));
					String email = user.getUsername();
					Map<String, String> keyValue = new HashMap<>();
					keyValue.put(JsonKey.FIRST_NAME, user.getName());
					keyValue.put(JsonKey.ID, id.toString());
					keyValue.put(JsonKey.HELPDESKID, helpdeskId.toString());
					keyValue.put(JsonKey.HELPDESKNAME,
							MasterDataManager.getHelpdeskIdHelpdeskObjectMapping().get(helpdeskId).getName());
					LOGGER.info(MasterDataManager.getHelpdeskIdHelpdeskObjectMapping().get(helpdeskId).getName());
					String[] emails = email.split(",");
					SendMail.sendMail(keyValue, emails, Constants.COPIEDTO, "copied-to-ticket-aurora.vm");
				}
			}
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
	}

	public List<Ticket> addTicketActivityLog(Long ticketId, String activity, Long changesBy) {
		List<Ticket> ticketList = null;
		try {
			jdbcTemplate.update(Sql.Ticket.ADD_ACTIVITY_LOG,
					new Object[] { activity, ticketId, DateUtil.getFormattedDateInUTC(new Date()), changesBy });
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return ticketList;
	}

	@Override
	public List<Ticket> getAllTicketsByUserId(Long id) {
		List<Ticket> ticketList = new ArrayList<>();
		try {
			ticketList = jdbcTemplate.query(Sql.Ticket.GET_ALL_TICKETS, new Object[] { id },
					MasterDataManager.rowMapTicket);
			setUserNamesAndWorkflow(ticketList);
			for (int i = 0; i < ticketList.size(); i++) {
				setTicketCCAndStatus(ticketList, i);
			}
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return ticketList;
	}

	@Override
	public List<Ticket> keepOnlyCreatedAndCopiedToTickets(Long userId, List<Ticket> ticketList) {
		List<Long> ticketIds = jdbcTemplate.queryForList(Sql.Ticket.GET_CREATED_AND_COPIED_TO_TICKET_IDS,
				new Object[] { userId, userId }, Long.class);
		Iterator<Ticket> itr = ticketList.iterator();
		while (itr.hasNext()) {
			Ticket ticket = itr.next();
			if (!ticketIds.contains(ticket.getId())) {
				itr.remove();
			}
		}
		return ticketList;
	}

	@Override
	public List<ChecklistItem> getChecklistItemsForTicket(Long ticketId) {
		List<ChecklistItem> checklistItems = new ArrayList<>();
		try {
			checklistItems = jdbcTemplate.query(Sql.Helpdesk.GET_CHECKLIST_FOR_TICKET, new Object[] { ticketId },
					new SqlDataMapper().new TicketsChecklistItemMapper());
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered exception in get Checklist for ticket %s", e.getMessage()));
		}
		return checklistItems;
	}

	@Override
	public String addDefaultWorkflowForTicketType(TicketTypeDto ticketTypeDto) {
		HelpdeskRowRecordMapper helpdesk = helpdeskDao.getHelpdeskForId(ticketTypeDto.getOrgId(),
				ticketTypeDto.getHelpdeskId());
		List<Long> workFlowIds = new ArrayList<>(helpdesk.getHelpdeskWorkflowMap().keySet());
		int[] values = null;
		String name = "";
		try {
			values = jdbcTemplate.batchUpdate(Sql.Ticket.INSERT_WORKFLOW_FOR_TICKET_TYPE,
					new BatchPreparedStatementSetter() {
						@Override
						public void setValues(java.sql.PreparedStatement statement, int i) throws SQLException {
							statement.setLong(1, workFlowIds.get(i));
							statement.setLong(2, ticketTypeDto.getId());
						}

						@Override
						public int getBatchSize() {
							return workFlowIds.size();
						}
					});
			name = updateTicketWorkFlowinDB(ticketTypeDto, workFlowIds, values, name);
		} catch (Exception ex) {
			LOGGER.error(String.format("Exception Occured while mapping Products to Order :  %s", ex.getMessage()));
		}
		return name;

	}

	private String updateTicketWorkFlowinDB(TicketTypeDto ticketTypeDto, List<Long> workFlowIds, int[] values,
			String name) {
		if (values != null && values.length > 0) {
			jdbcTemplate.update(Sql.Ticket.UPDATE_TICKET_WORKFLOW, new Object[] { true,
					DateUtil.getFormattedDateInUTC(new Date()), workFlowIds.get(0), ticketTypeDto.getId() });
			name = jdbcTemplate.queryForObject(Sql.Ticket.GET_WORKFLOW_NAME, new Object[] { workFlowIds.get(0) },
					String.class);
		}
		return name;
	}

	@Override
	public Boolean addChecklistForTicketType(TicketTypeDto ticketTypeDto) {
		List<ChecklistItem> helpdesk = helpdeskDao.getChecklistItemsForHelpdesk(ticketTypeDto.getHelpdeskId(),
				ticketTypeDto.getTypeId());
		int[] values = null;
		try {
			values = jdbcTemplate.batchUpdate(Sql.Ticket.INSERT_CHECKLIST_FOR_TICKET_TYPE,
					new BatchPreparedStatementSetter() {
						@Override
						public void setValues(java.sql.PreparedStatement statement, int i) throws SQLException {
							statement.setLong(1, ticketTypeDto.getId());
							statement.setLong(2, helpdesk.get(i).getId());
						}

						@Override
						public int getBatchSize() {
							return helpdesk.size();
						}
					});
			return (values != null && values.length > 0);
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an Exception while adding checklist type for a ticket : %s",
					e.getMessage()));
			return false;
		}
	}

	@Override
	public Boolean deleteWorkflowForTicketType(Long ticketId) {
		int status = 0;
		try {
			status = jdbcTemplate.update(Sql.Ticket.DELETE_WORKFLOW_FOR_TICKET_TYPE, new Object[] { ticketId });
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_DELETING_WORKFLOW_STAGES_FOR_A_HELPDESK_TYPE,
					e.getMessage()));
			return false;
		}
		return (status > 0);
	}

	@Override
	public Boolean deleteChecklistForTicketType(TicketTypeDto ticketTypeDto) {
		int status = 0;
		try {
			status = jdbcTemplate.update(Sql.Helpdesk.DELETE_CHECKLIST_FOR_TICKET,
					new Object[] { ticketTypeDto.getId() });
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_DELETING_WORKFLOW_STAGES_FOR_A_HELPDESK_TYPE,
					e.getMessage()));
			return false;
		}
		return (status > 0);
	}

	@Override
	public List<TicketWorkflowDto> getWorkflowForTicket(Long ticketId) {
		List<TicketWorkflowDto> ticketWorkFlow = new ArrayList<>();
		try {
			ticketWorkFlow = jdbcTemplate.query(Sql.Helpdesk.GET_WORKFLOW_FOR_TICKET, new Object[] { ticketId },
					new SqlDataMapper().new TicketWorkFlowMapper());
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered exception in getchecklist for ticket %s", e.getMessage()));
		}
		return ticketWorkFlow;
	}

	public Map<Long, List<TicketWorkflowDto>> getWorkflowForTicketList(List<Long> ticketIdList) {
		TicketWorkFlowMapperV2 mapper = new SqlDataMapper().new TicketWorkFlowMapperV2();
		if (!ticketIdList.isEmpty()) {
			String queryAppend = getIdQuery(ticketIdList);
			try {
				jdbcTemplate.query(Sql.Helpdesk.GET_WORKFLOW_FOR_TICKET_LIST + queryAppend, new Object[] {}, mapper);
			} catch (Exception e) {
				LOGGER.error(String.format("Encountered exception in getchecklist for ticket %s", e.getMessage()));
			}
		}
		return mapper.getTicketWorkflowMap();
	}

	private static String getIdQuery(final List<Long> idList) {
		final StringBuilder query = new StringBuilder("(");
		if (!idList.isEmpty()) {
			query.append(idList.get(0).toString());
			for (int i = 1; i < idList.size(); i++) {
				query.append(", " + idList.get(i));
			}
		}
		return query.append(")").toString();
	}

	@Override
	public Boolean updateNotesToTicket(Long requestedBy, Long ticketId, String notes) {
		try {
			jdbcTemplate.update(Sql.Ticket.UPDATE_NOTES_TO_TICKETS, new Object[] { notes, ticketId });
			addTicketActivityLog(ticketId, "Ticket Notes has been updated", requestedBy);
			return true;
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return false;
	}

	@Override
	public Boolean addUpdateUpdatesToTicket(Updates update) {

		try {
			if (update.getId() != null) {
				sendRepliesToReviews(update);
				jdbcTemplate.update(Sql.Ticket.UPDATE_UPDATES, new Object[] { update.getUpds(), update.isActive(),
						DateUtil.getFormattedDateInUTC(new Date()), update.getId() });
			} else {
				sendRepliesToReviews(update);
				List<Long> ccList = getTicketCC(update.getTicketId());
				Long requestedBy = jdbcTemplate.queryForObject(Apps.GET_REQUESTED_BY,
						new Object[] { update.getTicketId() }, Long.class);
				ccList.add(requestedBy);
				ccList.remove(update.getCreatedBy());
				jdbcTemplate.update(Sql.Ticket.ADD_UPDATES, new Object[] { update.getUpds(), update.getCreatedBy(),
						update.getTicketId(), DateUtil.getFormattedDateInUTC(new Date()) });
				for (int i = 0; i < ccList.size(); i++) {
					sendMailToCC(update, ccList, i);
				}
			}
			return true;
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return false;
	}

	private void sendMailToCC(Updates update, List<Long> ccList, int i) {
		try {
			User user = superAdminDao.userDetailsByUserId(ccList.get(i));
			String email = user.getUsername();
			Map<String, String> keyValue = new HashMap<>();
			keyValue.put(JsonKey.FIRST_NAME, user.getName());
			keyValue.put(JsonKey.ID, update.getTicketId().toString());
			keyValue.put(JsonKey.UPDATE, update.getUpds());
			String[] emails = email.split(",");
			SendMail.sendMail(keyValue, emails, Constants.UPDATES, "ticket-status-update-aurora.vm");
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
	}

	@Override
	public List<Updates> getUpdatesForTicket(Long id) {
		List<Updates> update = null;
		try {
			update = jdbcTemplate.query(Sql.Ticket.GET_UPDATES, new Object[] { id }, MasterDataManager.rowMapUpdate);
		} catch (Exception e) {

			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return update;
	}

	@Override
	public Long getTemplatesVersion() {
		Long version = 0l;
		try {
			version = jdbcTemplate.queryForObject(Sql.Ticket.GET_VERSION_FOR_TEMPLATES, Long.class);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_DELETING_WORKFLOW_STAGES_FOR_A_HELPDESK_TYPE,
					e.getMessage()));
		}
		return version;
	}

	@Override
	public boolean updateTemplateVersion(Long versionTimeStamp) {
		LOGGER.info("Updating the Template Version Timestamp");
		try {
			jdbcTemplate.update(Sql.Ticket.UPDATE_VERSION_TIMESTAMP, new Object[] { versionTimeStamp });
		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an Exception while updating the Version TimeStamp :  %s",
					e.getMessage()));
			return false;
		}
		return true;
	}

	@Override
	public Ticket getTicketsById(Long userId, Long id) {
		List<Ticket> ticketList = null;
		try {
			ticketList = jdbcTemplate.query(Sql.Ticket.GET_TICKET_BY_ID, new Object[] { id },
					MasterDataManager.rowMapTicket);
			for (int i = 0; i < ticketList.size(); i++) {
				List<Long> cc = getTicketCC(ticketList.get(i).getId());
				ticketList.get(i).setCc(cc);
				List<String> attachment = getTicketAttachment(ticketList.get(i).getId());
				ticketList.get(i).setAttachmentUrl(attachment);
			}

			if (!ticketList.isEmpty()) {
				List<ChecklistItem> checklist = getChecklistItemsForTicket(id);
				ticketList.get(0).setChecklist(checklist);
				ticketList = setUserNamesAndWorkflow(ticketList);
				List<TicketWorkflowDto> data = ticketList.get(0).getWorkflowStages();
				for (int i = 0; i < data.size(); i++) {
					if (data.get(i).getStatus()) {
						ticketList.get(0).setStatus(data.get(i).getName());
					}
				}
				List<String> att = fetchAtt(ticketList);
				ticketList.get(0).setAttachmentUrl(att);
				ticketList.get(0).setTags(tagDao.getAllTicketTags(id));
				return ticketList.get(0);
			}
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return null;
	}

	private List<String> fetchAtt(List<Ticket> ticketList) {
		List<String> att = null;
		try {
			att = new ArrayList<>();
			S3Config s3values = superAdminDao.getS3Access();
			if (ticketList.get(0).getAttachmentUrl() != null) {
				for (int i = 0; i < ticketList.get(0).getAttachmentUrl().size(); i++) {
					if (ticketList.get(0).getAttachmentUrl() != null) {
						fetchAttachment(ticketList, att, s3values, i);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return att;
	}

	private void fetchAttachment(List<Ticket> ticketList, List<String> att, S3Config s3values, int i) {
		try {
			String url = null;
			url = S3FileManager.getPreSignedURL(s3values, ticketList.get(0).getAttachmentUrl().get(i));
			att.add(url);
		} catch (Exception e) {
			fetchFile(ticketList, i, e);
		}
	}

	private void fetchFile(List<Ticket> ticketList, int i, Exception e) {
		try {
			byte[] file = getFile(ticketList.get(0).getAttachmentUrl().get(i));
			ticketList.get(0).setImg(file);
		} catch (Exception e1) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
	}

	public byte[] getFile(String attachmentUrl) {
		try {
			byte[] content = null;
			if (!StringUtils.isBlank(attachmentUrl)) {
				Path path = Paths.get(Constants.ATTACHMENT_FOLDER + attachmentUrl);
				content = readBytes(content, path);
			}
			return content;
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return new byte[0];
	}

	private byte[] readBytes(byte[] content, Path path) {
		try {
			content = Files.readAllBytes(path);
		} catch (final IOException e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return content;
	}

	private List<Ticket> setUserNamesAndWorkflow(List<Ticket> newTicketList) {
		List<Long> ticketIdList = newTicketList.stream().map(Ticket::getId).collect(Collectors.toList());
		Map<Long, List<TicketWorkflowDto>> ticketWorkflowMap = getWorkflowForTicketList(ticketIdList);
		for (Ticket ticket : newTicketList) {
			if (ticket.getRequestedBy() != null && StringUtils
					.isNotBlank(MasterDataManager.getUserIdAndUserNameMap().get(ticket.getRequestedBy()))) {
				ticket.setRequestedByName(MasterDataManager.getUserIdAndUserNameMap().get(ticket.getRequestedBy()));
			}
			if (ticketWorkflowMap.get(ticket.getId()) != null && !ticketWorkflowMap.get(ticket.getId()).isEmpty()) {
				ticket.setWorkflowStages(ticketWorkflowMap.get(ticket.getId()));
			}
		}
		return newTicketList;
	}

	@Override
	public TicketCount getNoOfTickets(Long userId) {
		try {
			TicketCount ticketCount = new TicketCount();
			List<Ticket> tickets = getAllTicketsByUserId(userId);
			Long pinnedTicketCount = 0L;
			Long closedTicketCount = 0L;
			if (!tickets.isEmpty()) {
				for (int i = 0; i < tickets.size(); i++) {
					if (getPinnedTicket(tickets, i)) {
						pinnedTicketCount = pinnedTicketCount + 1;
					}
					List<TicketWorkflowDto> workflow = getWorkflowForTicket(tickets.get(i).getId());
					for (int j = 0; j < workflow.size(); j++) {
						if (workflow.get(j).getStatus() && workflow.get(j).getName().equals("Closed")) {
							closedTicketCount = closedTicketCount + 1;
						}
					}

				}
			}
			ticketCount.setCreatedTicketCount(Long.valueOf(tickets.size()));
			ticketCount.setPinnedTicketCount(pinnedTicketCount);
			ticketCount.setClosedTicketCount(closedTicketCount);
			return ticketCount;
		} catch (Exception e) {

			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return null;

	}

	public boolean getPinnedTicket(List<Ticket> tickets, int i) {
		return !ProjectUtil.isObjectNull(tickets.get(i).getPinnedTicket()) && tickets.get(i).getPinnedTicket();
	}

	public List<Long> getTicketCC(Long id) {
		try {
			return jdbcTemplate.queryForList(Sql.UserQueries.GET_TICKET_CC, new Object[] { id }, Long.class);
		} catch (Exception e) {

			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return new ArrayList<>();

	}

	public List<String> getTicketAttachment(Long id) {
		try {
			return jdbcTemplate.queryForList(Sql.UserQueries.GET_TICKET_ATTACHMENT, new Object[] { id }, String.class);
		} catch (Exception e) {

			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return new ArrayList<>();

	}

	@Override
	public List<Ticket> getAllTicketsByAppId(Long appId) {
		List<Ticket> ticketList = null;
		try {
			ticketList = jdbcTemplate.query(Sql.Ticket.GET_ALL_TICKETS_BY_APP_ID, new Object[] { appId },
					MasterDataManager.rowMapTicket);
			for (int i = 0; i < ticketList.size(); i++) {
				setTicketCCAndStatus(ticketList, i);
			}
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return ticketList;
	}

	private void setTicketCCAndStatus(List<Ticket> ticketList, int i) {
		List<Long> cc = getTicketCC(ticketList.get(i).getId());
		ticketList.get(i).setCc(cc);
		List<TicketWorkflowDto> workFlow = getWorkflowForTicket(ticketList.get(i).getId());
		for (int j = 0; j < workFlow.size(); j++) {
			if (workFlow.get(j).getStatus()) {
				ticketList.get(i).setStatus(workFlow.get(j).getName());
			}
		}
	}

	@Override
	public boolean updateTicketBasic(MultipartFile file, Ticket ticket) {
		ticket.setUpdatedTime(DateUtil.getFormattedDateInUTC(new Date()));
		Long loggedInUserId = ticket.getUserId();
		int status = 0;
		Long id = ticket.getId();
		Ticket oldticket = getTicketsById(loggedInUserId, id);
		List<Long> admins = helpdeskDao.getHelpdeskAdmins(oldticket.getHelpdeskId());
		Ticket oldestTicket = oldticket;
		try {
			if (!admins.contains(loggedInUserId)) {
				List<Ticket> ticketList = new ArrayList<>();
				ticketList.add(oldticket);
				List<Ticket> t = keepOnlyCreatedAndCopiedToTickets(loggedInUserId, ticketList);
				if (!t.isEmpty()) {
					oldticket = t.get(0);
					oldestTicket = oldticket;
				}
			} else {
				status = jdbcTemplate.update(Sql.Ticket.UPDATE_TICKET, new Object[] { ticket.getPriority(),
						ticket.getNotes(), ticket.getActive(), ticket.getUpdatedTime(), id });
			}
			if (checkIfLoggedInUserIsTheCreatorOfTheTicket(loggedInUserId, oldticket, admins)) {
				ticket.setHelpdeskId(oldticket.getHelpdeskId());
				addCc(ticket);
				status = jdbcTemplate.update(Sql.Ticket.UPDATE_TICKET_DESCRIPTION,
						new Object[] { ticket.getDescription(), id });
			}
			Ticket newticket = getTicketsById(loggedInUserId, id);
			Ticket t = new Ticket();
			t.setId(id);
			if (newticket != null) {
				if (!oldestTicket.getDescription().equals(newticket.getDescription())) {
					addTicketActivityLog(id, "Ticket Description has been updated to " + newticket.getDescription(),
							loggedInUserId);
					t.setDescription(newticket.getDescription());
				}
				if (!oldestTicket.getPriority().equals(newticket.getPriority())) {
					addTicketActivityLog(id, "Ticket priority has been updated to " + newticket.getPriority(),
							loggedInUserId);
					t.setPriority(newticket.getPriority());
				}
				if (newticket.getNotes() != null && oldestTicket.getNotes() != null
						&& !oldestTicket.getNotes().equals(newticket.getNotes())) {
					addTicketActivityLog(id, "Ticket Notes has been updated to " + newticket.getNotes(),
							loggedInUserId);
				}
			} else {
				addTicketActivityLog(id, "Ticket Status has been updated to " + ticket.getActive(), loggedInUserId);
				t.setActive(ticket.getActive());
			}

			t.setCc(ticket.getCc());
			t.setOperation(UPDATE);
			t.setUpdatedTimeTS(new Date().getTime());
			t.setUpdatedTime(ticket.getUpdatedTime());
			ticketsRequestInterceptor.addData(t);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_UPDATING_TICKET_S, e.getMessage()));
			return false;
		}
		return (status > 0);
	}

	public boolean checkIfLoggedInUserIsTheCreatorOfTheTicket(Long loggedInUserId, Ticket oldticket,
			List<Long> admins) {
		return oldticket.getRequestedBy().equals(loggedInUserId) || admins.contains(loggedInUserId);
	}

	public String convertFromTimestampToUTC(Long timestamp) {
		try {
			Date d = new Date(timestamp * 1000);
			DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			return f.format(d);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return "";
	}

	public String uploadFile(MultipartFile file, long ticketId) {
		try {
			if (!new File(Constants.ATTACHMENT_FOLDER).exists()) {
				if (new File(Constants.ATTACHMENT_FOLDER).mkdir()) {
					LOGGER.info("Directory is created!");
				} else {
					LOGGER.error("Failed to create directory!");
				}
			} else {
				LOGGER.info("Folder exist");
			}
			User user = new User();
			byte[] bytes = file.getBytes();
			String newFileName = ticketId + "." + file.getOriginalFilename().split(".")[1];
			String val = "\\" + ticketId + "\\" + newFileName;
			Path path = Paths.get(Constants.ATTACHMENT_FOLDER + val);
			if (!StringUtils.isBlank(path.toString())) {
				LOGGER.info("Path before write: {}", path);
			}
			Path path1 = Files.write(path, bytes);
			if (!StringUtils.isBlank(path1.toString())) {
				LOGGER.info("Path after write : {}", path1);
			}
			Long attachmentId = getLastInsertId(val);
			if (attachmentId > 0) {
				jdbcTemplate.update(Sql.ADD_ATTACHMENT_TO_TICKET, new Object[] { ticketId, attachmentId });
			}
			jdbcTemplate.update(Sql.INSERT_ATTACHMENT, new Object[] { val, ticketId });
			user.setImagePath(val);
			return user.getImagePath();
		} catch (IOException e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return null;
	}

	@Override
	public boolean updateTicketType(TicketTypeDto ticketTypeDto, Long userId) {
		int status = 0;
		try {
			status = jdbcTemplate.update(Sql.Ticket.UPDATE_TICKET_TYPE,
					new Object[] { ticketTypeDto.getTypeId(), ticketTypeDto.getId() });
			deleteWorkflowForTicketType(ticketTypeDto.getId());
			String stat = addWorkFlowForTicketType(ticketTypeDto);
			deleteChecklistForTicketType(ticketTypeDto);
			addChecklistForTicketType(ticketTypeDto);
			addTicketActivityLog(ticketTypeDto.getId(),
					"Ticket Type has been updated to" + ticketTypeDto.getTypeId().toString(), userId);
			Ticket newticket = new Ticket();
			newticket.setType(ticketTypeDto.getTypeId());
			newticket.setOperation(UPDATE);
			newticket.setStatus(stat);
			newticket.setUpdatedTime(DateUtil.getFormattedDateInUTC(new Date()));
			newticket.setUpdatedTimeTS(new Date().getTime());
			ticketsRequestInterceptor.addData(newticket);

		} catch (Exception e) {
			LOGGER.error(String.format("Encountered an Exception while updating ticket type :  %s", e.getMessage()));
			return false;
		}
		return (status > 0);
	}

	private String addWorkFlowForTicketType(TicketTypeDto ticketTypeDto) {
		List<HelpdeskDto> helpdesk = helpdeskService.getHelpdeskById(ticketTypeDto.getOrgId(),
				ticketTypeDto.getHelpdeskId());
		List<HelpdeskWorkflowDto> workFlowStages = new ArrayList<>();
		List<HelpdeskTypeDto> helpdeskTypes = helpdesk.get(0).getTypes();
		for (int i = 0; i < helpdeskTypes.size(); i++) {
			if (helpdeskTypes.get(i).getId().equals(ticketTypeDto.getTypeId())) {
				workFlowStages = helpdeskTypes.get(i).getWorkflowStages();
			}
		}
		List<Long> workFlowIds = workFlowStages.stream().map(HelpdeskWorkflowDto::getId).collect(Collectors.toList());
		int[] values = null;
		String name = "";
		try {
			values = jdbcTemplate.batchUpdate(Sql.Ticket.INSERT_WORKFLOW_FOR_TICKET_TYPE,
					new BatchPreparedStatementSetter() {
						@Override
						public void setValues(java.sql.PreparedStatement statement, int i) throws SQLException {
							statement.setLong(1, workFlowIds.get(i));
							statement.setLong(2, ticketTypeDto.getId());
						}

						@Override
						public int getBatchSize() {
							return workFlowIds.size();
						}
					});
			if (values != null && values.length > 0) {
				jdbcTemplate.update(Sql.Ticket.UPDATE_TICKET_WORKFLOW,
						new Object[] { true, DateUtil.getFormattedDateInUTC(new Date()), workFlowStages.get(0).getId(),
								ticketTypeDto.getId() });
				name = jdbcTemplate.queryForObject(Sql.Ticket.GET_WORKFLOW_NAME, new Object[] { workFlowIds.get(0) },
						String.class);
			}
		} catch (Exception ex) {
			LOGGER.error(String.format("Exception Occured while mapping the new workflow :  %s", ex.getMessage()));
		}
		return name;
	}

	@Override
	public boolean updateTicketStatus(Ticket ticket) {
		int status = 0;
		List<TicketWorkflowDto> oldWorkFlow = getWorkflowForTicket(ticket.getId());
		String oldStatus = "";
		String newStatus = "";
		for (int i = 0; i < oldWorkFlow.size(); i++) {
			if (oldWorkFlow.get(i).getStatus()) {
				oldStatus = oldWorkFlow.get(i).getName();
			}
		}
		try {
			for (int i = 0; i < ticket.getWorkflowStages().size(); i++) {
				status = jdbcTemplate.update(Sql.Ticket.UPDATE_TICKET_WORKFLOW,
						new Object[] { ticket.getWorkflowStages().get(i).getStatus(),
								DateUtil.getFormattedDateInUTC(new Date()),
								ticket.getWorkflowStages().get(i).getWorkFlowId(), ticket.getId() });
			}
			List<TicketWorkflowDto> workFlow = getWorkflowForTicket(ticket.getId());
			for (int i = 0; i < workFlow.size(); i++) {
				if (workFlow.get(i).getStatus()) {
					newStatus = workFlow.get(i).getName();
				}
			}
			addTicketActivityLog(ticket.getId(),
					"Ticket Request Status has been updated from " + oldStatus + " to " + newStatus,
					ticket.getRequestedBy());
			Ticket newticket = new Ticket();
			newticket.setId(ticket.getId());
			newticket.setOperation(UPDATE);
			newticket.setStatus(newStatus);
			newticket.setUpdatedTime(DateUtil.getFormattedDateInUTC(new Date()));
			newticket.setUpdatedTimeTS(new Date().getTime());
			User user = superAdminDao.userDetailsByUserId(ticket.getRequestedBy());
			String email = user.getUsername();
			Map<String, String> keyValue = new HashMap<>();
			keyValue.put(JsonKey.FIRST_NAME, user.getName());
			keyValue.put(JsonKey.ID, ticket.getId().toString());
			keyValue.put(JsonKey.HELPDESKID, ticket.getHelpdeskId().toString());
			keyValue.put(JsonKey.HELPDESK_NAME,
					MasterDataManager.getHelpdeskIdHelpdeskObjectMapping().get(ticket.getHelpdeskId()).getName());
			keyValue.put(JsonKey.OLDSTATUS, oldStatus);
			keyValue.put(JsonKey.NEWSTATUS, newStatus);
			String[] emails = email.split(",");
			SendMail.sendMail(keyValue, emails, Constants.STATUS_CHANGE, "ticket-status-update-aurora.vm");
			ticketsRequestInterceptor.addData(newticket);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_UPDATING_TICKET_S, e.getMessage()));
			return false;
		}
		return (status > 0);
	}

	@Override
	public List<TicketElastic> getTicketDetailsByHelpdeskId(Ticket ticket) {
		RestHighLevelClient client = connectToElasticSearch();
		SearchResponse searchResponse = null;
		List<TicketElastic> mapper = new ArrayList<>();
		List<Long> admins = helpdeskDao.getHelpdeskAdmins(ticket.getHelpdeskId());
		List<User> user = helpdeskDao.getUsersForHelpeskId(ticket.getHelpdeskId());
		Long userId = ticket.getUserId();
		ticket.setCc(new ArrayList<>(Arrays.asList(userId)));
		List<Long> userIds = user.stream().map(User::getId).collect(Collectors.toList());
		if (userId > 0) {
			try {
				SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
				BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
				setBoolQuery(ticket, admins, userId, userIds, boolQuery);
				searchSourceBuilder.query(boolQuery);
				commonQuery(ticket, searchSourceBuilder);
				if (!searchSourceBuilder.toString().equals("{}")) {
					searchResponse = searchFromTicketElasticData(client, searchSourceBuilder);
					SearchHit[] hit = searchResponse.getHits().getHits();
					long total = searchResponse.getHits().getTotalHits();
					for (SearchHit hits : hit) {
						String sourceAsMap = hits.getSourceAsString();
						Gson g = new Gson();
						TicketElastic ticketElastic = g.fromJson(sourceAsMap, TicketElastic.class);
						ticketElastic.setTotal(total);
						mapper.add(ticketElastic);
					}
				}
			} catch (Exception e) {
				LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
			}
		}
		return mapper;
	}

	public void setBoolQuery(Ticket ticket, List<Long> admins, Long userId, List<Long> userIds,
			BoolQueryBuilder boolQuery) {
		if (admins.contains(userId)) {
			boolQuery.must(QueryBuilders.matchQuery("helpdeskId", ticket.getHelpdeskId()));
			boolQuery.must(QueryBuilders.matchQuery("active", "true"));
		} else if (userIds.contains(userId)) {
			boolQuery.must(QueryBuilders.matchQuery("helpdeskId", ticket.getHelpdeskId()));
			boolQuery.must(QueryBuilders.matchQuery("active", "true"));
			BoolQueryBuilder newBoolQuery = QueryBuilders.boolQuery();
			newBoolQuery.should(QueryBuilders.termsQuery(Sql.Ticket.CC, ticket.getCc()));
			newBoolQuery.should(QueryBuilders.matchQuery("requestedBy", ticket.getUserId()));
			boolQuery.must(newBoolQuery);
		}
		if (ticket.getSourceId() != null) {
			boolQuery.filter(QueryBuilders.termQuery(Sql.Ticket.SOURCE_ID, ticket.getSourceId()));
		}
		if (StringUtils.isNotBlank(ticket.getSearchKeyword())) {
			boolQuery
					.filter(QueryBuilders.wildcardQuery(Sql.Ticket.DESCRIPTION, "*" + ticket.getSearchKeyword() + "*"));
		}
		if (!ProjectUtil.isObjectListNullOrEmpty(ticket.getFilterStatus()) && !ticket.getFilterStatus().isEmpty()) {
			boolQuery.filter(QueryBuilders.termsQuery(Sql.Ticket.STATUS, ticket.getFilterStatus()));
		}
		if (!ProjectUtil.isObjectListNullOrEmpty(ticket.getSelectedTags()) && !ticket.getSelectedTags().isEmpty()) {
			boolQuery.filter(QueryBuilders.termsQuery(Sql.Ticket.TAGS, ticket.getSelectedTags()));
		}
	}

	public static <T, U> List<U> convertIntListToStringList(List<T> listOfInteger, Function<T, U> function) {
		return listOfInteger.stream().map(function).collect(Collectors.toList());
	}

	private void commonQuery(Ticket ticket, SearchSourceBuilder searchSourceBuilder) {
		if (!StringUtils.isBlank(ticket.getFilterCTUT())) {
			searchSourceBuilder.sort(ticket.getFilterCTUT(), org.elasticsearch.search.sort.SortOrder.DESC);
		} else {
			searchSourceBuilder.sort("createdTimeTS", org.elasticsearch.search.sort.SortOrder.DESC);
		}
		if (!StringUtils.isBlank(String.valueOf(ticket.getFrom())) && ticket.getFrom() >= 0) {
			searchSourceBuilder.from(ticket.getFrom());
		}
		if (!StringUtils.isBlank(String.valueOf(ticket.getSize())) && ticket.getSize() >= 0) {
			searchSourceBuilder.size(ticket.getSize());
		} else {
			searchSourceBuilder.size(25);
		}
		searchSourceBuilder.fetchSource(Constants.getIncludeFields(), Constants.getExcludeFields());
	}

	@Override
	public List<Ticket> getFeedBacksFromAuroraSdk() {
		RestHighLevelClient client = connectToElasticSearch();
		SearchResponse searchResponse = null;
		List<Ticket> mapper = new ArrayList<>();
		try {
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
					.query(QueryBuilders.boolQuery().must(QueryBuilders.matchAllQuery())).size(700);
			searchResponse = searchFromAuroraSdkData(client, searchSourceBuilder);
			SearchHit[] hit = searchResponse.getHits().getHits();
			long total = searchResponse.getHits().getTotalHits();
			for (SearchHit hits : hit) {
				String sourceAsMap = hits.getSourceAsString();
				Gson g = new Gson();
				Ticket t = g.fromJson(sourceAsMap, Ticket.class);
				mapper.add(t);
			}
			BulkRequest request = new BulkRequest();
			for (int i = 0; i < total; i++) {
				mapper.get(i).setSourceId(1L);
				Ticket tkt = addTicket(mapper.get(i));
				if (tkt != null) {
					Map<String, Object> jsonMap = ticketsRequestInterceptor.createJsonMap(tkt);
					request.add(new IndexRequest(elasticsearchIndex, elasticsearchType, tkt.getId().toString())
							.source(jsonMap));
				}
			}
			client.bulk(request);
			client.close();
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return mapper;
	}

	@Override
	public Map<String, Long> getTicketsCountPerMonthPerUser(Analytics analytics) {
		RestHighLevelClient client = connectToElasticSearch();
		SearchResponse searchResponse = null;
		Map<String, Long> monthCountMap = new HashMap<>();
		try {
			DateHistogramInterval dateHistogramInterval = DateHistogramInterval.MONTH;
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
					.query(QueryBuilders.boolQuery()
							.must(QueryBuilders.rangeQuery(CREATED_TIME).gte(analytics.getStartDate())
									.lte(analytics.getEndDate()))
							.filter(QueryBuilders.termQuery(Sql.Ticket.REQUESTEDBY, analytics.getUserId())))
					.aggregation(AggregationBuilders.dateHistogram(COUNT).field(CREATED_TIME)
							.dateHistogramInterval(dateHistogramInterval))
					.size(0);
			searchResponse = searchFromTicketElasticData(client, searchSourceBuilder);
			Histogram hist = searchResponse.getAggregations().get(COUNT);
			for (Histogram.Bucket bucket : hist.getBuckets()) {
				monthCountMap.put(bucket.getKeyAsString().split(T)[0], bucket.getDocCount());
			}
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return monthCountMap;
	}

	private RestHighLevelClient connectToElasticSearch() {
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(ELASTIC, EL_STIC123));
		HttpClientConfigCallback r = new RestClientBuilder.HttpClientConfigCallback() {
			@Override
			public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
				return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
			}
		};
		return new RestHighLevelClient(
				RestClient.builder(new HttpHost(elasticsearchUrl)).setHttpClientConfigCallback(r));
	}

	private SearchResponse searchFromTicketElasticData(RestHighLevelClient client,
			SearchSourceBuilder searchSourceBuilder) throws IOException {
		SearchRequest searchRequest = new SearchRequest(elasticsearchIndex).types(elasticsearchType)
				.source(searchSourceBuilder);
		SearchResponse searchResponse = client.search(searchRequest);
		client.close();
		return searchResponse;
	}

	private SearchResponse searchFromAuroraSdkData(RestHighLevelClient client, SearchSourceBuilder searchSourceBuilder)
			throws IOException {
		SearchRequest searchRequest = new SearchRequest(FEEDBACK_D1).types(EVENT).source(searchSourceBuilder);
		SearchResponse searchResponse = client.search(searchRequest);
		client.close();
		return searchResponse;
	}

	@Override
	public boolean updateTicketChecklist(Ticket ticket) {
		int status = 0;
		try {
			for (int i = 0; i < ticket.getChecklist().size(); i++) {
				status = jdbcTemplate.update(Sql.Ticket.UPDATE_TICKET_CHECKLIST,
						new Object[] { ticket.getChecklist().get(i).getChecked(), ticket.getChecklist().get(i).getId(),
								ticket.getId() });
			}
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_WHILE_UPDATING_TICKET_S, e.getMessage()));
			return false;
		}
		return (status > 0);
	}

	@Override
	public List<ActivityLog> getActivityLogsPerTicket(Long id) {
		List<ActivityLog> activityLogs = null;
		try {
			activityLogs = jdbcTemplate.query(Sql.Ticket.GET_ACTIVITY_LOGS, new Object[] { id },
					MasterDataManager.rowMapActivityLogs);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return activityLogs;
	}

	@Override
	public List<ActivityLog> getActivityLogsPerUser(Long id) {
		List<ActivityLog> activities = new ArrayList<>();
		try {
			activities = jdbcTemplate.query(Sql.Ticket.GET_ACTIVITY_LOGS_PER_USER, new Object[] { id, id },
					MasterDataManager.rowMapActivityLogs);
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return activities;
	}

	@Override
	public boolean pinTicket(Ticket ticket) {
		try {
			if (ticket.getPinnedTicket() != null) {
				jdbcTemplate.update(Sql.Ticket.UPDATE_TICKET_PIN,
						new Object[] { ticket.getPinnedTicket(), ticket.getId() });
				Ticket newticket = new Ticket();
				newticket.setId(ticket.getId());
				newticket.setPinnedTicket(ticket.getPinnedTicket());
				newticket.setOperation(UPDATE);
				newticket.setUpdatedTime(DateUtil.getFormattedDateInUTC(new Date()));
				newticket.setUpdatedTimeTS(new Date().getTime());
				ticketsRequestInterceptor.addData(newticket);
			}
			return true;
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return false;
	}

	@Override
	public boolean sendRepliesToReviews(Updates updates) {
		try {
			String reviewId = jdbcTemplate.queryForObject(Sql.Ticket.GET_REVIEW_ID,
					new Object[] { updates.getTicketId() }, String.class);
			if (reviewId != null) {
				Long id = jdbcTemplate.queryForObject(Sql.Ticket.GET_ORG_ID_FROM_TICKET_ID,
						new Object[] { updates.getTicketId() }, Long.class);
				String appName = jdbcTemplate.queryForObject(Sql.Ticket.GET_APP_URL_FROM_TICKET_ID,
						new Object[] { updates.getTicketId() }, String.class);
				if (checkIfIDAndAppNameIsNotNull(id, appName)) {
					RestTemplate restTemplate = new RestTemplate();
					Access exp = userService.getReviewConfig(id);
					AccessResponse accessResponse = new AccessResponse();
					accessResponse = transformTOJSONString(restTemplate, exp, accessResponse);
					final String uri = Constants.HTTPS_WWW_GOOGLEAPIS_COM_ANDROIDPUBLISHER_V3_APPLICATIONS + appName
							+ REVIEWS + reviewId + REPLY;
					HttpHeaders header = new HttpHeaders();
					header.setContentType(MediaType.APPLICATION_JSON);
					header.set(Constants.HEADER_STRING, Constants.TOKEN_PREFIX + accessResponse.getAccessToken());
					JSONObject request = new JSONObject();
					if (extractUpdates(updates)) {
						request.put(REPLY_TEXT, updates.getUpds());
					} else {
						return false;
					}
					HttpEntity<String> entity = new HttpEntity<>(request.toString(), header);
					ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
					if (matchStatus(result)) {
						return true;
					}
				}

			}
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return false;
	}

	public boolean checkIfIDAndAppNameIsNotNull(Long id, String appName) {
		return id != null && appName != null;
	}

	public AccessResponse transformTOJSONString(RestTemplate restTemplate, Access exp, AccessResponse accessResponse) {
		if (!ProjectUtil.isObjectNull(exp)) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			Map<String, Object> map = new HashMap<>();
			map.put(Constants.GRANT_TYPE, Constants.REFRESH_TOKEN);
			map.put(Constants.CLIENT_ID, exp.getClientId());
			map.put(Constants.CLIENT_SECRET, exp.getClientSecret());
			map.put(Constants.REFRESH_TOKEN, exp.getRefreshToken());
			HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);
			ResponseEntity<String> response = restTemplate
					.postForEntity(Constants.HTTPS_ACCOUNTS_GOOGLE_COM_O_OAUTH2_TOKEN, request, String.class);
			if (!StringUtils.isEmpty(response.getBody())) {
				List<Object> chainrSpecJSON = JsonUtils.classpathToList(Constants.ACCESSTOKENSPEC_JSON);
				Chainr chainr = Chainr.fromSpec(chainrSpecJSON);
				Object transformedOutput = chainr.transform(JsonUtils.jsonToObject(response.getBody()));
				Gson g = new Gson();
				accessResponse = g.fromJson(JsonUtils.toJsonString(transformedOutput), AccessResponse.class);
			}
		}
		return accessResponse;
	}

	public boolean extractUpdates(Updates updates) {
		return updates.getUpds() != null && !updates.getUpds().isEmpty();
	}

	public boolean matchStatus(ResponseEntity<String> result) {
		return result.getStatusCode() == HttpStatus.OK;
	}

}
