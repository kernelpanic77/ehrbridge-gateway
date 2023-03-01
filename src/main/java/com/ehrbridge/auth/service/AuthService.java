package com.ehrbridge.auth.service;

import com.ehrbridge.auth.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ehrbridge.auth.entity.Role;
import com.ehrbridge.auth.entity.User;
import com.ehrbridge.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final JwtService jwtService;

    private final AuthenticationManager authManager;

    private final OtpService otpService;
    
    public RegisterReponse register(RegisterRequest request) throws MessagingException, UnsupportedEncodingException {
        String otp = otpService.generateOtp();
        System.out.println(otp);
        Date otpValidity = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
        var user = User.builder()
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

    public AuthResponse authenticate(AuthRequest request) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()) 
        );

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder().token(jwtToken).build();

    }

    public VerifyOtpResponse updateResponse(VerifyOtpResponse verifyOtpResponse)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("The email does not exist"));

        verifyOtpResponse.setEhrbid(user.getEhrbID());
        verifyOtpResponse.setToken(jwtService.generateToken(user));

        return verifyOtpResponse;
    }
        
}
