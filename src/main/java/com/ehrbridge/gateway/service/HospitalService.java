package com.ehrbridge.gateway.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ehrbridge.gateway.dto.Hospital.FetchAllHospitalResponse;
import com.ehrbridge.gateway.entity.Hospital;
import com.ehrbridge.gateway.repository.HospitalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HospitalService {
    private final HospitalRepository hospitalRepository;

    public ResponseEntity<FetchAllHospitalResponse> fetchHospitals(){
        try {
            List<Hospital> hospitals = hospitalRepository.findAll();
            return new ResponseEntity<FetchAllHospitalResponse>(FetchAllHospitalResponse.builder().hospitals(hospitals).build(), HttpStatusCode.valueOf(200));

        } catch (Exception e) {
            // TODO: handle exception
        }
        
        return new ResponseEntity<FetchAllHospitalResponse>(FetchAllHospitalResponse.builder().hospitals(null).build(), HttpStatusCode.valueOf(500));

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
