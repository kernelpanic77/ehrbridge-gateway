package com.ehrbridge.gateway.dto.ConsentManager;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestDetails {
    private String hiuName;
    private String hipName;
    private String doctorName;
}
