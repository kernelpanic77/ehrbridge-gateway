package com.ehrbridge.gateway.service;

import com.ehrbridge.gateway.entity.HospitalKeys;
import com.ehrbridge.gateway.repository.HospitalKeysRepository;
import com.ehrbridge.gateway.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiKeyService {
    private final HospitalKeysRepository hospitalKeysRepository;

    public String getHospitalIdfromApiKey(String api_key)
    {
        var hospitalKey = hospitalKeysRepository.getHospitalKeysByApiKey(api_key).orElseThrow();
        return hospitalKey.getHospitalId();
    }

    public boolean validateApiKey(String api_key)
    {
        var hospitalKey = hospitalKeysRepository.getHospitalKeysByApiKey(api_key).orElseThrow();
        if(hospitalKey.getValidity().after(new Date()))
        {
            return true;
        }
        return false;
    }

}
