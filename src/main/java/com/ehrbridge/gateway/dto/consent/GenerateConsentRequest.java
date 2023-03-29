package com.ehrbridge.gateway.dto.consent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateConsentRequest {
    private ConsentObjectRequest consent_object;
}
