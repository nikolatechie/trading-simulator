package com.nikolagrujic.tradingsimulator.repository;

import com.nikolagrujic.tradingsimulator.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
    void deleteByEmail(String email);

    @Query("SELECT u.verificationDate FROM User u WHERE u.email = :email")
    LocalDate getVerificationDate(@Param("email") String email);
}