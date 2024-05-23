package com.nikolagrujic.tradingsimulator.repository;

import com.nikolagrujic.tradingsimulator.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    EmailVerificationToken findByToken(String token);
    List<EmailVerificationToken> findAllByExpiryDateTimeBefore(LocalDateTime dateTime);
    void deleteByUser_Id(Long user_id);
}