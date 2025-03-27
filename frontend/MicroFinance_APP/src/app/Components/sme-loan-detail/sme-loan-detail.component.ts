import { Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SmeLoanService } from '../../service/sme-loan.service';
import { Smeloan } from '../../model/SmeLoan';
import { ApiResponse } from '../../model/ApiResponse';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

@Component({
  selector: 'app-sme-loan-detail',
  standalone: false,
  templateUrl: './sme-loan-detail.component.html',
  styleUrl: './sme-loan-detail.component.css'
})
export class SmeLoanDetailComponent {
  statusStyles: { [key: string]: string } = {
    'Approve': 'bg-green-100 text-green-800',
    'Pending': 'bg-yellow-100 text-yellow-800',
    'Reject': 'bg-red-100 text-red-800'
  };

  loan : Smeloan | undefined;
  activeTab: 'details' | 'schedule' | 'track' | 'lateFee' = 'details';
  constructor(private route: ActivatedRoute, 
                public router: Router,
                private smeloanService : SmeLoanService
               ) {}
  // Component Code
ngOnInit() {
  const idParam = this.route.snapshot.paramMap.get('id');
  
  if (idParam) {
    console.log("SME Loan Detail id:", idParam);
    
    // Determine ID type and call appropriate service
    if (this.isValidNumber(idParam)) {
      // Handle numeric ID
      const numericId = Number(idParam);
      this.handleNumericId(numericId);
    } else {
      // Handle string ID
      this.handleStringId(idParam);
    }
  } else {
    console.error('No loan ID provided');
  }
}

private isValidNumber(id: string): boolean {
  return !isNaN(Number(id)) && isFinite(Number(id));
}

private handleNumericId(id: number): void {
  this.smeloanService.getLoanById(id).subscribe({
    next: (response) => this.handleResponse(response),
    error: (err) => this.handleError(err)
  });
}

private handleStringId(id: string): void {
  this.smeloanService.getLoanByLoanId(id).subscribe({
    next: (response) => this.handleResponse(response),
    error: (err) => this.handleError(err)
  });
}

private handleResponse(response: ApiResponse<Smeloan>): void {
  if (response.data) {
    this.loan = response.data;
    console.log('Loan data:', this.loan);
  } else {
    console.error('Loan not found');
    // Optional: Navigate to error page or show message
  }
}

private handleError(err: any): void {
  console.error('API Error:', err);
  // Optional: Show error message to user
}
  viewCifDetail(){

  }
  viewAccountDetail(){
    
  }
  viewCollateralDetail(collateralId:number){
    if (collateralId) {
      this.router.navigate(['/entry-dashboard/collateralDetail', collateralId]);
      console.log(collateralId);
      
    } else {
      console.warn('Collateral ID is missing');
    }
  }
  switchTab(tab: 'details' | 'schedule') {
    this.activeTab = tab;
  }


  getTotalCollateral(): number {
    let total = 0;
  
    if (this.loan?.usedCollaterals) {
      this.loan.usedCollaterals.forEach((item, index) => {
        console.log(`Collateral ${index + 1}:`, item);
        total += Number(item?.usedValue) || 0;
        console.log(`UsedValue of collateral ${index + 1}: `, item?.usedValue);
        console.log("Total Collaterals:", this.loan?.usedCollaterals?.length);
  
      });
  
    }
    console.log("Final Total Collateral: ", total);
  
    return total;
  }
  

  downloadSmeReport(): void {
    if (!this.loan) {
      console.error('No loan selected.');
      return;
    }
  
    const doc = new jsPDF({
      format: [148, 148], // Shorter page
      orientation: 'portrait'
    });
  
    const pageWidth = doc.internal.pageSize.getWidth();
    const margin = 8;
    let yPos = 10; // Adjusted start position for better spacing
  
    // Define Colors
    const primaryColor: [number, number, number] = [0, 102, 204];  // Blue
    const grayColor: [number, number, number] = [100, 100, 100];   // Dark Gray
    const lightGray: [number, number, number] = [240, 240, 240];   // Light Gray
  
    // Set Font
    doc.setFont('helvetica', 'normal');
  
    // Header Box (like Modal)
    doc.setFillColor(...lightGray);
    doc.rect(margin, yPos - 4, pageWidth - margin * 2, 10, 'F');
    doc.setFontSize(13);
    doc.setFont('helvetica', 'bold');
    doc.setTextColor(...primaryColor);
    doc.text('SME Loan Voucher', margin + 40, yPos + 2);
  
    doc.setFontSize(10);
    doc.setTextColor(...grayColor);
    yPos += 12;
    doc.text(`ID: ${this.loan.loanId}`, margin, yPos);
    yPos += 8;
  
    // Borrower Row - Closer Spacing
    doc.setFontSize(11);
    doc.setTextColor(0);
    doc.text('Borrower:', margin, yPos);
    const borrowerName = this.loan.borrowerName || '';
    doc.text(borrowerName, margin + 25, yPos); // Name closer to label
  
    // Line Below Borrower
    yPos += 5;
    doc.setDrawColor(180, 180, 180);
    doc.line(margin, yPos, pageWidth - margin, yPos);
    yPos += 6;
  
    // Key Numbers Grid
    const colWidth = (pageWidth - margin * 2) / 3;
    const centerX = margin + colWidth + 8;
    const rightX = margin + colWidth * 2+ 8;
  
    doc.setFontSize(10);
    doc.setTextColor(...grayColor);
    doc.text('Amount', margin, yPos);
    doc.text('Rate', centerX, yPos);
    doc.text('Term', rightX, yPos);
    doc.setTextColor(0);
  
    doc.setFont('helvetica', 'bold');
    doc.text(`${this.loan.loanAmount?.toLocaleString()} KS`, margin, yPos + 5);
    doc.text(`${this.loan.interestRate}%`, centerX, yPos + 5);
    doc.text(`${this.loan.duration} months`, rightX, yPos + 5);
    yPos += 10;
  
    // Collateral Summary (box)
    doc.setFillColor(...lightGray);
    doc.rect(margin, yPos, pageWidth - margin * 2, 8, 'F');
    doc.setTextColor(0);
    doc.text('Total Collateral:', margin + 2, yPos + 5);
    const collateral = this.getTotalCollateral().toLocaleString() + ' KS';
    doc.text(collateral, pageWidth - margin - doc.getTextWidth(collateral) , yPos + 5);
    yPos += 12;
  
    // Dates Section
    doc.setFontSize(10);
    doc.setTextColor(...grayColor);
    doc.text('Start Date', margin, yPos);
    doc.text('Maturity Date', pageWidth - margin - doc.getTextWidth('Maturity Date'), yPos);
  
    doc.setTextColor(0);
    const startDate = new Date(this.loan.approvedDate).toLocaleDateString('en-GB', {
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    });
    const endDate = this.loan.expiredDate 
      ? new Date(this.loan.expiredDate).toLocaleDateString('en-GB', {
          day: '2-digit',
          month: 'short',
          year: 'numeric'
        })
      : 'N/A';
  
    doc.text(startDate, margin, yPos + 5);
    doc.text(endDate, pageWidth - margin - doc.getTextWidth(endDate), yPos + 5);
  
    
    // Save PDF
    const fileName = `Loan_Approval_${this.loan.loanId}.pdf`;
    doc.save(fileName);
  }
  
  
}