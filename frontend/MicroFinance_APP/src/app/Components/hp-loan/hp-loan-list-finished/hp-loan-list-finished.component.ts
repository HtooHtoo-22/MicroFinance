import { Component, OnInit } from '@angular/core';
import { HPLoan, HpLoanService } from '../../../service/hp-loan.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-hp-loan-list-finished',
  standalone: false,
  templateUrl: './hp-loan-list-finished.component.html',
  styleUrl: './hp-loan-list-finished.component.css'
})
export class HpLoanListFinishedComponent implements OnInit {
  loans: HPLoan[] = [];
  errorMessage = '';
  currentPage: number = 1;
  itemsPerPage: number = 7;
  totalLoans: number = 0;
  totalPages: number = 0;
  filteredLoans: HPLoan[] = [];
selectedStatus: string = 'ALL';
loading:boolean =false;
  constructor(
    private hpLoanService: HpLoanService,
    private router: Router,
  ) {}

  ngOnInit() {
    this.fetchApprovedLoans();
  }

  fetchApprovedLoans(): void {
    this.hpLoanService.getApprovedHPLoans().subscribe({
      next: (response) => {
        if (response) { // Check status field from ApiResponse
          this.loans = response.data || [];
          console.log(this.loans);
          this.filterLoans();
          this.totalLoans = this.loans.length;
          this.totalPages = Math.ceil(this.totalLoans / this.itemsPerPage);
        } else {
          this.errorMessage = response || 'Failed to retrieve approved loans';
        }
      },
      error: (error) => {
        this.errorMessage = 'Error fetching approved loans: ' + (error.message || error);
      }
    });
  }
  filterLoans() {
    if (this.selectedStatus === 'ALL') {
      this.filteredLoans = this.loans;
    } else {
      this.filteredLoans = this.loans.filter(loan => 
        loan.loanStatus.toUpperCase() === this.selectedStatus
      );
    }
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
      this.router.navigate(['/operation-dashboard/hp-loan-detail', loanId]);
    } else {
      console.warn('Loan ID is missing');
    }
  }

  get paginatedLoans(): HPLoan[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.loans.slice(start, start + this.itemsPerPage);
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
    if (this.currentPage > 1) this.currentPage--;
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) this.currentPage++;
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) this.currentPage = page;
  }
}