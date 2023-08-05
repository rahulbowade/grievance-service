package org.upsmf.grievance.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(includeFieldNames = true)
public class GrievanceRaise {

    private String name;

    private String emailId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phone;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long orgId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long helpdeskId;

    private String description;

    private List<String> filePath;


}
