package com.ehrbridge.gateway.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HospitalRegisterResponse {
    private String api_key;
    private String hospitalId;
}
