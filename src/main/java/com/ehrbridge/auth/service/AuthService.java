package com.ehrbridge.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ehrbridge.auth.dto.AuthRequest;
import com.ehrbridge.auth.dto.AuthResponse;
import com.ehrbridge.auth.dto.RegisterReponse;
import com.ehrbridge.auth.dto.RegisterRequest;
import com.ehrbridge.auth.entity.Role;
import com.ehrbridge.auth.entity.User;
import com.ehrbridge.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

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
    
    public RegisterReponse register(RegisterRequest request) {
        var user = User.builder()
                       .firstName(request.getFirstName())
                       .lastName(request.getLastName())
                       .email(request.getEmailAddress())
                       .password(passwordEncoder.encode(request.getPassword()))
                       .address(request.getAddress())
                       .gender(request.getGender())
                       .phoneString(request.getPhoneString())
                       .role(Role.USER)
                       .build();

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);

        return RegisterReponse.builder().token(jwtToken).ehrbid(user.getEhrbID()).build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()) 
        );

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder().token(jwtToken).build();

    }
        
}
