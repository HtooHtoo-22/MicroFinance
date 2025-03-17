package com.microfinance.code.mapper;

import com.microfinance.code.dto.HPScheduleDTO;
import com.microfinance.code.model.HPSchedule;
import com.microfinance.code.status.RepaymentStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HPScheduleMapper {

    // Date formatter for converting LocalDate to String (e.g., "yyyy-MM-dd")
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Convert Entity to DTO
    public HPScheduleDTO toDTO(HPSchedule entity) {
        if (entity == null) {
            return null;
        }

        HPScheduleDTO dto = new HPScheduleDTO();
        dto.setId(entity.getId());
        dto.setDueDate(entity.getDueDate() != null ? entity.getDueDate().format(DATE_FORMATTER) : null);
        dto.setTotalDays(entity.getTotalDays());
        dto.setTermNumber(entity.getTermNumber());
        dto.setInstallment(entity.getInstallment());
        dto.setPrincipal(entity.getPrincipal());
        dto.setPrincipalOdAmount(entity.getPrincipalODAmount());
        dto.setInterestAmount(entity.getInterestAmount());
        dto.setInterestODAmount(entity.getInterestODAmount());
        dto.setTotalRepaidAmount(entity.getTotalRepaidAmount());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        dto.setGracePeriodEndDate(entity.getGracePeriodEndDate() != null ? entity.getGracePeriodEndDate().format(DATE_FORMATTER) : null);
        dto.setFullyPaidDate(entity.getFullyPaidDate() != null ? entity.getFullyPaidDate().format(DATE_FORMATTER) : null);
        dto.setLateFeeStatus(entity.isLateFeeStatus());
        dto.setHpLoanId(entity.getHpLoan() != null ? entity.getHpLoan().getId() : null);

        // If you want to include HPLoanDTO mapping, uncomment and implement the following:
        // if (entity.getHpLoan() != null) {
        //     HPLoanMapper hpLoanMapper = new HPLoanMapper(); // Assuming you have an HPLoanMapper
        //     dto.setHpLoanDTO(hpLoanMapper.toDTO(entity.getHpLoan()));
        // }

        return dto;
    }

    // Convert DTO to Entity
    public HPSchedule toEntity(HPScheduleDTO dto) {
        if (dto == null) {
            return null;
        }

        HPSchedule entity = new HPSchedule();
        entity.setId(dto.getId());
        entity.setDueDate(dto.getDueDate() != null ? LocalDate.parse(dto.getDueDate(), DATE_FORMATTER) : null);
        entity.setTotalDays(dto.getTotalDays());
        entity.setTermNumber(dto.getTermNumber());
        entity.setInstallment(dto.getInstallment());
        entity.setPrincipal(dto.getPrincipal());
        entity.setPrincipalODAmount(dto.getPrincipalOdAmount());
        entity.setInterestAmount(dto.getInterestAmount());
        entity.setInterestODAmount(dto.getInterestODAmount());
        entity.setTotalRepaidAmount(dto.getTotalRepaidAmount());
        entity.setStatus(dto.getStatus() != null ? RepaymentStatus.valueOf(dto.getStatus()) : null);
        entity.setGracePeriodEndDate(dto.getGracePeriodEndDate() != null ? LocalDate.parse(dto.getGracePeriodEndDate(), DATE_FORMATTER) : null);
        entity.setFullyPaidDate(dto.getFullyPaidDate() != null ? LocalDate.parse(dto.getFullyPaidDate(), DATE_FORMATTER) : null);
        entity.setLateFeeStatus(dto.isLateFeeStatus());

        // If hpLoanId is provided, you may need to fetch the HPLoan entity from the database
        // For now, we set only the ID; the actual HPLoan entity would need to be set elsewhere (e.g., in a service)
        if (dto.getHpLoanId() != null) {
            // Assuming HPLoan is fetched separately or set in the service layer
            // entity.setHpLoan(...);
        }

        // If you want to include HPLoanDTO mapping, uncomment and implement the following:
        // if (dto.getHpLoanDTO() != null) {
        //     HPLoanMapper hpLoanMapper = new HPLoanMapper(); // Assuming you have an HPLoanMapper
        //     entity.setHpLoan(hpLoanMapper.toEntity(dto.getHpLoanDTO()));
        // }

        return entity;
    }

    // Helper method to convert a list of entities to a list of DTOs
    public List<HPScheduleDTO> toDTOList(List<HPSchedule> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Helper method to convert a list of DTOs to a list of entities
    public List<HPSchedule> toEntityList(List<HPScheduleDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}