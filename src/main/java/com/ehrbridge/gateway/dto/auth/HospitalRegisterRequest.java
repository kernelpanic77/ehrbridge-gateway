package com.ehrbridge.gateway.dto.auth;

import com.ehrbridge.gateway.entity.HospitalKeys;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalRegisterRequest {
    private String hospitalName;
    private String emailAddress;
    private String phoneString;
    private String address;
    private String hospitalLicense;

    private String hook_url;

}
