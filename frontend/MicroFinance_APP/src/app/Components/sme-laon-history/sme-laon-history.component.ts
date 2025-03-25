import { Component } from '@angular/core';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { SmeLoanService } from '../../service/sme-loan.service';
import { AuthService } from '../../service/auth.service';
import { Smeloan } from '../../model/SmeLoan';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sme-laon-history',
  standalone: false,
  templateUrl: './sme-laon-history.component.html',
  styleUrl: './sme-laon-history.component.css'
})
export class SmeLaonHistoryComponent {
  loans: Smeloan[] = [];
  errorMessage: string | null = null;
  loading: boolean = false;
  currentPage: number = 1;
  itemsPerPage: number = 7;
  totalPages: number = 0;
  selectedStatus: string = 'PENDING';
  filteredLoans: Smeloan[] = [];
  showApprovalModal = false;
  selectedLoan: Smeloan | null = null;
  currentDate = new Date(); 
  estimatedEndDate?: Date;
  constructor(private smeLoanService: SmeLoanService,
    private authService: AuthService,
    private router: Router
) {}

  ngOnInit(): void {
    this.fetchLoans();
  }
  fetchLoans(): void {
    this.loading = true;
    this.smeLoanService.getLoans(Number(this.authService.getCurrentUserBranchId())).subscribe(
      (data) => {
        if (Array.isArray(data)) {
          this.loans = data;
          this.filterLoans(); // Apply initial filter here
        }
      },
      (error) => {
        this.errorMessage = error.message || 'Failed to load loan data.';
      }
    ).add(() => this.loading = false);
  }

  // Add filter function
  filterLoans(): void {
    
      this.filteredLoans = this.loans.filter(loan =>
        loan.status.toUpperCase() === this.selectedStatus.toUpperCase()
      );
    
    this.currentPage = 1;
    this.updatePagination();
  }
  private updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredLoans.length / this.itemsPerPage) || 1;
  }

// Modify the pagination getters
get paginatedLoans(): Smeloan[] {
  const start = (this.currentPage - 1) * this.itemsPerPage;
  return this.filteredLoans.slice(start, start + this.itemsPerPage);
}
get totalLoans(): number {
  return this.filteredLoans.length;
}

get startIndex(): number {
  return (this.currentPage - 1) * this.itemsPerPage + 1;
}

get endIndex(): number {
  return Math.min(this.currentPage * this.itemsPerPage, this.totalLoans);
}

get pages(): number[] {
  return Array.from({ length: this.totalPages }, (_, i) => i + 1);
}

previousPage(): void {
  if (this.currentPage > 1) {
    this.currentPage--;
  }
}

nextPage(): void {
  if (this.currentPage < this.totalPages) {
    this.currentPage++;
  }
}

goToPage(page: number): void {
  if (page >= 1 && page <= this.totalPages) {
    this.currentPage = page;
  }
}
onStatusChange(): void {
  this.filterLoans();
}



approveLoan(loanId: number): void {
  this.smeLoanService.approveLoan(loanId).subscribe({
    next: (response) => {
      console.log('Loan Approved successfully:', response.message);
      
      // Fetch the updated loan details
      this.smeLoanService.getLoanById(loanId).subscribe({
        next: (loanResponse) => {
          this.selectedLoan = loanResponse.data;
          this.currentDate = new Date();
          if (this.selectedLoan?.duration) {
            this.estimatedEndDate = this.addMonths(this.currentDate, this.selectedLoan.duration);
          }
          
          // Automatically trigger the report download
          
          this.downloadSmeReport();
          // Navigate to loan details page
          
          this.router.navigate(['/operation-dashboard/sme-loan-detail', loanId]);
        },
        error: (err) => console.error('Error fetching updated loan details:', err)
      });
    },
    error: (error) => {
      console.error('Error while approving loan:', error);
    }
  });
}


rejectLoan(loanId: number): void {
  this.smeLoanService.rejectLoan(loanId).subscribe({
    next: (response) => {
      console.log('Loan rejected successfully:', response.message);
      // Show success message or toast here if you want
    },
    error: (error) => {
      console.error('Error while rejecting loan:', error);
      // Show error message or toast here if needed
    }
  });
}

viewDetails(loanId: number | undefined): void {
  if (loanId) {
    this.router.navigate(['/operation-dashboard/sme-loan-detail', loanId]);
    console.log(loanId);
    
  } else {
    console.warn('Loan ID is missing');
  }

}
openApprovalModal(loanId: number) {
  this.smeLoanService.getLoanById(loanId).subscribe({
    next: (response) => {
      this.selectedLoan = response.data;
      this.currentDate = new Date();
      this.showApprovalModal = true;
      if (this.selectedLoan?.duration) {
        this.estimatedEndDate = this.addMonths(
          this.currentDate, 
          this.selectedLoan.duration
          
        );
      }
    },
    
    error: (err) => console.error('Error fetching loan details:', err)
  });
}
getTotalCollateral(): number {
  let total = 0;

  if (this.selectedLoan?.usedCollaterals) {
    this.selectedLoan.usedCollaterals.forEach((item, index) => {
      console.log(`Collateral ${index + 1}:`, item);
      total += Number(item?.usedValue) || 0;
      console.log(`UsedValue of collateral ${index + 1}: `, item?.usedValue);
      console.log("Total Collaterals:", this.selectedLoan?.usedCollaterals?.length);

    });

  }
  console.log("Final Total Collateral: ", total);

  return total;
}




private addMonths(date: Date, months: number): Date {
  const result = new Date(date);
  result.setMonth(result.getMonth() + months);
  
  // Handle edge cases (e.g. 31st -> 30th/28th)
  if (result.getDate() !== date.getDate()) {
    result.setDate(0);
  }
  
  return result;
}

downloadSmeReport(): void {
  if (!this.selectedLoan) {
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
  doc.text(`ID: ${this.selectedLoan.loanId}`, margin, yPos);
  yPos += 8;

  // Borrower Row - Closer Spacing
  doc.setFontSize(11);
  doc.setTextColor(0);
  doc.text('Borrower:', margin, yPos);
  const borrowerName = this.selectedLoan.borrowerName || '';
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
  doc.text(`${this.selectedLoan.loanAmount?.toLocaleString()} KS`, margin, yPos + 5);
  doc.text(`${this.selectedLoan.interestRate}%`, centerX, yPos + 5);
  doc.text(`${this.selectedLoan.duration} months`, rightX, yPos + 5);
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
  const startDate = new Date().toLocaleDateString('en-GB', {
    day: '2-digit',
    month: 'short',
    year: 'numeric'
  });
  const endDate = this.selectedLoan.expiredDate 
    ? new Date(this.selectedLoan.expiredDate).toLocaleDateString('en-GB', {
        day: '2-digit',
        month: 'short',
        year: 'numeric'
      })
    : 'N/A';

  doc.text(startDate.toString(), margin, yPos + 5);
  doc.text(endDate, pageWidth - margin - doc.getTextWidth(endDate), yPos + 5);

  
  // Save PDF
  const fileName = `Loan_Approval_${this.selectedLoan.loanId}.pdf`;
  doc.save(fileName);
}




}
