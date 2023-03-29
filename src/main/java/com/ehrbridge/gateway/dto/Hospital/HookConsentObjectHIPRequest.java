package com.ehrbridge.gateway.dto.Hospital;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HookConsentObjectHIPRequest {
    private String txnID;
    private String public_key;
    private String signed_consent_obj;
}
