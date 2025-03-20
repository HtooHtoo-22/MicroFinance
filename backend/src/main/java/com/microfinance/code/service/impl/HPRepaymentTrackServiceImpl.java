package com.microfinance.code.service.impl;

import com.microfinance.code.dto.HPRepaymentTrackDTO;
import com.microfinance.code.dto.SMERepaymentTrackDTO;
import com.microfinance.code.model.*;
import com.microfinance.code.repository.*;
import com.microfinance.code.service.interFace.HPRepaymentTrackService;
import com.microfinance.code.status.RepaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
                dto.setStatus("Fully Paid");
            } else if (track.getRepayStatus() == RepaymentStatus.INTEREST_PAID_PRINCIPAL_OD) {
                dto.setStatus("Interest Paid, Principal Overdue");
            } else if (track.getRepayStatus() == RepaymentStatus.INTEREST_OD_PRINCIPAL_OD) {
                dto.setStatus("Partially Paid (Some Interest Paid), Interest and Principal Overdue");
            }

            dtoList.add(dto);
        }
        return null;
    }
}
