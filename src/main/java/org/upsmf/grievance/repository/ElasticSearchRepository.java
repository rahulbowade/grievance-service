package org.upsmf.grievance.repository;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import org.upsmf.grievance.model.TemplateVersion;

/**
 * This Repository Class is used to perform the transactions of storing the data
 * into the Elastic Search Repository
 *
 * @author Darshan Nagesh
 *
 */
@Service
public class ElasticSearchRepository {

	private static final String ERROR = "Error : %s";
	String daoImplMarker = "AuroraESRepoMarker";
	Marker marker = MarkerFactory.getMarker(daoImplMarker);

	public static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchRepository.class);

	private final RestTemplate restTemplate;
	private RestHighLevelClient client;
	private String elasticHost;
	private int elasticPort;
	private static final String REST_SCHEME = "http";

	public ElasticSearchRepository(RestTemplate restTemplate,
			@Value("${services.esindexer.host.name}") String elasticHost,
			@Value("${services.esindexer.host.port}") int elasticPort) {
		this.elasticHost = elasticHost;
		this.elasticPort = elasticPort;
		this.restTemplate = restTemplate;
		this.client = getClientForElastic();
	}

	private RestHighLevelClient getClientForElastic() {
		return new RestHighLevelClient(RestClient.builder(new HttpHost(elasticHost, elasticPort, REST_SCHEME)));
	}

	public Boolean saveTemplate(TemplateVersion templateVersion, String url, HttpHeaders headers) {
		ResponseEntity<Map> map = null;
		try {
			map = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(templateVersion, headers), Map.class);
		} catch (final HttpClientErrorException httpClientErrorException) {
			LOGGER.error(String.format(ERROR, httpClientErrorException));
		} catch (HttpServerErrorException httpServerErrorException) {
			LOGGER.error(String.format(ERROR, httpServerErrorException));
		} catch (Exception e) {
			LOGGER.error(String.format(ERROR, e.getMessage()));
		}
		return (map != null && map.getStatusCode() != null
				&& ((map.getStatusCode() == HttpStatus.OK) || (map.getStatusCode() == HttpStatus.CREATED)));
	}

	public MultiSearchResponse executeMultiSearchRequest(SearchRequest searchRequest) {
		MultiSearchRequest multiRequest = new MultiSearchRequest();
		MultiSearchResponse response = null;
		if (!StringUtils.isBlank(marker.toString()) && !StringUtils.isBlank(searchRequest.source().toString())) {
			LOGGER.info("ES Query is : {}", searchRequest.source());
		}
		multiRequest.add(searchRequest);
		try {
			response = client.multiSearch(multiRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			LOGGER.error(String.format(marker.toString(), " Encountered an error while connecting : %s", e));
			LOGGER.error(String.format(marker.toString(), " Error Message to report : %s", e.getMessage()));
		}
		return response;
	}
}
