package org.upsmf.grievance.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(includeFieldNames = true)
public class OtpRequest {

    private String email;
    private String phoneNumber;

    private  String name;

}
