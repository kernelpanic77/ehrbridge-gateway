package com.ehrbridge.gateway.dto.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DataRequestHIPRequest {
    private String encrypted_consent_object;
    private String txnID;
    private String requestID;
    private String ehrbID;
    private String hiuID;
    private String request_msg;
    private String callbackURL;
    private String dateFrom;
    private String dateTo;
}
