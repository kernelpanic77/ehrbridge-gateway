package com.ehrbridge.gateway.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyOtpResponse {
        private String ehrbid;
        private String token;

        private String message;
}
