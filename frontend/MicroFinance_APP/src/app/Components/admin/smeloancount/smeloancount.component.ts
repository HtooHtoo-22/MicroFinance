import { Component, OnInit } from '@angular/core';
import { LoanService } from '../../../service/loan.service';

@Component({
  selector: 'app-smeloancount',
  standalone: false,
  templateUrl: './smeloancount.component.html',
  styleUrl: './smeloancount.component.css'
})
export class SmeloancountComponent implements OnInit {
  smeLoanCount: number = 0;
  loading: boolean = true;
  error: string | null = null;

  constructor(private loanService: LoanService) {}

  ngOnInit(): void {
    this.loadLoanCounts();
  }

  loadLoanCounts(): void {
    this.loading = true;
    this.error = null;
    
    this.loanService.getApprovedLoanCounts().subscribe({
      next: (response) => {
        this.smeLoanCount = response.smeLoans;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load SME loan count';
        this.loading = false;
        console.error('Error loading SME loan count:', err);
      }
    });
  }
}
