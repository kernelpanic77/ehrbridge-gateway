package com.ehrbridge.gateway.service;

import com.ehrbridge.gateway.dto.ConsentManager.*;
import com.ehrbridge.gateway.dto.Hospital.HookConsentObjectHIURequest;
import com.ehrbridge.gateway.dto.Hospital.HookConsentObjectHIPRequest;
import com.ehrbridge.gateway.dto.consent.GenerateConsentRequest;
import com.ehrbridge.gateway.dto.consent.GenerateConsentResponse;
import com.ehrbridge.gateway.entity.Doctor;
import com.ehrbridge.gateway.entity.Transactions;
import com.ehrbridge.gateway.repository.DoctorRepository;
import com.ehrbridge.gateway.repository.HospitalRepository;
import com.ehrbridge.gateway.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.twilio.http.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ConsentService {
    private final TransactionRepository transactionRepository;
    private final HospitalRepository hospitalRepository;
    private final DoctorRepository doctorRepository;
    @Value("${consentmanager.host}")
    private String CM_URL;

    @Value("${consentmanager.consent-request.endpoint}")
    private String CM_ENDPOINT;

    @Autowired
    private HttpHeaders headers;
    private final ApiKeyService apiKeyService;

    @Autowired
    private RestTemplate rest;
    public ResponseEntity<GenerateConsentResponse> generateConsent(GenerateConsentRequest request, String api_key)
    {
        String hiuId = request.getConsent_object().getHiuID();
        String hipId = request.getConsent_object().getHipID();
        String doctorId = request.getConsent_object().getDoctorID();


        String hiuName,hipName, doctorName;
        try{
            var doctor = doctorRepository.findById(doctorId).orElseThrow();
            doctorName = doctor.getFirstName() + " " + doctor.getLastName();
        }catch (Exception e)
        {
            return new ResponseEntity<GenerateConsentResponse>(GenerateConsentResponse.builder().message("Doctor Id Not found in the registry").build(), HttpStatusCode.valueOf(400));
        }
        try{
            var hiu = hospitalRepository.findById(hiuId).orElseThrow();
            hiuName = hiu.getHospitalName();
        }catch (Exception e)
        {
            return new ResponseEntity<GenerateConsentResponse>(GenerateConsentResponse.builder().message("HIU Id Not found in the registry").build(), HttpStatusCode.valueOf(400));

        }
        try{
            var hip = hospitalRepository.findById(hipId).orElseThrow();
            hipName = hip.getHospitalName();
        }catch (Exception e)
        {
            return new ResponseEntity<GenerateConsentResponse>(GenerateConsentResponse.builder().message("HIP Id Not found in the registry").build(), HttpStatusCode.valueOf(400));

        }
        if(!apiKeyService.validateApiKey(api_key))
        {
            System.out.println("dlkhkjkk");
            return new ResponseEntity<GenerateConsentResponse>(GenerateConsentResponse.builder().message("API Key expired, please generate a new one").status("FAILED").build(), HttpStatusCode.valueOf(402));
        }
        if(apiKeyService.getHospitalIdfromApiKey(api_key).equals(hiuId))
        {
            var transaction = Transactions.builder()
                    .txn_status("PENDING")
                    .hiuId(hiuId)
                    .hipId(hipId)
                    .doctorId(doctorId)
                    .build();

            transactionRepository.save(transaction);
            var txnID = transaction.getTxnID();

            //TODO: Check iff HIP, HIU, Doctir iare valid and id's exist in the registry.
            //TODO: Get Original names from repository using id's.
            var requestDetails = RequestDetails.builder()
                    .hipName(hipName)
                    .hiuName(hiuName)
                    .doctorName(doctorName)
                    .build();
            var consentManagerRequest = ConsentManagerRequest.builder()
                    .txnID(txnID)
                    .request_details(requestDetails)
                    .consent_obj(request.getConsent_object())
                    .build();
            ResponseEntity<ConsentManagerResponse> consentManagerResponse = pushConsentObjectToCM(consentManagerRequest);
            System.out.println(txnID);
            if(consentManagerResponse == null)
            {
                transaction.setTxn_status("FAILED");
                transactionRepository.save(transaction);
                return new ResponseEntity<GenerateConsentResponse>(GenerateConsentResponse.builder().txnID(txnID).status("FAILED").message("There wasn an error while sending the request to the consent manager, please try again").build(), HttpStatusCode.valueOf(503));
            }
            else
            {
                System.out.println("yayyy");
                return new ResponseEntity<GenerateConsentResponse>(GenerateConsentResponse.builder().txnID(txnID).status("PENDING").message("Consent Request sent successfully").build(), HttpStatusCode.valueOf(200));
            }
        }
        else
        {
            return new ResponseEntity<GenerateConsentResponse>(GenerateConsentResponse.builder().message("Please enter a valid API KEY").status("FAILED").build(), HttpStatusCode.valueOf(401));
        }


    }



    ResponseEntity<ConsentManagerResponse> pushConsentObjectToCM(ConsentManagerRequest request)
    {
        final String CM_REQ_ENDPOINT = CM_URL + CM_ENDPOINT;
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        System.out.println(CM_REQ_ENDPOINT);
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

    public ResponseEntity<HookConsentObjectResponse> receiveConsent(HookConsentObjectRequest request) {
        System.out.println(request);
        Transactions transaction = transactionRepository.findByTxnID(request.getTxnID()).orElseThrow();

        var hiuId = transaction.getHiuId();
        var hipId = transaction.getHipId();

        // TODO: var hookURLHIU = hospitalRepository.findById(hiudId).getHookURL(); - DONE
        // TODO: var hookURLHIP = hospitalRepository.findById(hipdId).getHookURL(); - DONE
        String hookURLHIU = hospitalRepository.findById(hiuId).orElseThrow().getHook_url();
        String hookURLHIP = hospitalRepository.findById(hipId).orElseThrow().getHook_url();

        HookConsentObjectHIURequest hiuRequest = HookConsentObjectHIURequest
                    .builder()
                    .consent_status(request.getConsent_status())
                    .signed_consent_object(request.getSigned_consent_obj())
                    .txnID(request.getTxnID())
                    .build();

        HookConsentObjectHIPRequest hipRequest = HookConsentObjectHIPRequest
                .builder()
                .signed_consent_obj(request.getSigned_consent_obj())
                .txnID(request.getTxnID())
                .public_key(request.getPublic_key())
                .build();
        System.out.println(hipRequest);
        System.out.println(hiuRequest);
        ResponseEntity<String> responseHI = pushConsentObjectToHIP(hipRequest, hookURLHIP );
        ResponseEntity<String> responseHIU = pushConsentObjectToHIU(hiuRequest, hookURLHIU);


        return new ResponseEntity<HookConsentObjectResponse>(HookConsentObjectResponse.builder().message("Consent Objects Sent Successfully").build(), HttpStatusCode.valueOf(200));
    }

    private ResponseEntity<String> pushConsentObjectToHIU(HookConsentObjectHIURequest request, String hookURL) {
        final String URL = hookURL + "/api/v1/consent/recieve-hiu";
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            System.out.println(request);
            String jsonCmRequest = objectWriter.writeValueAsString(request);
            System.out.println(jsonCmRequest);
            HttpEntity<String> requestEntity = new HttpEntity<String>(jsonCmRequest, headers);
            System.out.println(jsonCmRequest);
            System.out.println(URL);
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
