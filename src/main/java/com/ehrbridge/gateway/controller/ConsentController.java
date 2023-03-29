package com.ehrbridge.gateway.controller;

import com.ehrbridge.gateway.dto.ConsentManager.HookConsentObjectRequest;
import com.ehrbridge.gateway.dto.ConsentManager.HookConsentObjectResponse;
import com.ehrbridge.gateway.dto.consent.GenerateConsentRequest;
import com.ehrbridge.gateway.dto.consent.GenerateConsentResponse;
import com.ehrbridge.gateway.service.ConsentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/consent")
@RequiredArgsConstructor
public class ConsentController {
    @Autowired
    private final ConsentService consentService;

    @PostMapping("/generate")
    public ResponseEntity<GenerateConsentResponse> generateConsentRequest(@RequestBody GenerateConsentRequest request)
    {
        return  ResponseEntity.ok(consentService.generateConsent(request));
    }


    @PostMapping("/receive")
    public ResponseEntity<HookConsentObjectResponse> generateConsentRequest(@RequestBody HookConsentObjectRequest request)
    {
        return  ResponseEntity.ok(consentService.receiveConsent(request));
    }

    
}