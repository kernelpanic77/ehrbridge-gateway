package com.ehrbridge.gateway.dto.data;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataRequestHIPRequest {
    private String signed_consent_object;
    private String txnID;
    private String requestID;
    private String ehrbID;
    private String hiuID;
    private String request_msg;
    private String callbackURL;
    private Date dateFrom;
    private Date dateTo;
    private String[] hiType;
    private String[] departments;
    private String rsa_pubkey;
}
