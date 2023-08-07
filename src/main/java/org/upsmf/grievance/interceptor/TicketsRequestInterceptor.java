package org.upsmf.grievance.interceptor;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.upsmf.grievance.dao.impl.ApplicationDaoImpl;
import org.upsmf.grievance.model.Tags;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.util.Constants;
import org.upsmf.grievance.util.DateUtil;
import org.upsmf.grievance.util.ProjectUtil;

@Component
public class TicketsRequestInterceptor {
	private static final String ENCOUNTERED_AN_EXCEPTION_S = "Encountered an Exception : %s";
	private static final String CLASSNAME = TicketsRequestInterceptor.class.getName();
	private static BlockingQueue<Ticket> queue = new ArrayBlockingQueue<>(Constants.CAPACITY);
	public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationDaoImpl.class);

	@Value("${elk.data.up}")
	private boolean elkDataUp;

	@Value("${elasticsearch.url}")
	private String elasticsearchUrl;

	@Value("${elasticsearch.index}")
	private String elasticsearchIndex;

	@Value("${elasticsearch.type}")
	private String elasticsearchType;

	public void addData(Ticket data) {
		try {
			if (elkDataUp) {
				queue.add(data);
				processData();
			} else {
				LOGGER.info("ELK data insertion is not allowed %s");
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	public void processData() {
		Runnable task1 = new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					Ticket data = queue.take();
					if (data != null) {
						if (Constants.ELK_OPERATION.SAVE.toString().equalsIgnoreCase(data.getOperation())) {
							addTicketDataToElastic(data);
						} else if (Constants.ELK_OPERATION.UPDATE.toString().equalsIgnoreCase(data.getOperation())) {
							updateTicketDataInElastic(data);
						} else if (Constants.ELK_OPERATION.DELETE.toString().equalsIgnoreCase(data.getOperation())) {
							deleteTicketDataFromElastic(data);
						}
					} else {
						LOGGER.error(String.format("unable to get value %s", CLASSNAME));
					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
				}
			}
		};
		Thread thread1 = new Thread(task1);
		thread1.start();
	}

	private Map<String, Object> addTicketDataToElastic(Ticket value) throws ParseException, IOException {
		Map<String, Object> jsonMap = createJsonMap(value);
		writeDatatoElastic(jsonMap, String.valueOf(value.getId()));
		return jsonMap;
	}

	private Map<String, Object> updateTicketDataInElastic(Ticket value) throws ParseException, IOException {
		Map<String, Object> jsonMap = updateJsonMap(value);
		updateDataInElastic(jsonMap, String.valueOf(value.getId()));
		return jsonMap;
	}

	private Map<String, Object> updateJsonMap(Ticket value) throws ParseException {
		Map<String, Object> jsonMap = new HashMap<>();
		setValuesOnUpdate(value, jsonMap);
		setValuesOnUpdate2(value, jsonMap);
		if (!StringUtils.isBlank(value.getOsVersion())) {
			jsonMap.put(Constants.OS_VERSION, value.getOsVersion());
		}
		if (!StringUtils.isBlank(value.getOsType())) {
			jsonMap.put(Constants.OS_TYPE, value.getOsType());
		}
		if (!StringUtils.isBlank(value.getDeviceType())) {
			jsonMap.put(Constants.DEVICE_TYPE, value.getDeviceType());
		}
		if (!StringUtils.isBlank(value.getIp())) {
			jsonMap.put(Constants.IP, value.getIp());
		}
		if (!StringUtils.isBlank(value.getCountry())) {
			jsonMap.put(Constants.COUNTRY, value.getCountry());
		}
		if (!StringUtils.isBlank(value.getDeviceName())) {
			jsonMap.put(Constants.DEVICE_NAME, value.getDeviceName());
		}
		if (!StringUtils.isBlank(value.getDeviceManufacture())) {
			jsonMap.put(Constants.DEVICE_MANUFACTURE, value.getDeviceManufacture());
		}
		if (!StringUtils.isBlank(value.getDeviceLocale())) {
			jsonMap.put(Constants.DEVICE_LOCALE, value.getDeviceLocale());
		}
		if (!StringUtils.isBlank(value.getDeviceScreenResolution())) {
			jsonMap.put(Constants.DEVICE_SCREEN_RESOLUTION, value.getDeviceScreenResolution());
		}
		if (!StringUtils.isBlank(value.getUserEvent())) {
			jsonMap.put(Constants.USER_EVENT, value.getUserEvent());
		}
		if (!ProjectUtil.isObjectListNullOrEmpty(value.getTags())) {
			jsonMap.put(Constants.TAGS2, value.getTags().stream().map(Tags::getName).collect(Collectors.toList()));
		}
		if (!ProjectUtil.isObjectListNullOrEmpty(value.getCc())) {
			jsonMap.put(Constants.CC, value.getCc());
		}
		return jsonMap;
	}

	public void setValuesOnUpdate2(Ticket value, Map<String, Object> jsonMap) {
		if (value.getPinnedTicket() != null) {
			jsonMap.put(Constants.PINNED_TICKET, value.getPinnedTicket());
		}
		jsonMap.put(Constants.OPR, Constants.UPDATE);
		if (value.getAppId() != null) {
			jsonMap.put(Constants.APP_ID, value.getAppId());
		}
		if (value.getHelpdeskId() != null) {
			jsonMap.put(Constants.HELPDESK_ID, value.getHelpdeskId());
		}
		if (value.getSourceId() != null) {
			jsonMap.put(Constants.SOURCE_ID, value.getSourceId());
		}
		if (value.getType() != null) {
			jsonMap.put(Constants.TYPE_ID, value.getType());
		}
		if (!StringUtils.isBlank(value.getPriority())) {
			jsonMap.put(Constants.PRIORITY, value.getPriority());
		}
		if (!StringUtils.isBlank(value.getStatus())) {
			jsonMap.put(Constants.STATUS, value.getStatus());
		}
	}

	public void setValuesOnUpdate(Ticket value, Map<String, Object> jsonMap) throws ParseException {
		setValuesOnUpdate3(value, jsonMap);
		if (value.getUpdatedTime() != null) {
			jsonMap.put(Constants.U_T, DateUtil.getFormattedDateInString(value.getUpdatedTime()).split(" ")[1]);
		}
			jsonMap.put(Constants.CREATED_TIME_TS, value.getCreatedTime().getTime());

			jsonMap.put(Constants.UPDATED_TIME_TS, value.getUpdatedTime().getTime());
		if (value.getRequestedBy() != null) {
			jsonMap.put(Constants.REQUESTED_BY, value.getRequestedBy());
		}
		if (!StringUtils.isBlank(value.getAppVersion())) {
			jsonMap.put(Constants.APP_VERSION, value.getAppVersion());
		}
		if (!StringUtils.isBlank(value.getAppName())) {
			jsonMap.put(Constants.APP_NAME, value.getAppName());
		}
		if (value.getActive() != null) {
			jsonMap.put(Constants.ACTIVE, value.getActive());
		}
	}

	public void setValuesOnUpdate3(Ticket value, Map<String, Object> jsonMap) {
		if (!StringUtils.isBlank(value.getDescription())) {
			jsonMap.put(Constants.DESCRIPTION, value.getDescription());
		}
		if (!StringUtils.isBlank(value.getReviewId())) {
			jsonMap.put(Constants.REVIEW_ID, value.getReviewId());
		}
		if (!StringUtils.isBlank(String.valueOf(value.getRate())) && value.getRate() > 0) {
			jsonMap.put(Constants.RATE, value.getRate());
		}
		if (!StringUtils.isBlank(String.valueOf(value.getMaxRating())) && value.getMaxRating() > 0) {
			jsonMap.put(Constants.MAX_RATING, value.getMaxRating());
		}
		if (value.getCreatedTime() != null) {
			jsonMap.put(Constants.CREATED_TIME, DateUtil.getFormattedDateInString(value.getCreatedTime()).split(" ")[0]);
		}
		if (value.getUpdatedTime() != null) {
			jsonMap.put(Constants.UPDATED_TIME, DateUtil.getFormattedDateInString(value.getUpdatedTime()).split(" ")[0]);
		}
		if (value.getCreatedTime() != null) {
			jsonMap.put(Constants.C_T, DateUtil.getFormattedDateInString(value.getCreatedTime()).split(" ")[1]);
		}
	}

	public Map<String, Object> createJsonMap(Ticket value) throws ParseException {
		Map<String, Object> jsonMap = new HashMap<>();
		setValuesOnCreate(value, jsonMap);
		setValuesOnCreate2(value, jsonMap);
		setValuesOnCreate3(value, jsonMap);
		setValuesOnCreate4(value, jsonMap);
		setValuesOnCreate5(value, jsonMap);
		return jsonMap;
	}

	public void setValuesOnCreate5(Ticket value, Map<String, Object> jsonMap) {
		if (!StringUtils.isBlank(value.getDeviceScreenResolution())) {
			jsonMap.put(Constants.DEVICE_SCREEN_RESOLUTION, value.getDeviceScreenResolution());
		} else {
			jsonMap.put(Constants.DEVICE_SCREEN_RESOLUTION, null);
		}
		if (!StringUtils.isBlank(value.getUserEvent())) {
			jsonMap.put(Constants.USER_EVENT, value.getUserEvent());
		} else {
			jsonMap.put(Constants.USER_EVENT, null);
		}
		if (!ProjectUtil.isObjectListNullOrEmpty(value.getTags())) {
			jsonMap.put(Constants.TAGS2, value.getTags().stream().map(Tags::getName).collect(Collectors.toList()));
		} else {
			jsonMap.put(Constants.TAGS2, null);
		}
		if (!ProjectUtil.isObjectListNullOrEmpty(value.getCc())) {
			jsonMap.put(Constants.CC, value.getCc());
		} else {
			jsonMap.put(Constants.CC, null);
		}
	}

	public void setValuesOnCreate4(Ticket value, Map<String, Object> jsonMap) {
		setValuesOnCreate6(value, jsonMap);
		if (!StringUtils.isBlank(value.getCountry())) {
			jsonMap.put(Constants.COUNTRY, value.getCountry());
		} else {
			jsonMap.put(Constants.COUNTRY, null);
		}
		if (!StringUtils.isBlank(value.getDeviceName())) {
			jsonMap.put(Constants.DEVICE_NAME, value.getDeviceName());
		} else {
			jsonMap.put(Constants.DEVICE_NAME, null);
		}
		if (!StringUtils.isBlank(value.getDeviceManufacture())) {
			jsonMap.put(Constants.DEVICE_MANUFACTURE, value.getDeviceManufacture());
		} else {
			jsonMap.put(Constants.DEVICE_MANUFACTURE, null);
		}
		if (!StringUtils.isBlank(value.getDeviceLocale())) {
			jsonMap.put(Constants.DEVICE_LOCALE, value.getDeviceLocale());
		} else {
			jsonMap.put(Constants.DEVICE_LOCALE, null);
		}
	}

	public void setValuesOnCreate6(Ticket value, Map<String, Object> jsonMap) {
		if (!StringUtils.isBlank(value.getOsVersion())) {
			jsonMap.put(Constants.OS_VERSION, value.getOsVersion());
		} else {
			jsonMap.put(Constants.OS_VERSION, null);
		}
		if (!StringUtils.isBlank(value.getOsType())) {
			jsonMap.put(Constants.OS_TYPE, value.getOsType());
		} else {
			jsonMap.put(Constants.OS_TYPE, null);
		}
		if (!StringUtils.isBlank(value.getDeviceType())) {
			jsonMap.put(Constants.DEVICE_TYPE, value.getDeviceType());
		} else {
			jsonMap.put(Constants.DEVICE_TYPE, null);
		}
		jsonMap.put(Constants.SV, Constants.A_1_0);
		if (!StringUtils.isBlank(value.getIp())) {
			jsonMap.put(Constants.IP, value.getIp());
		} else {
			jsonMap.put(Constants.IP, null);
		}
	}

	public void setValuesOnCreate3(Ticket value, Map<String, Object> jsonMap) {
		setValuesOnCreate7(value, jsonMap);
		if (!StringUtils.isBlank(String.valueOf(value.getHelpdeskId()))) {
			jsonMap.put(Constants.HELPDESK_ID, value.getHelpdeskId());
		} else {
			jsonMap.put(Constants.HELPDESK_ID, null);
		}
		if (!StringUtils.isBlank(String.valueOf(value.getSourceId()))) {
			jsonMap.put(Constants.SOURCE_ID, value.getSourceId());
		} else {
			jsonMap.put(Constants.SOURCE_ID, null);
		}
		if (!StringUtils.isBlank(String.valueOf(value.getType()))) {
			jsonMap.put(Constants.TYPE_ID, value.getType());
		} else {
			jsonMap.put(Constants.TYPE_ID, null);
		}
		if (!StringUtils.isBlank(value.getPriority())) {
			jsonMap.put(Constants.PRIORITY, value.getPriority());
		} else {
			jsonMap.put(Constants.PRIORITY, null);
		}
		if (!StringUtils.isBlank(value.getStatus())) {
			jsonMap.put(Constants.STATUS, value.getStatus());
		} else {
			jsonMap.put(Constants.STATUS, null);
		}
	}

	public void setValuesOnCreate7(Ticket value, Map<String, Object> jsonMap) {
		if (!StringUtils.isBlank(value.getAppName())) {
			jsonMap.put(Constants.APP_NAME, value.getAppName());
		} else {
			jsonMap.put(Constants.APP_NAME, null);
		}
		jsonMap.put(Constants.ACTIVE, value.getActive());
		jsonMap.put(Constants.PINNED_TICKET, value.getPinnedTicket());
		if (value.getOperation().equalsIgnoreCase(Constants.SAVE)) {
			jsonMap.put(Constants.OPR, Constants.SAVE);
		} else {
			jsonMap.put(Constants.OPR, Constants.UPDATE);
		}
		if (!StringUtils.isBlank(String.valueOf(value.getAppId()))) {
			jsonMap.put(Constants.APP_ID, value.getAppId());
		} else {
			jsonMap.put(Constants.APP_ID, null);
		}
	}

	public void setValuesOnCreate2(Ticket value, Map<String, Object> jsonMap) throws ParseException {
		if (!StringUtils.isBlank(String.valueOf(value.getCreatedTime()))) {
			jsonMap.put(Constants.C_T, DateUtil.getFormattedDateInString(value.getCreatedTime()).split(" ")[1]);
		} else {
			jsonMap.put(Constants.C_T, null);
		}
		if (!StringUtils.isBlank(String.valueOf(value.getUpdatedTime()))) {
			jsonMap.put(Constants.U_T, DateUtil.getFormattedDateInString(value.getUpdatedTime()).split(" ")[1]);
		} else {
			jsonMap.put(Constants.U_T, null);
		}
			jsonMap.put(Constants.CREATED_TIME_TS, value.getCreatedTime().getTime());
			jsonMap.put(Constants.UPDATED_TIME_TS, value.getUpdatedTime().getTime());
		if (!StringUtils.isBlank(String.valueOf(value.getRequestedBy()))) {
			jsonMap.put(Constants.REQUESTED_BY, value.getRequestedBy());
		} else {
			jsonMap.put(Constants.REQUESTED_BY, null);
		}
		if (!StringUtils.isBlank(value.getAppVersion())) {
			jsonMap.put(Constants.APP_VERSION, value.getAppVersion());
		} else {
			jsonMap.put(Constants.APP_VERSION, null);
		}
	}

	public void setValuesOnCreate(Ticket value, Map<String, Object> jsonMap) {
		if (!StringUtils.isBlank(String.valueOf(value.getId()))) {
			jsonMap.put(Constants.ID, value.getId());
		} else {
			jsonMap.put(Constants.ID, 0);
		}
		if (!StringUtils.isBlank(value.getDescription())) {
			jsonMap.put(Constants.DESCRIPTION, value.getDescription());
		} else {
			jsonMap.put(Constants.DESCRIPTION, null);
		}
		if (!StringUtils.isBlank(value.getReviewId())) {
			jsonMap.put(Constants.REVIEW_ID, value.getReviewId());
		} else {
			jsonMap.put(Constants.REVIEW_ID, null);
		}
		if (!StringUtils.isBlank(String.valueOf(value.getRate()))) {
			jsonMap.put(Constants.RATE, value.getRate());
		} else {
			jsonMap.put(Constants.RATE, null);
		}
		if (!StringUtils.isBlank(String.valueOf(value.getMaxRating()))) {
			jsonMap.put(Constants.MAX_RATING, value.getMaxRating());
		} else {
			jsonMap.put(Constants.MAX_RATING, null);
		}
		if (!StringUtils.isBlank(String.valueOf(value.getCreatedTime()))) {
			jsonMap.put(Constants.CREATED_TIME, DateUtil.getFormattedDateInString(value.getCreatedTime()).split(" ")[0]);
		} else {
			jsonMap.put(Constants.CREATED_TIME, null);
		}
		if (!StringUtils.isBlank(String.valueOf(value.getUpdatedTime()))) {
			jsonMap.put(Constants.UPDATED_TIME, DateUtil.getFormattedDateInString(value.getUpdatedTime()).split(" ")[0]);
		} else {
			jsonMap.put(Constants.UPDATED_TIME, null);
		}
	}

	private void deleteTicketDataFromElastic(Ticket data) throws IOException {
		RestHighLevelClient rc = new RestHighLevelClient(RestClient.builder(new HttpHost(elasticsearchUrl)));
		try {
			DeleteRequest request = new DeleteRequest(elasticsearchIndex, "doc", String.valueOf(data.getId()));
			rc.delete(request, RequestOptions.DEFAULT);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		} finally {
			rc.close();
		}
	}

	public Long timestampconvertor(String createdDate) throws ParseException {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
			Date date = dateFormat.parse(createdDate);
			return date.getTime() / 1000;
		} catch (Exception e) {
			LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION_S, e.getMessage()));
		}
		return null;
	}

	private RestHighLevelClient connectToElasticSearch() {
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(Constants.ELASTIC, Constants.EL_STIC123));

		HttpClientConfigCallback httpClientConfigCallback = new HttpClientConfigCallback() {
			@Override
			public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
				return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
			}
		};
		return new RestHighLevelClient(RestClient.builder(new HttpHost(elasticsearchUrl))
				.setHttpClientConfigCallback(httpClientConfigCallback));
	}

	private void writeDatatoElastic(Map<String, Object> jsonMap, String id) throws IOException {
		RestHighLevelClient rc = connectToElasticSearch();
		try {
			IndexRequest indexRequest = new IndexRequest(elasticsearchIndex, elasticsearchType, id).source(jsonMap);
			IndexResponse response = rc.index(indexRequest, RequestOptions.DEFAULT);
			if (!StringUtils.isBlank(response.toString())) {
				LOGGER.info("Response : {}", response);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		} finally {
			rc.close();
		}
	}

	private void updateDataInElastic(Map<String, Object> jsonMap, String id) throws IOException {
		RestHighLevelClient rc = connectToElasticSearch();
		try {
			UpdateRequest updateRequest = new UpdateRequest(elasticsearchIndex, elasticsearchType, id).doc(jsonMap);
			rc.update(updateRequest, RequestOptions.DEFAULT);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		} finally {
			rc.close();
		}
	}
}