import { Component, OnInit } from '@angular/core';
import { LoanService } from '../../../service/loan.service';

@Component({
  selector: 'app-hploancount',
  standalone: false,
  templateUrl: './hploancount.component.html',
  styleUrl: './hploancount.component.css'
})
export class HploancountComponent implements OnInit {
  hpLoanCount: number = 0;
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
        this.hpLoanCount = response.hpLoans;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load HP loan count';
        this.loading = false;
        console.error('Error loading HP loan count:', err);
      }
    });
  }
}