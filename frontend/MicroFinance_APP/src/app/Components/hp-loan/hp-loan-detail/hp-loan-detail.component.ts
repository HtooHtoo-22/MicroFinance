import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HPLoan, HpLoanService } from '../../../service/hp-loan.service';
import { ApiResponse } from '../../../model/ApiResponse';

@Component({
  selector: 'app-hp-loan-detail',
  standalone: false,
  templateUrl: './hp-loan-detail.component.html',
  styleUrl: './hp-loan-detail.component.css'
})
export class HpLoanDetailComponent implements OnInit {
  loan: HPLoan | null = null;
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
    private router: Router
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
}