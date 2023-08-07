package org.upsmf.grievance.model;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Component;
import org.upsmf.grievance.dto.TicketWorkflowDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(includeFieldNames = true)
@Component
public class Ticket {

	private Long userId;
	private Long id;
	private String description;
	private String feedback;
	private String reviewId;
	private String developerComment;
	private int rate;
	private int maxRating;
	private Timestamp createdTime;
	private Timestamp updatedTime;
	private Long createdTimeTS;
	private Long updatedTimeTS;
	private String userName;
	private Long requestedBy;
	private String operation;
	private Long ts;
	private Long total;
	private Long userTimestamp;
	private Long developerTimestamp;

	private String appName;
	private String appVersion;
	private String country;
	private String feedbackByName;
	private String sv;
	private String userEvent;
	private String ip;
	private String deviceName;
	private String deviceManufacture;
	private String deviceScreenResolution;
	private String deviceLocale;
	private String deviceType;
	private String osType;
	private String osVersion;

	private String requestedByName;
	private String priority;
	private List<String> filterStatus;
	private String notes;
	private List<ChecklistItem> checklist;

	private Long appId;
	private String appKey;
	private Long helpdeskId;
	private Long sourceId;
	private Long type;
	private List<Updates> updates;
	private Long orgId;
	private Boolean active;
	private Boolean pinnedTicket = false;
	private List<TicketWorkflowDto> workflowStages;
	private List<Long> cc;
	private String filterCTUT;
	private int from;
	private int size;
	private String searchKeyword;
	private String status;
	private byte[] img;
	private List<String> attachmentUrl;
	private List<String> selectedTags;
	private List<Tags> tags;
	private String requesterEmail;
	private String requesterPhoneNumber;
	private String requesterUser;

	private Boolean junk;

	private Long lastUpdatedBy;

	private Boolean escalated;

	private Timestamp escalatedDate;

	private Long escalatedTo;

	private String requestedType;

	private List<String> comment;

	private List<String> requestedAttachmentUrl;

	private String escalatedBy;
}