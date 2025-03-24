// WebSocketNotificationService.java
package com.microfinance.code.service;

import com.microfinance.code.dto.DealerDTO;
import com.microfinance.code.dto.HPLoanDTO;
import com.microfinance.code.dto.SMELoanDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyNewDealer(DealerDTO dealer) {
        messagingTemplate.convertAndSend("/topic/dealers", dealer);
    }

    public void notifyDealerStatusChange(DealerDTO dealer) {
        messagingTemplate.convertAndSend("/topic/dealer-status", dealer);
    }

    public void notifyNewHPLoan(HPLoanDTO loan) {
        messagingTemplate.convertAndSend("/topic/hp-loans", loan);
    }

    public void notifyHPLoanStatusChange(HPLoanDTO loan) {
        messagingTemplate.convertAndSend("/topic/hp-loan-status", loan);
    }

    public void notifyNewSMELoan(SMELoanDTO loan){
        messagingTemplate.convertAndSend("/topic/sme-loans", loan);
    }

    public void notifySMELoanStatusChange(SMELoanDTO loan){
        messagingTemplate.convertAndSend("/topic/sme-loan-status", loan);
    }
}