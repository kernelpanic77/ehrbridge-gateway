package com.ehrbridge.gateway.controller;

import com.ehrbridge.gateway.dto.auth.*;
import com.ehrbridge.gateway.dto.auth.doctor.DoctorRegisterRequest;
import com.ehrbridge.gateway.dto.auth.doctor.DoctorRegisterResponse;
import com.ehrbridge.gateway.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ehrbridge.gateway.service.AuthService;

import lombok.RequiredArgsConstructor;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;


@RestController
@CrossOrigin
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private AuthService authService;

    @Autowired
    private OtpService otpService;


    @PostMapping("/register/user")
    public ResponseEntity<RegisterReponse> registerUser(@RequestBody RegisterRequest request) throws MessagingException, UnsupportedEncodingException {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<VerifyOtpResponse> registerUser(@RequestBody VerifyOtpRequest request) throws MessagingException, UnsupportedEncodingException {
        VerifyOtpResponse response = otpService.verifyOtp(request);
        if (response.getMessage().equals("OTP verification Successful")) {
            return ResponseEntity.ok(authService.updateResponse(response));
        }
        return new ResponseEntity<VerifyOtpResponse>(response, HttpStatusCode.valueOf(403));
    }

    @PostMapping("/signin/user")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody AuthRequest request) throws UnsupportedEncodingException, MessagingException {
        return ResponseEntity.ok(authService.authenticate(request));
    }


    @PostMapping("/register/doctor")
    public ResponseEntity<DoctorRegisterResponse> registerDoctor(@RequestBody DoctorRegisterRequest request){
        return ResponseEntity.ok(authService.registerDoctor(request));
    }

    @PostMapping("/register/hospital")
    public ResponseEntity<HospitalRegisterResponse> registerHospital(@RequestBody HospitalRegisterRequest request)
    {
        
        return ResponseEntity.ok(authService.registerHospital(request));
    }


}
