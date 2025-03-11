import { Component } from '@angular/core';
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
  // Implement the approval logic here
}
rejectLoan(loanId: number): void {
}
viewDetails(loanId: number | undefined): void {
  if (loanId) {
    this.router.navigate(['/dashboard/sme-loan-detail', loanId]);
    console.log(loanId);
    
  } else {
    console.warn('Loan ID is missing');
  }

}
}