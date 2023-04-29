package com.ehrbridge.gateway.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ehrbridge.gateway.dto.Hospital.FetchAllHospitalResponse;
import com.ehrbridge.gateway.dto.Hospital.PatientServerHospitalsResponse;
import com.ehrbridge.gateway.entity.Hospital;
import com.ehrbridge.gateway.repository.HospitalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HospitalService {
    
    @Value("${patientserver.host}")
    private String PS_HOST;

    @Value("${patientserver.hospital-discovery.endpoint}")
    private String PS_ENDPOINT;

    @Autowired
    private RestTemplate rest;

    @Autowired
    private HttpHeaders headers;

    private final HospitalRepository hospitalRepository;

    public ResponseEntity<PatientServerHospitalsResponse> fetchHospitals(String ehrbID){
        String REQ_ENDPOINT = PS_HOST + PS_ENDPOINT + "?ehrbID=" + ehrbID;
        System.out.println(REQ_ENDPOINT);
        try {
            ResponseEntity<PatientServerHospitalsResponse> responseEntity = rest.exchange(REQ_ENDPOINT, HttpMethod.GET, new HttpEntity<>(headers), PatientServerHospitalsResponse.class);
            if(responseEntity.getStatusCode().value() == 200){
                return responseEntity;
            }
            //List<Hospital> hospitals = hospitalRepository.findAll();
            //return new ResponseEntity<PatientServerHospitalsResponse>(PatientServerHospitalsResponse.builder().hospitals(hospitals).build(), HttpStatusCode.valueOf(200));

        } catch (Exception e) {
            // TODO: handle exception
        }
        
        return new ResponseEntity<PatientServerHospitalsResponse>(PatientServerHospitalsResponse.builder().hospitals(null).build(), HttpStatusCode.valueOf(500));

    }

    public ResponseEntity<Hospital> fetchHospital(String hospitalID){
        try {
            Optional<Hospital> hospital = hospitalRepository.findById(hospitalID);
            return new ResponseEntity<Hospital>(hospital.get(), HttpStatusCode.valueOf(200));

        } catch (Exception e) {
            // TODO: handle exception
        }
        
        return new ResponseEntity<Hospital>(HttpStatusCode.valueOf(500));

    }
}
