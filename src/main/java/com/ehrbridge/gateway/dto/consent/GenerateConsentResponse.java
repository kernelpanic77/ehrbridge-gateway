package com.ehrbridge.gateway.dto.consent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateConsentResponse {
    private String txnID;
    private String status;
    private String message;
}
