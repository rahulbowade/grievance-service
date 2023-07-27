package org.upsmf.grievance.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import org.upsmf.grievance.dao.TicketDao;
import org.upsmf.grievance.dto.TicketTypeDto;
import org.upsmf.grievance.model.ActivityLog;
import org.upsmf.grievance.model.Analytics;
import org.upsmf.grievance.model.TemplateVersion;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.TicketCount;
import org.upsmf.grievance.model.TicketElastic;
import org.upsmf.grievance.model.Updates;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.repository.ElasticSearchRepository;
import org.upsmf.grievance.service.TicketService;
import org.upsmf.grievance.util.Constants;

@Service(value = Constants.TICKET_SERVICE)
public class TicketServiceImpl implements TicketService {

	@Autowired
	private TicketDao ticketDao;

	private final String indexServiceHost;
	private final String userName;
	private final String password;
	private final String templatesIndexName;
	private final String templatesDocumentType;
	private static final String AUTHORIZATION = "Authorization";
	private static final String BASIC_AUTH = "Basic %s";
	public static final Logger LOGGER = LoggerFactory.getLogger(TicketServiceImpl.class);
	Gson gson = new Gson();

	public TicketServiceImpl(@Value("${services.esindexer.host}") String indexServiceHost,
			@Value("${services.esindexer.username}") String userName,
			@Value("${services.esindexer.password}") String password,
			@Value("${es.templates.index.name}") String templatesIndexName,
			@Value("${es.templates.document.type}") String templatesDocumentType, TicketDao ticketDao) {
		this.indexServiceHost = indexServiceHost;
		this.userName = userName;
		this.password = password;
		this.templatesIndexName = templatesIndexName;
		this.templatesDocumentType = templatesDocumentType;
		this.ticketDao = ticketDao;

	}

	@Autowired
	private ElasticSearchRepository elasticRepository;

	@Override
	public Ticket addTicket(Ticket ticket) {
		return ticketDao.addTicket(ticket);
	}

	@Override
	public Long getTemplatesVersion() {
		return ticketDao.getTemplatesVersion();
	}

	@Override
	public boolean configureTemplates(TemplateVersion templateVersion) {
		Long versionOfTemplate = new Date().getTime();
		if (versionOfTemplate > 0) {
			LOGGER.info("New Version of the Template : {}", versionOfTemplate);
		}
		if (null == templateVersion.getVersion()) {
			templateVersion.setVersion(versionOfTemplate);
		}
		String url = String.format("%s%s/%s/%s", this.indexServiceHost, templatesIndexName, templatesDocumentType,
				templateVersion.getVersion());
		HttpHeaders headers = getHttpHeaders();
		if (templateVersion.getVersion() > 0) {
			LOGGER.info("Template Version to be added to ES : {}", templateVersion.getVersion());
		}
		if (!StringUtils.isBlank(url)) {
			LOGGER.info("URL to invoke : {}", url);
		}
		Boolean saveStatus = elasticRepository.saveTemplate(templateVersion, url, headers);
		ticketDao.updateTemplateVersion(versionOfTemplate);
		return saveStatus;
	}

	@Override
	public TemplateVersion getTemplates() {
		MultiSearchResponse response = executeTemplatesElasticQuery();
		return templateResponseTranslator(response);
	}

	private MultiSearchResponse executeTemplatesElasticQuery() {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(1000);
		return elasticRepository.executeMultiSearchRequest(
				new SearchRequest(templatesIndexName).types(templatesDocumentType).source(searchSourceBuilder));
	}

	private TemplateVersion templateResponseTranslator(MultiSearchResponse templateResponse) {

		SearchResponse searchResponse = templateResponse.getResponses()[0].getResponse();
		List<TemplateVersion> templateVersionList = new ArrayList<>();
		if (searchResponse != null) {
			for (SearchHit hit : searchResponse.getHits()) {
				TemplateVersion templateVersion = gson.fromJson(hit.getSourceAsString(), TemplateVersion.class);
				templateVersionList.add(templateVersion);
			}
		}
		return Collections.max(templateVersionList, Comparator.comparing(template -> template.getVersion()));
	}

