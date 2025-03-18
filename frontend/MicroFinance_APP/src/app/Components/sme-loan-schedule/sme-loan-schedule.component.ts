import { Component, Input } from '@angular/core';
import { Smeloan } from '../../model/SmeLoan';
import { SmeScheduleService } from '../../service/sme-schedule.service';
import { SMESchedule } from '../../model/SMESchedule';

@Component({
  selector: 'app-sme-loan-schedule',
  standalone: false,
  templateUrl: './sme-loan-schedule.component.html',
  styleUrl: './sme-loan-schedule.component.css'
})
export class SmeLoanScheduleComponent {
  @Input() loanId?: number;
  @Input() loanData?: Smeloan;
  schedules: SMESchedule[] = [];
  hasGracePeriod: boolean = false;
  isRefreshing = false;
  constructor(private scheduleService: SmeScheduleService) {}
  
  ngOnInit(): void {
    console.log('Component initialized with Loan ID:', this.loanId);
    console.log('Loan Data:Repay', this.loanData);
    if (this.loanId) {
      this.scheduleService.getSchedulesByLoanId(this.loanId).subscribe({
        next: (schedules) => {
          console.log('Repayment Schedules:', schedules);
          this.schedules = schedules.data;
            this.hasGracePeriod = schedules.data.some((schedule: SMESchedule) => schedule.gracePeriodEndDate !== null);
        },
        error: (err) => {
          console.error('Error fetching schedules:', err);
        }
      });
    }
  }
  getRowClass(status: string): string {
    const baseClasses = 'transition-colors duration-200';
    switch (status.toLowerCase()) {
        case 'not due yet':
            return `${baseClasses} bg-slate-50 hover:bg-slate-100`;
        case 'in grace period':
            return `${baseClasses} bg-sky-50 hover:bg-sky-200 `;  // Custom color if needed
        case 'partial overdue':
            return `${baseClasses} bg-orange-100 hover:bg-orange-300 border-l-4 border-rose-300`;
        case 'full overdue':
            return `${baseClasses} bg-rose-200 hover:bg-rose-300 border-l-4 border-rose-300`; // Added left border
        case 'paid':
            return `${baseClasses} bg-emerald-100 hover:bg-emerald-200`; // More visible green
        default:
            return baseClasses;
    }
}
  getStatusClasses(status: string) {
    switch (status.toLowerCase()) {
        case 'paid':
            return 'bg-slate-100 text-green-600 px-3 py-1 rounded-full text-sm font-medium';
        case 'in grace period':
            return 'bg-sky-100 text-sky-800 px-3 py-1 rounded-full text-sm font-medium';
        case 'partial overdue':
            return 'bg-amber-50 text-amber-900 px-3 py-1 rounded-full text-sm font-medium';
        case 'full overdue':
            return 'bg-rose-100 text-rose-800 px-3 py-1 rounded-full text-sm font-medium';
        case 'not due yet':
            return 'bg-slate-100 text-slate-600 px-3 py-1 rounded-full text-sm font-medium';
        default:
            return 'bg-slate-100 text-slate-600 px-3 py-1 rounded-full text-sm font-medium';
    }
}
refreshData(): void {
  this.isRefreshing = true;
  this.scheduleService.getSchedulesByLoanId(this.loanId!).subscribe({
    next: (response) => {
      this.schedules = response.data;
      this.isRefreshing = false;
    },
    error: (err) => {
      console.error('Refresh failed:', err);
      this.isRefreshing = false;
    }
  });
}
}


