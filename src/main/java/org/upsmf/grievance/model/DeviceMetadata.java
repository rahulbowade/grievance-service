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
public class DeviceMetadata {

	private String productName;
	private String manufacturer;
	private String deviceClass;
	private Long screenWidthPx;
	private Long screenHeightPx;
	private String nativePlatform;
	private Long screenDensityDpi;
	private Long glEsVersion;
	private String cpuModel;
	private String cpuMake;
	private Long ramMb;

}
