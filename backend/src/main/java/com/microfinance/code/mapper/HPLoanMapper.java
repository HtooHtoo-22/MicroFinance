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
        hpLoan.setTenor(dto.getTenor()); // Add this line to map tenor

        // Convert dates to String
        dto.setRegisteredDate(hpLoan.getRegisteredDate() != null ? hpLoan.getRegisteredDate().format(DATE_TIME_FORMATTER) : null);
        dto.setApprovedDate(hpLoan.getApprovedDate() != null ? hpLoan.getApprovedDate().format(DATE_TIME_FORMATTER) : null);
        dto.setEndDate(hpLoan.getEndDate() != null ? hpLoan.getEndDate().format(DATE_FORMATTER) : null);

        dto.setStatus(hpLoan.getStatus());
        dto.setDuration(hpLoan.getDuration());

        // Store only IDs for related entities
// Set user IDs and names
        dto.setEntryUserId(hpLoan.getEntryUser() != null ? hpLoan.getEntryUser().getId() : null);
        dto.setEntryUserName(hpLoan.getEntryUser() != null ? hpLoan.getEntryUser().getName() : null);
        dto.setApprovedUserId(hpLoan.getApprovedUser() != null ? hpLoan.getApprovedUser().getId() : null);
        dto.setApprovedUserName(hpLoan.getApprovedUser() != null ? hpLoan.getApprovedUser().getName() : null);
        dto.setCurrentAccountId(hpLoan.getCurrentAccount().getAccountId() != null ? hpLoan.getCurrentAccount().getAccountId() : null);
        dto.setCurrentAccountId(hpLoan.getCurrentAccount().getCif().getCifId() != null ? hpLoan.getCurrentAccount().getCif().getCifId() : null);
        dto.setBorrowerName(hpLoan.getCurrentAccount().getCif().getUserName());
        dto.setProductId(hpLoan.getProduct() != null ? hpLoan.getProduct().getId() : null);
        dto.setProductName(hpLoan.getProduct().getProductName());
        dto.setProductPhoto(hpLoan.getProduct().getPhoto());
        dto.setDownPaymentRate(hpLoan.getDownPaymentRate());
        dto.setDealerCommissionRate(hpLoan.getDealerCommissionRate());

        return dto;
    }

    public static HPLoan toEntity(HPLoanDTO dto) {
        if (dto == null) {
            return null;
        }

        HPLoan hpLoan = new HPLoan();
        hpLoan.setId(dto.getId());
        hpLoan.setLoanId(dto.getLoanId());
        hpLoan.setLoanAmount(dto.getLoanAmount());
        if (dto.getInterestRate() == null) {
            throw new IllegalArgumentException("Interest Rate cannot be null");
        }
        hpLoan.setInterestRate(dto.getInterestRate());
        hpLoan.setGracePeriod(dto.getGracePeriod());

        hpLoan.setRegisteredDate(dto.getRegisteredDate() != null ? LocalDateTime.parse(dto.getRegisteredDate(), DATE_TIME_FORMATTER) : null);
        hpLoan.setApprovedDate(dto.getApprovedDate() != null ? LocalDateTime.parse(dto.getApprovedDate(), DATE_TIME_FORMATTER) : null);
        hpLoan.setEndDate(dto.getEndDate() != null ? LocalDate.parse(dto.getEndDate(), DATE_FORMATTER) : null);
        hpLoan.setStatus(dto.getStatus());
        hpLoan.setDuration(dto.getDuration());

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
            currentAccount.setAccountId(dto.getCurrentAccountId()); // Already a String
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