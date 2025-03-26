import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { SmeLoanService } from '../../service/sme-loan.service';
import { AuthService } from '../../service/auth.service';
import { Smeloan } from '../../model/SmeLoan';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { ModelComponent } from '../model/model.component';

@Component({
  selector: 'app-sme-laon-history',
  standalone: false,
  templateUrl: './sme-laon-history.component.html',
  styleUrls: ['./sme-laon-history.component.css']
})
export class SmeLaonHistoryComponent implements OnInit {
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

  constructor(
    private smeLoanService: SmeLoanService,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar,
    private cd: ChangeDetectorRef,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    console.log("Hi");
    
    this.fetchLoans();
  }

  fetchLoans(): void {
    this.loading = true;
    this.smeLoanService.getPendingLoans(Number(this.authService.getCurrentUserBranchId())).subscribe(
      (data) => {
        if (Array.isArray(data)) {
          this.loans = data;
          console.log("This loans : "+this.loans);
          
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
            this.showModal('Loan approved successfully!', true);
            this.showApprovalModal = false; // Close the modal
            this.fetchLoans(); // Refresh the loan list
        },
        error: (error) => {
            console.error('Error while approving loan:', error);
            this.showModal('Failed to approve loan', false);
        }
    });
}

rejectLoan(loanId: number): void {
    this.smeLoanService.rejectLoan(loanId).subscribe({
        next: (response) => {
            console.log('Loan rejected successfully:', response.message);
            this.showModal('Loan rejected successfully!', true);
            this.showApprovalModal = false; // Close the modal
            this.fetchLoans(); // Refresh the loan list
        },
        error: (error) => {
            console.error('Error while rejecting loan:', error);
            this.showModal('Failed to reject loan', false);
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

  showModal(message: string, success: boolean): void {
    const dialogRef = this.dialog.open(ModelComponent, {
      width: '300px',
      data: { message, success }
    });

    dialogRef.afterClosed().subscribe(() => {
      this.fetchLoans(); // Refresh the loan list after closing the modal
    });
  }
}