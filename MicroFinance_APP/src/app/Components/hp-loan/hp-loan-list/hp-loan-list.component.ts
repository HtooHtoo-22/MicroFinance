import { Component, OnInit } from '@angular/core';
import { ApiResponse } from '../../../model/Apirespon';
import { ModelComponent } from '../../model/model.component';
import { MatDialog } from '@angular/material/dialog';
import { HPLoan } from '../../../model/Hploan';
import { HpLoanService } from '../../../service/hp-loan.service';

@Component({
  selector: 'app-hp-loan-list',
  standalone: false,
  templateUrl: './hp-loan-list.component.html',
  styleUrl: './hp-loan-list.component.css'
})
export class HpLoanListComponent implements OnInit {
  loans: HPLoan[] = [];
  errorMessage = '';

  constructor(private hpLoanService: HpLoanService,private dialog:MatDialog) {}

  ngOnInit() {
    this.fetchLoans();
  }

  fetchLoans(): void {
    this.hpLoanService.getHPLoans().subscribe({
      next: (response) => {
        if (response.statusCode === 200) {
          this.loans = response.data || [];
        } else {
          this.errorMessage = 'Failed to retrieve loans';
        }
      },
      error: (error) => {
        this.errorMessage = 'Error fetching loans: ' + error;
      }
    });
  }


  approveLoan(loanId: number): void {
    this.hpLoanService.approveLoan(loanId).subscribe({
      next: () => {
        
        this.showModal('Loan Approved Successfully',true);
        this.fetchLoans();
      },
      error: () => {
       
        this.showModal('Error approving loan',false);
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
        this.showModal('Error rejecting loan',false);
      }
    });
  }


  showModal(message: string, success: boolean): void {
          this.dialog.open(ModelComponent, {
            width: '300px',
            data: { message, success, }
          });
        }
}

