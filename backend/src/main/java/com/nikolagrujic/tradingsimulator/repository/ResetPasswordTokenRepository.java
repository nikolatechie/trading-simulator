package com.nikolagrujic.tradingsimulator.repository;

import com.nikolagrujic.tradingsimulator.model.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {
    ResetPasswordToken findByToken(String token);
}