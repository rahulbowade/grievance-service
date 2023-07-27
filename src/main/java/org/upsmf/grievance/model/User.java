package org.upsmf.grievance.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This class holds the information about the User's basic authentication along
 * with Mail ID.
 *
 * @author Darshan Nagesh
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(includeFieldNames = true)
public class User {

	private Long id;

	private String username;
	private byte[] img;
	@JsonInclude(Include.NON_NULL)
	private String password;
	@JsonInclude(Include.NON_NULL)
	private String phone;

	@JsonInclude(Include.NON_NULL)
	private Long orgId;

	private String name;
	private String imagePath;

	@JsonInclude(Include.NON_NULL)
	private Boolean isActive;

	@JsonInclude(Include.NON_NULL)
	private String authType;

	@JsonProperty("authToken")
	@JsonInclude(Include.NON_NULL)
	private String authToken;

	@JsonInclude(Include.NON_NULL)
	private String createdDate;

	@JsonInclude(Include.NON_NULL)
	private Long createdBy;

	@JsonInclude(Include.NON_NULL)
	private String updatedDate;

	@JsonInclude(Include.NON_NULL)
	private Long updatedBy;

	@JsonInclude(Include.NON_NULL)
	private List<Role> roles;

	@JsonInclude(Include.NON_NULL)
	private List<Action> actions;

	private List<Organization> organization;
	private List<Helpdesk> helpdesk;

	private Boolean isAnonymous;

}