	/**
	 * A helper method to create the headers for Rest Connection with UserName and
	 * Password
	 *
	 * @return HttpHeaders
	 */
	private HttpHeaders getHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(AUTHORIZATION, getBase64Value(userName, password));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	/**
	 * Helper Method to create the Base64Value for headers
	 *
	 * @param userName
	 * @param password
	 * @return
	 */
	public String getBase64Value(String userName, String password) {
		String authString = String.format("%s:%s", userName, password);
		byte[] encodedAuthString = Base64.encodeBase64(authString.getBytes(StandardCharsets.US_ASCII));
		return String.format(BASIC_AUTH, new String(encodedAuthString));
	}

	@Override
	public boolean updateTicketBasic(MultipartFile file, Ticket ticket) {

		return ticketDao.updateTicketBasic(file, ticket);
	}

	@Override
	public Ticket getTicketsById(User user, Long id) {
		return ticketDao.getTicketsById(user.getId(), id);
	}

	@Override
	public List<Ticket> getAllTicketsByAppId(Long appId) {
		return ticketDao.getAllTicketsByAppId(appId);
	}

	@Override
	public Boolean updateNotesToTicket(Ticket ticket) {
		return ticketDao.updateNotesToTicket(ticket.getRequestedBy(), ticket.getId(), ticket.getNotes());
	}

	@Override
	public Boolean addUpdateUpdatesToTicket(Updates update) {
		return ticketDao.addUpdateUpdatesToTicket(update);
	}

	@Override
	public List<Updates> getUpdatesForTicket(Long id) {
		return ticketDao.getUpdatesForTicket(id);
	}

	@Override
	public boolean updateTicketType(TicketTypeDto ticketTypeDto, Long userId) {
		return ticketDao.updateTicketType(ticketTypeDto, userId);
	}

	@Override
	public boolean updateTicketStatus(Ticket ticket) {
		return ticketDao.updateTicketStatus(ticket);
	}

	@Override
	public boolean updateTicketChecklist(Ticket ticket) {
		return ticketDao.updateTicketChecklist(ticket);
	}

	@Override
	public List<ActivityLog> getActivityLogsPerTicket(Long id) {
		return ticketDao.getActivityLogsPerTicket(id);
	}

	@Override
	public List<ActivityLog> getActivityLogsPerUser(Long id) {

		return ticketDao.getActivityLogsPerUser(id);
	}

	@Override
	public List<Ticket> getAllTicketsByUserId(Long id) {
		return new ArrayList<>();
	}

	@Override
	public List<TicketElastic> getTicketDetailsByHelpdeskId(Ticket ticket) {
		return ticketDao.getTicketDetailsByHelpdeskId(ticket);
	}

	@Override
	public List<Ticket> keepOnlyCreatedAndCopiedToTickets(Long userId, List<Ticket> ticketList) {
		return ticketDao.keepOnlyCreatedAndCopiedToTickets(userId, ticketList);
	}

	@Override
	public boolean pinTicket(Ticket ticket) {
		return ticketDao.pinTicket(ticket);
	}

	@Override
	public TicketCount getNoOfTickets(Long userId) {
		return ticketDao.getNoOfTickets(userId);
	}

	@Override
	public Map<String, Long> getTicketsCountPerMonthPerUser(Analytics analytics) {
		return ticketDao.getTicketsCountPerMonthPerUser(analytics);
	}

	@Override
	public boolean attachmentUpload(MultipartFile file, Ticket ticket) {
		return ticketDao.attachmentUpload(file, ticket);
	}

	@Override
	public List<Ticket> getFeedBacksFromAuroraSdk() {
		return ticketDao.getFeedBacksFromAuroraSdk();
	}

	@Override
	public boolean sendRepliesToReviews(Updates updates) {
		return ticketDao.sendRepliesToReviews(updates);
	}

}
