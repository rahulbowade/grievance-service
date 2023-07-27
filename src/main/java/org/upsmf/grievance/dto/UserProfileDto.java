package org.upsmf.grievance.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This Data Transfer Object will carry the information related to the User
 * Profile to update the User Profile Information in the API
 *
 * @author Darshan Nagesh
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(includeFieldNames = true)
@Builder
public class UserProfileDto {

	private Long id;
	private Long userId;
	private String firstName;
	private String lastName;
	private int age;
	private String emailId;
	private String phoneNumber;
	private String dob;
	private String gender;
	private String avatarUrl;
	private Date startDate;
	private Date endDate;
	private Long salary;
	private String address;
	private Boolean isActive;
	private Boolean isDeleted;
	private Date registrationDate;
	private Date createdDate;
	private Long createdBy;
	private Date updatedDate;
	private Long updatedBy;
	private String employmentType;
}
