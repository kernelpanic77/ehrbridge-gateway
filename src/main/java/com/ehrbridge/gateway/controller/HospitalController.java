package com.ehrbridge.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ehrbridge.gateway.dto.Hospital.FetchAllHospitalResponse;
import com.ehrbridge.gateway.dto.Hospital.PatientServerHospitalsResponse;
import com.ehrbridge.gateway.dto.Hospital.Visit;
import com.ehrbridge.gateway.entity.Hospital;
import com.ehrbridge.gateway.service.HospitalService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/hospitals")
@RequiredArgsConstructor
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;

    @GetMapping("/fetch-all")
    public ResponseEntity<PatientServerHospitalsResponse> fetchHospitalById(@RequestParam String ehrbID){
        return hospitalService.fetchHospitals(ehrbID);
    }

    @GetMapping("/fetch-all-hospitals")
    public ResponseEntity<FetchAllHospitalResponse> fetchAllHospitals(){
        return hospitalService.fetchAllHospitals();
    }
    
    @GetMapping("/fetch")
    public ResponseEntity<Hospital> fetchHospital(@RequestParam String hospitalID){
        return hospitalService.fetchHospital(hospitalID);
    }

    @PostMapping("/notify-visit")
    public ResponseEntity<String> notifyVisit(@RequestBody Visit v){
        return hospitalService.notifyVisit(v);
    }
}
