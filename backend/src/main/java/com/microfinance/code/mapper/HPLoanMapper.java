package com.microfinance.code.mapper;

import com.microfinance.code.dto.HPLoanDTO;
import com.microfinance.code.model.HPLoan;
import com.microfinance.code.model.CurrentAccount;
import com.microfinance.code.model.Product;
import com.microfinance.code.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HPLoanMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Convert Entity to DTO
    public static HPLoanDTO toDTO(HPLoan hpLoan) {
        if (hpLoan == null) {
            return null;
        }

        HPLoanDTO dto = new HPLoanDTO();
        dto.setId(hpLoan.getId());
        dto.setLoanId(hpLoan.getLoanId());
        dto.setLoanAmount(hpLoan.getLoanAmount());
        dto.setInterestRate(hpLoan.getInterestRate());
        dto.setGracePeriod(hpLoan.getGracePeriod());

        // Convert dates to String
        dto.setRegisteredDate(hpLoan.getRegisteredDate() != null ? hpLoan.getRegisteredDate().format(DATE_TIME_FORMATTER) : null);
        dto.setApprovedDate(hpLoan.getApprovedDate() != null ? hpLoan.getApprovedDate().format(DATE_TIME_FORMATTER) : null);
        dto.setEndDate(hpLoan.getEndDate() != null ? hpLoan.getEndDate().format(DATE_FORMATTER) : null);

        dto.setStatus(hpLoan.getStatus());
        dto.setDuration(hpLoan.getDuration());

        // Store only IDs for related entities
        dto.setEntryUserId(hpLoan.getEntryUser() != null ? hpLoan.getEntryUser().getId() : null);
        dto.setApprovedUserId(hpLoan.getApprovedUser() != null ? hpLoan.getApprovedUser().getId() : null);
        dto.setCurrentAccountId(hpLoan.getCurrentAccount() != null ? hpLoan.getCurrentAccount().getId() : null);
        dto.setProductId(hpLoan.getProduct() != null ? hpLoan.getProduct().getId() : null);

        dto.setDownPaymentRate(hpLoan.getDownPaymentRate());
        dto.setDealerCommissionRate(hpLoan.getDealerCommissionRate());

        return dto;
    }

    // Convert DTO to Entity
    public static HPLoan toEntity(HPLoanDTO dto) {
        if (dto == null) {
            return null;
        }

        HPLoan hpLoan = new HPLoan();
        hpLoan.setId(dto.getId());
        hpLoan.setLoanId(dto.getLoanId());
        hpLoan.setLoanAmount(dto.getLoanAmount());
        hpLoan.setInterestRate(dto.getInterestRate());
        hpLoan.setGracePeriod(dto.getGracePeriod());

        // Convert String to LocalDateTime / LocalDate
        hpLoan.setRegisteredDate(dto.getRegisteredDate() != null ? LocalDateTime.parse(dto.getRegisteredDate(), DATE_TIME_FORMATTER) : null);
        hpLoan.setApprovedDate(dto.getApprovedDate() != null ? LocalDateTime.parse(dto.getApprovedDate(), DATE_TIME_FORMATTER) : null);
        hpLoan.setEndDate(dto.getEndDate() != null ? LocalDate.parse(dto.getEndDate(), DATE_FORMATTER) : null);
        hpLoan.setStatus(dto.getStatus());
        hpLoan.setDuration(dto.getDuration());

        // Set related entity references using only IDs
        if (dto.getEntryUserId() != null) {
            User entryUser = new User();
            entryUser.setId(dto.getEntryUserId());
            hpLoan.setEntryUser(entryUser);
        }

        if (dto.getApprovedUserId() != null) {
            User approvedUser = new User();
            approvedUser.setId(dto.getApprovedUserId());
            hpLoan.setApprovedUser(approvedUser);
        }

        if (dto.getCurrentAccountId() != null) {
            CurrentAccount currentAccount = new CurrentAccount();
            currentAccount.setId(dto.getCurrentAccountId());
            hpLoan.setCurrentAccount(currentAccount);
        }

        if (dto.getProductId() != null) {
            Product product = new Product();
            product.setId(dto.getProductId());
            hpLoan.setProduct(product);
        }

        hpLoan.setDownPaymentRate(dto.getDownPaymentRate());
        hpLoan.setDealerCommissionRate(dto.getDealerCommissionRate());

        return hpLoan;
    }
}
