import { Component, OnInit } from '@angular/core';
import { LoanDashboardServiceImpl } from '../../service/loan-dashboard-service.service';
import { LoanDashboardDTO } from '../../model/LoanDashboardDTO';

@Component({
  selector: 'app-loan-metrics-admin',
  standalone: false,
  templateUrl: './loan-metrics-admin.component.html',
  styleUrl: './loan-metrics-admin.component.css'
})
export class LoanMetricsAdminComponent implements OnInit {
  metrics: LoanDashboardDTO | null = null;
  loading = false;
  errorMessage: string | null = null;
  
  // Default to current month
  startDate: string = new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().split('T')[0];
  endDate: string = new Date().toISOString().split('T')[0];

  constructor(private loanDashboardService: LoanDashboardServiceImpl) {}

  ngOnInit(): void {
    this.loadMetrics();
  }

  loadMetrics(): void {
    this.loading = true;
    this.errorMessage = null;
    
    this.loanDashboardService.getLoanMetrics(this.startDate, this.endDate).subscribe({
      next: (response) => {
        this.metrics = response;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load loan metrics. Please try again later.';
        console.error('Error loading loan metrics:', error);
      },
      complete: () => {
        this.loading = false;
      }
    });
  }
}