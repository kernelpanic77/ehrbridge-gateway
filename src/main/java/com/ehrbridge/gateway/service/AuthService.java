package com.ehrbridge.gateway.service;

import com.ehrbridge.gateway.dto.auth.*;

import com.ehrbridge.gateway.dto.auth.doctor.DoctorRegisterRequest;
import com.ehrbridge.gateway.dto.auth.doctor.DoctorRegisterResponse;


import com.ehrbridge.gateway.entity.Hospital;
import com.ehrbridge.gateway.entity.HospitalKeys;
import com.ehrbridge.gateway.repository.HospitalKeysRepository;
import com.ehrbridge.gateway.repository.HospitalRepository;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.ehrbridge.gateway.entity.Doctor;

import com.ehrbridge.gateway.entity.Role;
import com.ehrbridge.gateway.entity.User;
import com.ehrbridge.gateway.repository.DoctorRepository;
import com.ehrbridge.gateway.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final HospitalKeysRepository hospitalKeysRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final OtpService otpService;


    public ResponseEntity<RegisterReponse> register(RegisterRequest request) {
        String otp = otpService.generateOtp();
        Date otpValidity = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));


        var user = User
                .builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmailAddress())
                .password(passwordEncoder.encode(request.getPassword()))
                .address(request.getAddress())
                .gender(request.getGender())
                .phoneString(request.getPhoneString())
                .verified(false)
                .Otp(passwordEncoder.encode(otp))
                .otpValidity(otpValidity)
                .role(Role.USER)
                .build();

        try {
            userRepository.save(user);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<RegisterReponse>(RegisterReponse.builder().message("User Already exists").build(), HttpStatusCode.valueOf(400));
        }

        try {
            otpService.sendEmail(otp, user);
        } catch (Exception e) {
            return new ResponseEntity<RegisterReponse>(RegisterReponse.builder().message("Unable to send OTP").build(), HttpStatusCode.valueOf(500));
        }

        var jwtToken = jwtService.generateToken(user);

        return new ResponseEntity<RegisterReponse>(RegisterReponse.builder().message("OTP sent Successfully").token(jwtToken).build(), HttpStatusCode.valueOf(200));
    }

    public ResponseEntity<AuthResponse> authenticate(AuthRequest request)  {

        String otp = otpService.generateOtp();
        Date otpValidity = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        try{
            authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        }catch(Exception e){
            return new ResponseEntity<AuthResponse>(AuthResponse.builder().message("Login Failed").build(), HttpStatusCode.valueOf(403));
        }
        
        user.setOtp(passwordEncoder.encode(otp));  
        user.setOtpValidity(otpValidity);

        try {
            otpService.sendEmail(otp, user);
        } catch (Exception e) {
            return new ResponseEntity<AuthResponse>(AuthResponse.builder().message("Unable to send OTP").build(), HttpStatusCode.valueOf(500));
        }

        userRepository.save(user);
        
        var jwtToken = jwtService.generateToken(user);

        return new ResponseEntity<AuthResponse>(AuthResponse.builder().token(jwtToken).ehrbID(user.getEhrbID()).message("OTP sent Successfully").build(), HttpStatusCode.valueOf(200));

    }

    public ResponseEntity<DoctorRegisterResponse> registerDoctor(DoctorRegisterRequest request){
        var doctor = Doctor.builder()
                            .address(request.getAddress())
                            .department(request.getDepartment())
                            .gender(request.getGender())
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .email(request.getEmailAddress())
                            .build();
        try {
            doctorRepository.save(doctor);
        } catch (Exception e) {
            return new ResponseEntity<DoctorRegisterResponse>(DoctorRegisterResponse.builder().message("Doctor with the email Already exists").build(), HttpStatusCode.valueOf(400));
        }
        return new ResponseEntity<DoctorRegisterResponse>(DoctorRegisterResponse.builder().doctorEhrbID(doctor.getEhrbID()).message("Registration Successful").build(), HttpStatusCode.valueOf(200));

    }
    public ResponseEntity<HospitalRegisterResponse> registerHospital(HospitalRegisterRequest request) {
        var hospital = Hospital
                .builder()
                .hospitalName(request.getHospitalName())
                .emailAddress(request.getEmailAddress())
                .phone(request.getPhoneString())
                .address(request.getAddress())
                .hospitalLicense(request.getHospitalLicense())
                .hook_url(request.getHook_url())
                .build();

        try
        {
            hospitalRepository.save(hospital);
        }
        catch(Exception e){
            return new ResponseEntity<HospitalRegisterResponse>(HospitalRegisterResponse.builder().message("Hospital Already exists").build(), HttpStatusCode.valueOf(400));
        }


        String apiKey =  RandGeneratedStr(32);

        var hospital_key = HospitalKeys
                .builder()
                .hospitalId(hospital.getHospitalId())
                .validity(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(15)))
                .apiKey(apiKey)
                .build();

        hospitalKeysRepository.save(hospital_key);

        return new ResponseEntity<HospitalRegisterResponse>(HospitalRegisterResponse.builder().hospitalId(hospital.getHospitalId()).api_key(apiKey).message("Hospital Registered Successfully").build(), HttpStatusCode.valueOf(200));
    }

    String RandGeneratedStr(int l) {

        String AlphaNumericStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder s = new StringBuilder(l);
        int i;
        for ( i=0; i<l; i++) {
            int ch = (int)(AlphaNumericStr.length() * Math.random());
            s.append(AlphaNumericStr.charAt(ch));
        }
        return s.toString();

    }



}
