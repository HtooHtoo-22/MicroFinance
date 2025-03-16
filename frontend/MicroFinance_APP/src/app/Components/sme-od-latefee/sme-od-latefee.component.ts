import { Component, Input, OnInit } from '@angular/core';
import { SmeLoanService } from '../../service/sme-loan.service';
import { SMELateFeeSummary } from '../../model/SMELateFeeSummary';
import { ApiResponse } from '../../model/Apirespon';


@Component({
  selector: 'app-sme-od-latefee',
  standalone: false,
  templateUrl: './sme-od-latefee.component.html',
  styleUrls: ['./sme-od-latefee.component.css'] // Corrected the styleUrl to styleUrls
})
export class SmeOdLatefeeComponent implements OnInit {
  @Input() loanId?: number;
  lateFeeSummary: SMELateFeeSummary | null = null;
  hasGracePeriod: boolean = false;
  constructor(private smeLoanService: SmeLoanService) { }

  ngOnInit(): void {
    if (this.loanId) {
      this.getLateFeeSummary();
    } else {
      console.error('No loan ID provided');
    }
  }

  getLateFeeSummary(): void {
    if (this.loanId) {
      this.smeLoanService.getLateFeeSummaryByLoanId(this.loanId).subscribe(
        (response: ApiResponse<SMELateFeeSummary>) => {
          if (response && response.data) {
            this.lateFeeSummary = response.data;
            console.log('Late Fee Summary:', this.lateFeeSummary);
            this.hasGracePeriod = this.lateFeeSummary.odSchedules.some(schedule => schedule.gracePeriodEndDate !== null);
          } else {
            console.error('No data found for late fee summary');
          }
        },
        (error) => {
          console.error('Error fetching late fee summary:', error);
        }
      );
    } else {
      console.error('Loan ID is not provided or invalid.');
    }
  }
  getOverdueRowClass(status: string): string {
    const base = 'transition-colors duration-200';
    switch (status.toLowerCase()) {
        case 'partial overdue': 
            return `${base} bg-orange-100 hover:bg-orange-200`;
        case 'full overdue':
            return `${base} bg-red-100 hover:bg-red-200`;
        default:
            return base;
    }
}

getOverdueStatusClasses(status: string) {
    switch (status.toLowerCase()) {
        case 'partial overdue':
            return 'bg-amber-100 text-amber-900 px-3 py-1 rounded-full text-sm';
        case 'full overdue':
            return 'bg-red-100 text-red-900 px-3 py-1 rounded-full text-sm';
        default:
            return 'bg-gray-100 text-gray-600 px-3 py-1 rounded-full text-sm';
    }
}
getTotalODInterest(): number {
  if (!this.lateFeeSummary?.odSchedules) return 0;
  return this.lateFeeSummary.odSchedules.reduce((sum, schedule) => sum + (schedule.interestODAmount || 0), 0);
}

calculateDailyFee(): number {
  if (!this.lateFeeSummary) return 0;
  
  if (this.lateFeeSummary.lateDays <= 90) {
      return this.getTotalODInterest() * (this.lateFeeSummary.lateFeeRateBf90 / 100);
  }
  return this.lateFeeSummary.outStandingAmount * (this.lateFeeSummary.lateFeeRateAf90 / 100);
}
}
