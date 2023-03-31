package com.ehrbridge.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ehrbridge.gateway.dto.data.DataRequest;
import com.ehrbridge.gateway.dto.data.DataResponse;
import com.ehrbridge.gateway.service.DataService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
public class DataController {

    @Autowired
    private final DataService dataService;

    @PostMapping("/request")
    public ResponseEntity<DataResponse> getDataReqFromHIU(@RequestBody DataRequest request, @RequestHeader(value="api_key", required=true) String api_key){
        return dataService.forwardDataReqToHIP(request, api_key);
    }
}
