package org.upsmf.grievance.model.es;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.upsmf.grievance.dto.TicketWorkflowDto;
import org.upsmf.grievance.model.ChecklistItem;
import org.upsmf.grievance.model.Tags;
import org.upsmf.grievance.model.Updates;

import javax.persistence.Id;
import java.util.List;

@Document(indexName = "grievance-ticket", createIndex = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    private String id;

    @Field(name="ticketId")
    private Long ticketId;
    
    @Field(name = "userId")
    private Long userId;

    @Field(name = "description")
    private String description;

    @Field(name = "feedback")
    private String feedback;

    @Field(name = "reviewId")
    private String reviewId;

    @Field(name = "developerComment")
    private String developerComment;

    @Field(name = "rate", type = FieldType.Integer)
    private int rate;

    @Field(name = "maxRating", type = FieldType.Integer)
    private int maxRating;

    @Field(name = "createdTime")
    private String createdTime;

    @Field(name = "updatedTime")
    private String updatedTime;

    @Field(name = "createdTimeTS")
    private Long createdTimeTS;

    @Field(name = "updateTimeTS")
    private Long updatedTimeTS;

    @Field(name = "userName")
    private String userName;

    @Field(name = "requestedBy")
    private Long requestedBy;

    @Field(name = "operation")
    private String operation;

    @Field(name = "ts")
    private String ts;

    @Field(name = "total")
    private Long total;

    @Field(name = "userTimestamp")
    private String userTimestamp;

    @Field(name = "developerTimestamp")
    private String developerTimestamp;

    @Field(name = "appName")
    private String appName;

    @Field(name = "appVersion")
    private String appVersion;

    @Field(name = "country")
    private String country;

    @Field(name = "feedbackByName")
    private String feedbackByName;

    @Field(name = "sv")
    private String sv;

    @Field(name = "userEvent")
    private String userEvent;

    @Field(name = "ip")
    private String ip;

    @Field(name = "deviceName")
    private String deviceName;

    @Field(name = "deviceManufacture")
    private String deviceManufacture;

    @Field(name = "deviceScreenResolution")
    private String deviceScreenResolution;

    @Field(name = "deviceLocale")
    private String deviceLocale;

    @Field(name = "deviceType")
    private String deviceType;

    @Field(name = "osType")
    private String osType;

    @Field(name = "osVersion")
    private String osVersion;

    @Field(name = "requestedByName")
    private String requestedByName;

    @Field(name = "priority")
    private String priority;

    @Field(name = "filterStatus", type = FieldType.Keyword)
    private List<String> filterStatus;

    @Field(name = "notes")
    private String notes;

    @Field(name = "checkList")
    private List<ChecklistItem> checklist;

    @Field(name = "appId")
    private Long appId;

    @Field(name = "appKey")
    private String appKey;

    @Field(name = "helpdeskId")
    private Long helpdeskId;

    @Field(name = "sourceId")
    private Long sourceId;

    @Field(name = "type")
    private Long type;

    @Field(name = "updates")
    private List<Updates> updates;

    @Field(name = "orgId")
    private Long orgId;

    @Field(name = "active")
    private Boolean active;

    @Field(name = "pinnedTicket")
    private Boolean pinnedTicket = false;

    @Field(name = "workflowStages")
    private List<TicketWorkflowDto> workflowStages;

    @Field(name = "cc")
    private List<Long> cc;

    @Field(name = "filterCTUT")
    private String filterCTUT;

    @Field(name = "from")
    private int from;

    @Field(name = "size")
    private int size;

    @Field(name = "searchKeyword")
    private String searchKeyword;

    @Field(name = "status")
    private String status;

    @Field(name = "img")
    private byte[] img;

    @Field(name = "attachmentUrl")
    private List<String> attachmentUrl;

    @Field(name = "selectedTags")
    private List<String> selectedTags;

    @Field(name = "tags")
    private List<Tags> tags;
}
