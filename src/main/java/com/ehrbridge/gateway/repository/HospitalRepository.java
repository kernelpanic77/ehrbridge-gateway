package com.ehrbridge.gateway.repository;

import com.ehrbridge.gateway.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital, String> {
    Optional<Hospital> findByHospitalId(String hospitalId);
}
