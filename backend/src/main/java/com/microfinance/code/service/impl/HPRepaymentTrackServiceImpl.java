package com.microfinance.code.service.impl;

import com.microfinance.code.dto.HPRepaymentTrackDTO;
import com.microfinance.code.dto.SMERepaymentTrackDTO;
import com.microfinance.code.model.*;
import com.microfinance.code.repository.*;
import com.microfinance.code.service.interFace.HPRepaymentTrackService;
import com.microfinance.code.status.RepaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class HPRepaymentTrackServiceImpl implements HPRepaymentTrackService {
    @Autowired
    private HPRepaymentTrackRepo repaymentTrackRepo;

    @Autowired
    private HPODRepaymentTrackRepo odRepayTrackRepo;

    @Autowired
    private HPLateFeeTrackingRepo lateFeeTrackingRepo;
    @Override
    public List<HPRepaymentTrackDTO> getTrackListByLoanId(Integer loanId){
        List<HPRepaymentTrackDTO> dtoList = new ArrayList<>();
        List<HPRepaymentTrack> repaymentTracks =repaymentTrackRepo.findByHpSchedule_HpLoan_Id(loanId);
        List<HPODRepaymentTrack> odRepaymentTracks = odRepayTrackRepo.findByHpRepaymentSchedule_HpLoan_Id(loanId);
        List<HPLateFeeTracking> lateFeeTrackings = lateFeeTrackingRepo.findByHpLoanId(loanId);
        // 1. Normal Repayment Tracks
        for (HPRepaymentTrack track : repaymentTracks) {
            HPRepaymentTrackDTO dto = new HPRepaymentTrackDTO();
            dto.setPaymentDate(track.getDate());
            dto.setPaymentAmount(track.getPaidAmount());
            dto.setTerm(track.getHpSchedule().getTermNumber());
            dto.setPaymentPurpose("Normal Repayment");

            if (track.getRepayStatus() == RepaymentStatus.ALL_PAID) {
                dto.setStatus("Fully Paid For Normal");
            } else if (track.getRepayStatus() == RepaymentStatus.INTEREST_PAID_PRINCIPAL_OD) {
                dto.setStatus("Interest Paid, Principal Overdue For Normal");
            } else if (track.getRepayStatus() == RepaymentStatus.INTEREST_OD_PRINCIPAL_OD) {
                dto.setStatus("Partially Paid, Interest and Principal Overdue For Normal");
            }

            dtoList.add(dto);
        }
        // 2. OD Repayment Tracks


        Map<String, List<HPODRepaymentTrack>> groupedMap = new HashMap<>();

// Group by scheduleId + date
        for (HPODRepaymentTrack odTrack : odRepaymentTracks) {
            String key = odTrack.getHpRepaymentSchedule().getId() + "_" + odTrack.getDate().toLocalDate();
            groupedMap.computeIfAbsent(key, k -> new ArrayList<>()).add(odTrack);
        }



        for (Map.Entry<String, List<HPODRepaymentTrack>> entry : groupedMap.entrySet()) {
            List<HPODRepaymentTrack> group = entry.getValue();

            HPRepaymentTrackDTO dto = new HPRepaymentTrackDTO();

            // Sum total payment
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (HPODRepaymentTrack track : group) {
                if (track.getPaidInterestODAmount() != null) {
                    totalAmount = totalAmount.add(track.getPaidInterestODAmount());
                }
                if (track.getPaidPrincipalODAmount() != null) {
                    totalAmount = totalAmount.add(track.getPaidPrincipalODAmount());
                }
            }

            // Get latest row for status (you can customize how "latest" is defined)
            HPODRepaymentTrack latest = group.get(group.size() - 1); // or use max by ID

            dto.setPaymentAmount(totalAmount);
            dto.setPaymentDate(latest.getDate().toLocalDate());
            dto.setPaymentPurpose("OD Repayment"); // or custom if needed
            dto.setTerm(latest.getHpRepaymentSchedule().getTermNumber()); // assuming term is in HPSchedule
            dto.setStatus(latest.getRepayStatus().getDisplayName());

            dtoList.add(dto);
        }

        for (HPLateFeeTracking lateFee : lateFeeTrackings) {
            HPRepaymentTrackDTO dto = new HPRepaymentTrackDTO();
            dto.setPaymentDate(lateFee.getLateFeeRepaidDate());
            dto.setPaymentAmount(lateFee.getTotalLateFees());
            dto.setTerm(0); // optional or you can skip term field for this
            dto.setPaymentPurpose("Late Fee Repayment");
            dto.setStatus("Late Fee Paid for " + lateFee.getLateDays() + " Day(s)");
            dto.setLateDays(lateFee.getLateDays());
            dto.setLateFees(lateFee.getTotalLateFees());
            dtoList.add(dto);
        }
        dtoList.sort(Comparator.comparing(HPRepaymentTrackDTO::getPaymentDate));
        return dtoList;
    }
}
