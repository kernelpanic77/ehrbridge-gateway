package com.ehrbridge.gateway.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hospitals")
@Builder
public class Hospital  {
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;
    private String hospitalName;
    private String emailAddress;
    private String phone;
    private String address;
    private String hospitalLicense;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String hospitalId;

    private String hook_url;


}
