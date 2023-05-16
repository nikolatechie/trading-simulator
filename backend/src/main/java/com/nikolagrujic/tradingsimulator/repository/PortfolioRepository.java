package com.nikolagrujic.tradingsimulator.repository;

import com.nikolagrujic.tradingsimulator.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio,Long> {
    Portfolio getByUser_Email(String email);
    Portfolio getByUser_EmailAndLocked(String user_email, boolean locked);
}