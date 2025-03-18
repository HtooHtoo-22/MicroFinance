package com.microfinance.code.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SMERepaymentTrackDTO {
    private LocalDate paymentDate;
    private BigDecimal paymentAmount;
    private String paymentPurpose; // or paymentFor
    private int term;
    private String status;
    private Integer lateDays;
    private BigDecimal lateFees;
}
