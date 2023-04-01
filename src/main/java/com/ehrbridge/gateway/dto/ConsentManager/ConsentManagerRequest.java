package com.ehrbridge.gateway.dto.ConsentManager;

import com.ehrbridge.gateway.dto.consent.ConsentObjectRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsentManagerRequest {
    private String txnID;
    private RequestDetails requestDetails;
    private ConsentObjectRequest consent_obj;
}
