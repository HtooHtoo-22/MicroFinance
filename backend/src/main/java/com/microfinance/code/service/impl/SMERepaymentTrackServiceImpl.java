package com.microfinance.code.service.impl;

import com.microfinance.code.dto.SMERepaymentTrackDTO;
import com.microfinance.code.model.SMELateFeeTracking;
import com.microfinance.code.model.SMEODRepaymentTrack;
import com.microfinance.code.model.SMERepaymentTrack;
import com.microfinance.code.repository.SMELateFeeTrackingRepo;
import com.microfinance.code.repository.SMEODRepaymentTrackRepo;
import com.microfinance.code.repository.SMERepaymentTrackRepo;
import com.microfinance.code.service.interFace.SMERepaymentTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

@Service
public class SMERepaymentTrackServiceImpl implements SMERepaymentTrackService {

    @Autowired
    private SMERepaymentTrackRepo repaymentTrackRepo;

    @Autowired
    private SMEODRepaymentTrackRepo odRepayTrackRepo;

    @Autowired
    private SMELateFeeTrackingRepo lateFeeTrackingRepo;
    public List<SMERepaymentTrackDTO> getTrackListByLoanId(Integer loanId){

        List<SMERepaymentTrackDTO> dtoList = new ArrayList<>();

        List<SMERepaymentTrack> repaymentTracks =repaymentTrackRepo.findBySmeRepaymentSchedule_SmeLoan_Id(loanId);
        List<SMEODRepaymentTrack> odRepaymentTracks = odRepayTrackRepo.findBySmeRepaymentSchedule_SmeLoan_Id(loanId);
        List<SMELateFeeTracking> lateFeeTrackings = lateFeeTrackingRepo.findBySmeLoanId(loanId);
        // 1. Normal Repayment Tracks
        for (SMERepaymentTrack track : repaymentTracks) {
            SMERepaymentTrackDTO dto = new SMERepaymentTrackDTO();
            dto.setPaymentDate(track.getDate());
            dto.setPaymentAmount(track.getPaidAmount());
            dto.setTerm(track.getSmeRepaymentSchedule().getTermNumber());
            dto.setPaymentPurpose("Normal Repayment");

            if (track.isOdStatus()) {
                dto.setStatus("Partial Repayment with OD Occurred");
            } else {
                dto.setStatus("Repayment Paid Successfully");
            }

            dtoList.add(dto);
        }

        // 2. OD Repayment Tracks
        for (SMEODRepaymentTrack odTrack : odRepaymentTracks) {
            SMERepaymentTrackDTO dto = new SMERepaymentTrackDTO();
            dto.setPaymentDate(odTrack.getDate().toLocalDate());
            dto.setPaymentAmount(odTrack.getPaid_od_amount());
            dto.setTerm(odTrack.getSmeRepaymentSchedule().getTermNumber());
            dto.setPaymentPurpose("OD Repayment");


            if (odTrack.isOdEndStatus()) {
                dto.setStatus("OD Amount Paid Successfully");
            } else {
                dto.setStatus("OD Amount Remaining");
            }

            dtoList.add(dto);
        }

        // 3. Late Fee Tracking
        for (SMELateFeeTracking lateFee : lateFeeTrackings) {
            SMERepaymentTrackDTO dto = new SMERepaymentTrackDTO();
            dto.setPaymentDate(lateFee.getLateFeeRepaidDate());
            dto.setPaymentAmount(lateFee.getTotalLateFees());
            dto.setTerm(0); // optional or you can skip term field for this
            dto.setPaymentPurpose("Late Fee Repayment");
            dto.setStatus("Late Fee Paid for " + lateFee.getLateDays() + " Day(s)");
            dto.setLateDays(lateFee.getLateDays());
            dto.setLateFees(lateFee.getTotalLateFees());
            dtoList.add(dto);
        }
        dtoList.sort(Comparator.comparing(SMERepaymentTrackDTO::getPaymentDate));
        return dtoList;
    }


}
