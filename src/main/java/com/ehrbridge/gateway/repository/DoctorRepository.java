package com.ehrbridge.gateway.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ehrbridge.gateway.entity.Doctor;

import javax.print.Doc;

public interface DoctorRepository extends JpaRepository<Doctor, String> {
    Optional<Doctor> findByEmail(String email);

    Optional<Doctor> findById(String id);
}
