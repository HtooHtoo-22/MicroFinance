import { Component } from '@angular/core';
import { SmeLoanService } from '../../service/sme-loan.service';
import { AuthService } from '../../service/auth.service';
import { Router } from '@angular/router';
import { Smeloan } from '../../model/SmeLoan';

@Component({
  selector: 'app-sme-approve-loans',
  standalone: false,
  templateUrl: './sme-approve-loans.component.html',
  styleUrl: './sme-approve-loans.component.css'
})
export class SmeApproveLoansComponent {
  loans: Smeloan[] = [];
    errorMessage: string | null = null;
    loading: boolean = false;
    filteredLoans: Smeloan[] = [];
    selectedStatus: string = 'ALL';
    currentPage: number = 1;
  itemsPerPage: number = 7;
  totalPages: number = 0;
  constructor(private smeLoanService: SmeLoanService,
      private authService: AuthService,
      private router: Router
  ) {}
  ngOnInit(): void {
    this.fetchLoans();
  }
  fetchLoans(): void {
    this.loading = true;
    this.smeLoanService.getApprovedLoans(Number(this.authService.getCurrentUserBranchId()))
      .subscribe({
        next: (data) => {
          this.loans = Array.isArray(data) ? data : [];
          this.filterLoans(); // Apply initial filter
        },
        error: (error) => {
          this.errorMessage = error.message || 'Failed to load loans';
        },
        complete: () => this.loading = false
      });
  }
  
  filterLoans(): void {
    if (this.selectedStatus === 'ALL') {
      this.filteredLoans = [...this.loans];
    } else {
      this.filteredLoans = this.loans.filter(loan => 
        loan.loanStatus.toUpperCase() === this.selectedStatus
      );
    }
  }


  















setStatus(status: string) {
  this.selectedStatus = status;
}

private updatePagination(): void {
  this.totalPages = Math.ceil(this.filteredLoans.length / this.itemsPerPage) || 1;
}
getStatusClass(status: string): string {
  const statusClasses = {
    'PAID LOAN': 'bg-green-100 text-green-800',
    'HEALTHY LOAN': 'bg-teal-100 text-teal-800',
    'WATCHLIST LOAN': 'bg-yellow-100 text-yellow-800',
    'NPL LOAN': 'bg-red-100 text-red-800'
  };
  return statusClasses[status.toUpperCase() as keyof typeof statusClasses] || 'bg-gray-100 text-gray-800';
}
viewDetails(loanId: number | undefined): void {
  if (loanId) {
    this.router.navigate(['/operation-dashboard/sme-loan-detail', loanId]);
    console.log(loanId);
    
  } else {
    console.warn('Loan ID is missing');
  }

}
  // Add filter function
 
}
