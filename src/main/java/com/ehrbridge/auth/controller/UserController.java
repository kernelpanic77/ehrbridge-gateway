package com.ehrbridge.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ehrbridge.auth.repository.UserRepository;
import com.ehrbridge.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

import com.ehrbridge.auth.dto.AuthRequest;
import com.ehrbridge.auth.dto.AuthResponse;
import com.ehrbridge.auth.dto.RegisterReponse;
import com.ehrbridge.auth.dto.RegisterRequest;
import com.ehrbridge.auth.entity.User;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    @Autowired 
    private AuthService service;

    
    @PostMapping("/register/user")
    public ResponseEntity<RegisterReponse> registerUser(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/signin/user")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody AuthRequest request){
        return ResponseEntity.ok(service.authenticate(request));
    }
    
}
