package com.ehrbridge.gateway.dto.consent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ConsentObjectRequest {
    private String ehrbID;
    private String hiuID;
    private String hipID;
    private String doctorID;
    private String[] hiType;
    private String[] departments;
    private PermissionConsentObject permission;
}