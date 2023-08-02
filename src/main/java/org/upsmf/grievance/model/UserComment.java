package org.upsmf.grievance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(includeFieldNames = true)
@Builder
public class UserComment {

	private String text;
	private LastModified lastModified;
	private Long starRating;
	private String reviewerLanguage;
	private String device;
	private Long androidOsVersion;
	private Long appVersionCode;
	private String appVersionName;
	private Long thumbsUpCount;
	private Long thumbsDownCount;
	private DeviceMetadata deviceMetadata;
}