package com.ehrbridge.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ehrbridge.gateway.dto.data.DataRequest;
import com.ehrbridge.gateway.dto.data.DataRequestHIPResponse;
import com.ehrbridge.gateway.dto.data.DataResponse;
import com.ehrbridge.gateway.entity.Hospital;
import com.ehrbridge.gateway.repository.HospitalRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DataService {

    @Autowired
    private RestTemplate rest;

    @Autowired
    private HttpHeaders headers;

    private final HospitalRepository hospitalRepository;

    private final ApiKeyService apiKeyService;

    public ResponseEntity<DataResponse> forwardDataReqToHIP(DataRequest request, String api_key){
        System.out.println(request);
        System.out.println(api_key);
        boolean api_keyValidity;
        try {
             api_keyValidity = apiKeyService.validateApiKey(api_key);
        }
        catch (Exception e)
        {
            return new ResponseEntity<DataResponse>(DataResponse.builder().message("API Key invalid").build(), HttpStatusCode.valueOf(401));
        }
        // check if api_key is valid
        if(!api_keyValidity){
            return new ResponseEntity<DataResponse>(DataResponse.builder().message("Api Key expired").status("FAIL").build(), HttpStatusCode.valueOf(402));
        }
        boolean checkApiKey;
        System.out.println("yayyy");
        try{
            System.out.println(api_key);
            System.out.println(request.getHiuID());
            checkApiKey = apiKeyService.getHospitalIdfromApiKey(api_key).equals(request.getHiuID());
        }catch (Exception e)
        {
            return new ResponseEntity<DataResponse>(DataResponse.builder().message("API Key invalid").build(), HttpStatusCode.valueOf(401));
        }

        if(!checkApiKey){
            return new ResponseEntity<DataResponse>(DataResponse.builder().message("API Key Invalid").status("FAIL").build(), HttpStatusCode.valueOf(401));
        }
        Hospital hipDetails;
        try{
             hipDetails = hospitalRepository.findByHospitalId(request.getHipID()).orElseThrow();
        } catch (Exception e)
        {
            return new ResponseEntity<DataResponse>(DataResponse.builder().message("HIP Not found").status("FAIL").build(), HttpStatusCode.valueOf(400));
        }


        String HOSPITAL_HOOK_URL = hipDetails.getHook_url();
        String HOSPITAL_DATA_REQ_ENDPOINT = "/api/v1/data/request-data-hip";

        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            String jsonHIPReq = objectWriter.writeValueAsString(request);
            HttpEntity<String> requestEntity = new HttpEntity<String>(jsonHIPReq, headers);
            System.out.println(HOSPITAL_HOOK_URL + HOSPITAL_DATA_REQ_ENDPOINT);
            ResponseEntity<DataRequestHIPResponse> responseEntity = rest.exchange(HOSPITAL_HOOK_URL + HOSPITAL_DATA_REQ_ENDPOINT, HttpMethod.POST, requestEntity, DataRequestHIPResponse.class);
            if(responseEntity.getStatusCode().value() != 200){
                return new ResponseEntity<DataResponse>(DataResponse.builder().message("unable to connect with HIP").status("FAIL").build(), HttpStatusCode.valueOf(503));
            }
            System.out.println(responseEntity.getBody());
            return new ResponseEntity<DataResponse>(DataResponse.builder().message("Data Request sent to hip succssfully!").status("PASS").build(), HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        
        return new ResponseEntity<DataResponse>(DataResponse.builder().message("Internal Server error").status("FAIL").build(), HttpStatusCode.valueOf(500));
    }

}


