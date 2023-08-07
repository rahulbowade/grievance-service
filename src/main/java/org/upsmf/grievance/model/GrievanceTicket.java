package org.upsmf.grievance.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(includeFieldNames = true)
@Builder
public class GrievanceTicket {

    private Long ticketId;

    private String date;
}
