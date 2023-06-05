package com.nikolagrujic.tradingsimulator.repository;

import com.nikolagrujic.tradingsimulator.model.StockInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface StockInfoRepository extends JpaRepository<StockInfo,Long> {
    boolean existsBySymbol(String symbol);
    StockInfo getBySymbol(String symbol);
    Page<StockInfo> findAll(Pageable pageable);

    @Query("SELECT s.currentPrice FROM StockInfo s WHERE s.symbol = :symbol")
    BigDecimal getPriceBySymbol(@Param("symbol") String symbol);

    @Query("SELECT s FROM StockInfo s " +
            "WHERE lower(s.symbol) LIKE lower(concat('%', :search, '%')) " +
            "OR lower(s.name) LIKE lower(concat('%', :search, '%')) " +
            "ORDER BY CASE "
            + "WHEN lower(s.symbol) = lower(:search) THEN 0 "
            + "WHEN lower(s.symbol) LIKE lower(concat(:search, '%')) THEN 1 "
            + "WHEN lower(s.symbol) LIKE lower(concat('%', :search, '%')) THEN 2 "
            + "WHEN lower(s.symbol) LIKE lower(concat('%', :search)) THEN 3 "
            + "WHEN lower(s.name) LIKE lower(concat(:search, '%')) THEN 4 "
            + "WHEN lower(s.name) LIKE lower(concat('%', :search, '%')) THEN 5 "
            + "WHEN lower(s.name) LIKE lower(concat('%', :search)) THEN 6 "
            + "ELSE 7 END, s.symbol ASC")
    Page<StockInfo> findAll(
        @Param("search") String search,
        Pageable pageable
    );

    @Query("SELECT s.lastUpdated FROM StockInfo s WHERE s.symbol = :symbol")
    LocalDateTime getUpdateDateTimeBySymbol(@Param("symbol") String symbol);

    @Transactional
    @Modifying
    @Query("UPDATE StockInfo s SET s.currentPrice = :currentPrice, s.lastUpdated = :dateTime WHERE s.symbol = :symbol")
    void updateCurrentPrice(
        @Param("symbol") String symbol,
        @Param("currentPrice") BigDecimal currentPrice,
        @Param("dateTime") LocalDateTime dateTime
    );
}