import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HPLoan, HpLoanService } from '../../../service/hp-loan.service';
import { ApiResponse } from '../../../model/ApiResponse';
import jsPDF from 'jspdf';
import { HPSchedule } from '../../../model/HPSchedule';
import { HpLoanSchduleService } from '../../../service/hp-loan-schdule.service';

@Component({
  selector: 'app-hp-loan-detail',
  standalone: false,
  templateUrl: './hp-loan-detail.component.html',
  styleUrl: './hp-loan-detail.component.css'
})
export class HpLoanDetailComponent implements OnInit {
  loan: HPLoan | null = null;
  loans: HPSchedule | null = null;
  errorMessage: string | null = null;
  activeTab: 'details' | 'schedule'| 'track' | 'lateFee' = 'details';
  statusStyles: { [key: string]: string } = {
    PENDING: 'bg-yellow-100 text-yellow-800',
    APPROVE: 'bg-green-100 text-green-800',
    REJECT: 'bg-red-100 text-red-800',
    default: 'bg-gray-100 text-gray-800'
  };

  constructor(
    private route: ActivatedRoute,
    private hpLoanService: HpLoanService,
    private router: Router,
    private hpLoanScheduleService : HpLoanSchduleService
  ) {}

  ngOnInit(): void {
    const loanId = this.route.snapshot.paramMap.get('id');
    if (loanId) {
      this.fetchLoanDetails(+loanId);
    } else {
      this.errorMessage = 'Invalid loan ID.';
    }
  }

  fetchLoanDetails(loanId: number): void {
    this.hpLoanService.getHPLoanById(loanId).subscribe({
      next: (response: ApiResponse<HPLoan>) => {
        this.loan = response.data;
        this.errorMessage = null;
      },
      error: (error) => {
        console.error('Error fetching loan details:', error);
        this.errorMessage = 'Failed to load loan details. Please try again later.';
      }
    });
  }


  switchTab(tab: 'details' | 'schedule' | 'track' | 'lateFee'): void {
    this.activeTab = tab;
  }

  repaymentSchedule(loanId: number): void {
    this.switchTab('schedule');
  }

  viewCifDetail(): void {
    // Implement navigation or logic to view CIF details
    console.log('View CIF Detail:', this.loan?.currentAccountId);
  }

  viewAccountDetail(): void {
    // Implement navigation or logic to view account details
    console.log('View Account Detail:', this.loan?.currentAccountId);
  }

  downloadhpReport(): void {
    if (!this.loan) {
      console.error('No loan selected.');
      return;
    }
  
    const doc = new jsPDF({
      format: [148, 210], // Slightly taller page for HP loans
      orientation: 'portrait'
    });
  
    const pageWidth = doc.internal.pageSize.getWidth();
    const margin = 8;
    let yPos = 10;
  
    // Define Colors
    const primaryColor: [number, number, number] = [0, 102, 204];  // Blue
    const grayColor: [number, number, number] = [100, 100, 100];   // Dark Gray
    const lightGray: [number, number, number] = [240, 240, 240];   // Light Gray
  
    // Set Font
    doc.setFont('helvetica', 'normal');
  
    // Header Box
    doc.setFillColor(...lightGray);
    doc.rect(margin, yPos - 4, pageWidth - margin * 2, 10, 'F');
    doc.setFontSize(13);
    doc.setFont('helvetica', 'bold');
    doc.setTextColor(...primaryColor);
    doc.text('HP Loan Voucher', margin + 40, yPos + 2);
  
    doc.setFontSize(10);
    doc.setTextColor(...grayColor);
    yPos += 12;
    doc.text(`Loan ID: ${this.loan.id}`, margin, yPos);
    yPos += 8;
  
    // Borrower Row
    doc.setFontSize(11);
    doc.setTextColor(0);
    doc.text('Borrower:', margin, yPos);
    const borrowerName = this.loan.borrowerName || 'N/A'; 
    doc.text(borrowerName, margin + 25, yPos);
  
    // Line Below Borrower
    yPos += 5;
    doc.setDrawColor(180, 180, 180);
    doc.line(margin, yPos, pageWidth - margin, yPos);
    yPos += 6;
  
    // Key Numbers Grid
    const colWidth = (pageWidth - margin * 2) / 3;
    const centerX = margin + colWidth + 8;
    const rightX = margin + colWidth * 2 + 8;
  
    doc.setFontSize(10);
    doc.setTextColor(...grayColor);
    doc.text('Principal', margin, yPos);
    doc.text('Interest', centerX, yPos);
    doc.text('Term', rightX, yPos);
    doc.setTextColor(0);
  
    doc.setFont('helvetica', 'bold');
    doc.text(`${this.loan.loanAmount.toLocaleString() || '0'} KS`, margin, yPos + 5);
    doc.text(`${this.loan.interestRate || '0'}%`, centerX, yPos + 5);
    doc.text(`${this.loan.duration || '0'} months`, rightX, yPos + 5);
    yPos += 10;
  
    // Repayment Summary (box)
  
    // Dates Section
    doc.setFontSize(10);
    doc.setTextColor(...grayColor);
    doc.text('Start Date', margin, yPos);
    doc.text('Maturity Date', pageWidth - margin - doc.getTextWidth('Maturity Date'), yPos);
  
    doc.setTextColor(0);
    const startDate = this.loan.approvedDate
      ? new Date(this.loan.approvedDate).toLocaleDateString('en-GB', {
          day: '2-digit',
          month: 'short',
          year: 'numeric'
        })
      : 'N/A';
      
    const endDate = this.loan.endDate 
      ? new Date(this.loan.endDate).toLocaleDateString('en-GB', {
          day: '2-digit',
          month: 'short',
          year: 'numeric'
        })
      : 'N/A';
  
    doc.text(startDate, margin, yPos + 5);
    doc.text(endDate, pageWidth - margin - doc.getTextWidth(endDate), yPos + 5);
    yPos += 10;
  
    // Save PDF
    const fileName = `HP_Loan_Voucher_${this.loan.id}.pdf`;
    doc.save(fileName);
  }
}