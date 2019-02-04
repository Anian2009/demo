package com.example.demo.repository;

import com.example.demo.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByToken (String token);
    Users findByActivationCode(String activationCode);
    Users findByEmail(String email);
}
