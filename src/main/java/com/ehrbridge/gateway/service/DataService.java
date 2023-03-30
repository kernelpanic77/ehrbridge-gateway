package com.ehrbridge.gateway.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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

    public DataResponse forwardDataReqToHIP(DataRequest request, String api_key){
        // check if api_key is valid
        if(!apiKeyService.validateApiKey(api_key)){
            return DataResponse.builder().msg("Api Key is invalid!").status("FAIL").build();
        }

        if(!apiKeyService.getHospitalIdfromApiKey(api_key).equals(request.getHiuID())){
            return DataResponse.builder().msg("Api key does not match with the hiuUD in the request, please check the hiuID").status("FAIL").build();
        }

        Optional<Hospital> hipDetails = hospitalRepository.findByHospitalId(request.getHipID());

        if(!hipDetails.isPresent()){
            return DataResponse.builder().msg("hipID invalid!").status("FAIL").build();
        }

        String HOSPITAL_HOOK_URL = hipDetails.get().getHook_url();
        String HOSPITAL_DATA_REQ_ENDPOINT = "/api/v1/data/request-data-hip";

        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            String jsonHIPReq = objectWriter.writeValueAsString(request);
            HttpEntity<String> requestEntity = new HttpEntity<String>(jsonHIPReq, headers);
            ResponseEntity<DataRequestHIPResponse> responseEntity = rest.exchange(HOSPITAL_HOOK_URL + HOSPITAL_DATA_REQ_ENDPOINT, HttpMethod.POST, requestEntity, DataRequestHIPResponse.class);
            if(responseEntity.getStatusCode().value() != 200){
                return DataResponse.builder().msg("unable to connect with HIP").status("FAIL").build();
            }
            System.out.println(responseEntity.getBody());
            return DataResponse.builder().msg("Data Request sent to hip succssfully!").status("PASS").build();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        
        return DataResponse.builder().msg("Internal Server error").status("FAIL").build();
    }

}


