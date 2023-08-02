package org.upsmf.grievance.config;

public class MyCustomJwtClaims {
	private String sub;
	private Long userReference;
	private Long orgReference;

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public Long getUserReference() {
		return userReference;
	}

	public void setUserReference(Long userReference) {
		this.userReference = userReference;
	}

	public Long getOrgReference() {
		return orgReference;
	}

	public void setOrgReference(Long orgReference) {
		this.orgReference = orgReference;
	}

}
