package com.nikolagrujic.tradingsimulator.model;

import com.nikolagrujic.tradingsimulator.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockHolding> stockHoldings;

    @OneToMany(mappedBy = "portfolio", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PortfolioHistory> history = new ArrayList<>();

    @Column(columnDefinition = "decimal(19, 2) default 30000.00")
    private BigDecimal cash = BigDecimal.valueOf(Constants.STARTING_CASH_BALANCE);

    @Column
    private boolean locked = false;
}