package com.nikolagrujic.tradingsimulator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table
public class StockInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    @Column(unique = true)
    private String symbol;

    @NotNull
    @NotBlank
    private String country;

    @NotNull
    @NotBlank
    private String currency;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    @JsonProperty("mic_code")
    private String micCode;

    @NotNull
    @NotBlank
    private String exchange;
}