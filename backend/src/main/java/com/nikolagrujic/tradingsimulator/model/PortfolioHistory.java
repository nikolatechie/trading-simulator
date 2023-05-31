package com.nikolagrujic.tradingsimulator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "portfolio_history")
public class PortfolioHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    private BigDecimal totalValue;

    private LocalDate date;
}