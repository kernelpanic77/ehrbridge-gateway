package com.ehrbridge.gateway.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterPatientServerRequest {
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String gender; 
    private String address;
    private String phoneString;
    private String password;
    private String ehrbID;
}
