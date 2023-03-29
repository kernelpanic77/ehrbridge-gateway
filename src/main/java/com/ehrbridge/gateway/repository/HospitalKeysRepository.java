package com.ehrbridge.gateway.repository;

import com.ehrbridge.gateway.entity.HospitalKeys;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HospitalKeysRepository extends JpaRepository<HospitalKeys, String> {
    Optional<HospitalKeys> getHospitalKeysByApiKey(String apiKey);
}
