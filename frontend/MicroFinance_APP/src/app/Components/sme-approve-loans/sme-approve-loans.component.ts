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
    this.smeLoanService.getApprovedLoans(Number(this.authService.getCurrentUserBranchId())).subscribe(
      (data) => {
        if (Array.isArray(data)) {
          this.loans = data;
          console.log("This Loans", this.loans);
          
          this.filterLoans(); // Apply initial filter here
        }
      },
      (error) => {
        this.errorMessage = error.message || 'Failed to load loan data.';
      }
    ).add(() => this.loading = false);
  }
  filterLoans(): void {
    
    this.filteredLoans = this.loans.filter(loan =>
      loan.loanStatus.toUpperCase() === this.selectedStatus.toUpperCase()
    );
  
  this.currentPage = 1;
  this.updatePagination();
}
private updatePagination(): void {
  this.totalPages = Math.ceil(this.filteredLoans.length / this.itemsPerPage) || 1;
}
getStatusClass(status: string): string {
  const statusClasses = {
    'ACTIVE': 'bg-green-100 text-green-800',
    'PENDING': 'bg-yellow-100 text-yellow-800',
    'CLOSED': 'bg-gray-100 text-gray-800',
    'DEFAULTED': 'bg-red-100 text-red-800',
    'COMPLETED': 'bg-blue-100 text-blue-800'
  };
  return statusClasses[status.toUpperCase() as keyof typeof statusClasses] || 'bg-gray-100 text-gray-800';
}
viewDetails(loanId: Smeloan): void {
}
downloadReport(loan:Smeloan): void {
}
  // Add filter function
 
}
