package com.ehrbridge.gateway.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ehrbridge.gateway.entity.User;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserRepository extends JpaRepository<User, String>{

    Optional<User> findByEmail(String email);
}
