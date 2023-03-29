package com.ehrbridge.gateway.dto.auth.doctor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorRegisterResponse {
    private String doctorEhrbID;
    private String msg;
}
