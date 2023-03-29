package com.ehrbridge.gateway.dto.auth.doctor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRegisterRequest {
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String gender; 
    private String address;
    private String phoneString;
    private String department;
}
