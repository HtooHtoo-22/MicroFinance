import { Component, OnInit } from '@angular/core';
import { HPLoan, HpLoanService } from '../../../service/hp-loan.service';
import { MatDialog } from '@angular/material/dialog';
import { ModelComponent } from '../../model/model.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-hp-loan-list',
  standalone: false,
  templateUrl: './hp-loan-list.component.html',
  styleUrls: ['./hp-loan-list.component.css']
})
export class HpLoanListComponent implements OnInit {
  loans: HPLoan[] = [];
  errorMessage = '';
  showApprovalModal = false;
  selectedLoan: HPLoan | null = null;
  currentDate: Date | null = null;
  estimatedEndDate: Date | null = null;
  currentPage: number = 1;
  itemsPerPage: number = 7;
  totalLoans: number = 0;
  totalPages: number = 0;

  constructor(
    private hpLoanService: HpLoanService,
    private dialog: MatDialog,
    private router: Router
  ) {}

  ngOnInit() {
    this.fetchLoans();
  }

  fetchLoans(): void {
    this.hpLoanService.getHPLoans().subscribe({
      next: (response) => {
        if (response.statusCode === 200) {
          this.loans = response.data || [];
          this.totalLoans = this.loans.length;
          this.totalPages = Math.ceil(this.totalLoans / this.itemsPerPage);
        } else {
          this.errorMessage = 'Failed to retrieve loans';
        }
      },
      error: (error) => {
        this.errorMessage = 'Error fetching loans: ' + error;
      }
    });
  }

  openApprovalModal(loanId: number): void {
    this.hpLoanService.getHPLoanById(loanId).subscribe({
      next: (response) => {
        this.selectedLoan = response.data;
        this.currentDate = new Date();
        this.showApprovalModal = true;
        if (this.selectedLoan?.duration) {
          this.estimatedEndDate = this.addMonths(this.currentDate, this.selectedLoan.duration);
        }
      },
      error: (err) => {
        console.error('Error fetching loan details:', err);
        this.errorMessage = 'Error fetching loan details';
      }
    });
  }

  approveLoan(loanId: number): void {
    this.hpLoanService.approveLoan(loanId).subscribe({
      next: () => {
        this.showApprovalModal = false;
        this.showModal('Loan Approved Successfully', true);
        this.fetchLoans();
      },
      error: () => {
        this.showApprovalModal = false;
        this.showModal('Error approving loan', false);
      }
    });
  }

  rejectLoan(loanId: number): void {
    this.hpLoanService.rejectLoan(loanId).subscribe({
      next: () => {
        this.showModal('Loan Rejected Successfully', true);
        this.fetchLoans();
      },
      error: () => {
        this.showModal('Error rejecting loan', false);
      }
    });
  }

  viewDetails(loanId: number | undefined): void {
    if (loanId) {
      this.router.navigate(['/dashboard/hp-loan-detail', loanId]);
    } else {
      console.warn('Loan ID is missing');
    }
  }

  showModal(message: string, success: boolean): void {
    this.dialog.open(ModelComponent, {
      width: '300px',
      data: { message, success }
    });
  }

  addMonths(date: Date, months: number): Date {
    const result = new Date(date);
    result.setMonth(result.getMonth() + months);
    return result;
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