package org.upsmf.grievance.util;

import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.upsmf.grievance.dao.impl.SuperAdminDaoImpl;
import org.upsmf.grievance.model.S3Config;

public class S3FileManager {

	private S3FileManager() {
		super();
	}

	public static final Logger LOGGER = LoggerFactory.getLogger(SuperAdminDaoImpl.class);

	/**
	 * This method will remove particular file form given bucket name.
	 *
	 * @param fileName
	 *            String
	 * @param bucketLocation
	 *            String
	 * @return boolean
	 */

	private static AmazonS3 getS3(S3Config s3Config) {
		AWSCredentialsProvider provider = new AWSCredentialsProviderChain(new AWSStaticCredentialsProvider(
				new BasicAWSCredentials(s3Config.getAccessKey(), s3Config.getSecretKey())));
		return AmazonS3ClientBuilder.standard().withRegion(Regions.fromName("ap-south-1")).withCredentials(provider)
				.enablePathStyleAccess().build();
	}

	public static String filePath(String fileName, String folder, Long userId, Long compId) {
		String key = null;
		key = compId + "/" + folder + "/" + userId + "/" + fileName;
		return key;

	}

	public static String attachementfilePath(String fileName, String folder, Long ticketId, Long compId) {
		String key = null;
		key = compId + "/" + folder + "/" + ticketId + "/" + fileName;
		return key;

	}

	public static String getPreSignedURL(S3Config s3values, String path) {
		try {
			GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(s3values.getBucketName(), path,
					HttpMethod.GET);
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
			cal.add(Calendar.HOUR, 100);
			request.withExpiration(cal.getTime());
			URL url = getS3(s3values).generatePresignedUrl(request);
			return url.toString();
		} catch (Exception e) {
			LOGGER.error(String.format("S3 url for download Error %s", e.getMessage()));
		}
		return null;
	}

}
