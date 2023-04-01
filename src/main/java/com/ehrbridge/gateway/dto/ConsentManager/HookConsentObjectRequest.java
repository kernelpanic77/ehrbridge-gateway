package com.ehrbridge.gateway.dto.ConsentManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HookConsentObjectRequest {
    private String txnID;
    private String consent_status;
    private String public_key;
    private String signed_consent_obj;
}
