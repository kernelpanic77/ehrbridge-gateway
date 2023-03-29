package com.ehrbridge.gateway.dto.Hospital;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HookConsentObjectHIURequest {
    private String txnId;
    private String consent_status;
    private String signed_consent_object;

}
