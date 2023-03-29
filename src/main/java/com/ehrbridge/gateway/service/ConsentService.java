package com.ehrbridge.gateway.service;

import com.ehrbridge.gateway.dto.ConsentManager.*;
import com.ehrbridge.gateway.dto.Hospital.HookConsentObjectHIURequest;
import com.ehrbridge.gateway.dto.Hospital.HookConsentObjectHIPRequest;
import com.ehrbridge.gateway.dto.consent.GenerateConsentRequest;
import com.ehrbridge.gateway.dto.consent.GenerateConsentResponse;
import com.ehrbridge.gateway.entity.Transactions;
import com.ehrbridge.gateway.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ConsentService {
    private final TransactionRepository transactionRepository;
    // TODO: private final HospitalRepository hospitalRepository;
    @Value("${consentmanager.host}")
    private String CM_URL;

    @Value("${consentmanager.consent-request.endpoint")
    private String CM_ENDPOINT;

    @Autowired
    private HttpHeaders headers;

    @Autowired
    private RestTemplate rest;
    public GenerateConsentResponse generateConsent(GenerateConsentRequest request)
    {
        String hiuId = request.getConsent_object().getHiuID();
        String hipId = request.getConsent_object().getHipID();
        String doctorId = request.getConsent_object().getDoctorID();

        var transaction = Transactions.builder()
                .txn_status("PENDING")
                .hiuId(hiuId)
                .hipId(hipId)
                .doctorId(doctorId)
                .build();

        transactionRepository.save(transaction);
        var txnId = transaction.getTxnId();
        //TODO: Check iff HIP, HIU, Doctir iare valid and id's exist in the registry.
        //TODO: Get Original names from repository using id's.
        var requestDetails = RequestDetails.builder()
                .hipName("hip")
                .hiuName("hiu")
                .doctorName("doctor")
                .build();
        var consentManagerRequest = ConsentManagerRequest.builder()
                .txnId(txnId)
                .requestDetails(requestDetails)
                .consentObjectRequest(request.getConsent_object())
                .build();

        ResponseEntity<ConsentManagerResponse> consentManagerResponse = pushConsentObjectToGateway(consentManagerRequest);
        if(consentManagerResponse == null)
        {
            transaction.setTxn_status("FAILED");
            transactionRepository.save(transaction);
            return GenerateConsentResponse.builder().txn_id(txnId).status("FAILED").build();
        }
        else
        {
            return GenerateConsentResponse.builder().txn_id(txnId).status("PENDING").build();
        }

    }



    ResponseEntity<ConsentManagerResponse> pushConsentObjectToGateway(ConsentManagerRequest request)
    {
        final String CM_REQ_ENDPOINT = CM_URL + CM_ENDPOINT;
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            String jsonCmRequest = objectWriter.writeValueAsString(request);
            HttpEntity<String> requestEntity = new HttpEntity<String>(jsonCmRequest, headers);
            ResponseEntity<ConsentManagerResponse> responseEntity = rest.exchange(CM_REQ_ENDPOINT, HttpMethod.POST, requestEntity, ConsentManagerResponse.class);
            if(responseEntity.getStatusCode().value() == 200){
                return responseEntity;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }
        return null;
    }

    public HookConsentObjectResponse receiveConsent(HookConsentObjectRequest request) {
        Transactions transaction = transactionRepository.findByTxnId(request.getTxnId()).orElseThrow();

        var hiuId = transaction.getHiuId();
        var hipId = transaction.getHipId();

        // TODO: var hookURLHIU = hospitalRepository.findById(hiudId).getHookURL();
        // TODO: var hookURLHIP = hospitalRepository.findById(hipdId).getHookURL();
        String hookURLHIU = null;
        String hookURLHIP = null;

        HookConsentObjectHIURequest hiuRequest = HookConsentObjectHIURequest.builder()
                    .consent_status(request.getConsent_status())
                    .signed_consent_object(request.getSigned_consent_obj())
                    .txnId(request.getTxnId())
                    .build();

        HookConsentObjectHIPRequest hipRequest = HookConsentObjectHIPRequest
                .builder()
                .signed_consent_obj(request.getSigned_consent_obj())
                .txnID(request.getTxnId())
                .public_key(request.getPublic_key())
                .build();

        ResponseEntity<String> responseHIP = pushConsentObjectToHIP(hipRequest, hookURLHIU );
        ResponseEntity<String> responseHIU = pushConsentObjectToHIU(hiuRequest, hookURLHIP);


        return HookConsentObjectResponse.builder().message("Consent Objects Sent Successfully").build();
    }

    private ResponseEntity<String> pushConsentObjectToHIU(HookConsentObjectHIURequest request, String hookURL) {
        final String URL = hookURL + "/api/v1/consent/recieve-hiu";
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            String jsonCmRequest = objectWriter.writeValueAsString(request);
            HttpEntity<String> requestEntity = new HttpEntity<String>(jsonCmRequest, headers);
            ResponseEntity<String> responseEntity = rest.exchange(URL, HttpMethod.POST, requestEntity, String.class);
            if(responseEntity.getStatusCode().value() == 200){
                return responseEntity;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }
        return null;

    }

    private ResponseEntity<String> pushConsentObjectToHIP(HookConsentObjectHIPRequest request, String hookURL) {
        final String URL = hookURL + "/api/v1/consent/recieve-hip";
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            String jsonCmRequest = objectWriter.writeValueAsString(request);
            HttpEntity<String> requestEntity = new HttpEntity<String>(jsonCmRequest, headers);
            ResponseEntity<String> responseEntity = rest.exchange(URL, HttpMethod.POST, requestEntity, String.class);
            if(responseEntity.getStatusCode().value() == 200){
                return responseEntity;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }
        return null;



    }
}
