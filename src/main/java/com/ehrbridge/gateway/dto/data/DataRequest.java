package com.ehrbridge.gateway.dto.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataRequest {
    private String signed_consent_object;
    private String txnID;
    private String requestID;
    private String ehrbID;
    private String doctorID;
    private String hiuID;
    private String hipID;
    private String request_msg;
    private String callbackURL;   
    private Date dateFrom;
    private Date dateTo; 
    private String[] hiType;
    private String[] departments;
    private String rsa_pubkey;
}
