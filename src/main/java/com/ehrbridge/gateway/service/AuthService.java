package com.ehrbridge.gateway.service;

import com.ehrbridge.gateway.dto.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ehrbridge.gateway.entity.Role;
import com.ehrbridge.gateway.entity.User;
import com.ehrbridge.gateway.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authManager;

    private final OtpService otpService;

    public RegisterReponse register(RegisterRequest request) throws MessagingException, UnsupportedEncodingException {
        String otp = otpService.generateOtp();
        Date otpValidity = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
        System.out.println(otpValidity);
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

        userRepository.save(user);

        otpService.sendEmail(otp, user);

        var jwtToken = jwtService.generateToken(user);
//
//        return RegisterReponse.builder().token(jwtToken).ehrbid(user.getEhrbID()).build();
        return RegisterReponse.builder().message("OTP sent Successfully").token(jwtToken).build();
    }

    public AuthResponse authenticate(AuthRequest request) throws UnsupportedEncodingException, MessagingException {

        String otp = otpService.generateOtp();
        Date otpValidity = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        System.out.println(user.getEmail());

        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        
        user.setOtp(passwordEncoder.encode(otp));  
        user.setOtpValidity(otpValidity);
        otpService.sendEmail(otp, user);

        userRepository.save(user);
        
        var jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder().token(jwtToken).ehrbID(user.getEhrbID()).message("OTP sent Successfully").build();

    }

    public VerifyOtpResponse updateResponse(VerifyOtpResponse verifyOtpResponse) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("The email does not exist"));

        user.setVerified(true);

        userRepository.save(user);


        verifyOtpResponse.setEhrbid(user.getEhrbID());
        verifyOtpResponse.setToken(jwtService.generateToken(user));

        return verifyOtpResponse;
    }

    public AuthPatientServerResponse authenticatePatient(AuthRequest request) {
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        userRepository.save(user);
        
        var jwtToken = jwtService.generateToken(user);

        return AuthPatientServerResponse.builder().user(user).token(jwtToken).message("Patient Authenticated").build();
    }

}
