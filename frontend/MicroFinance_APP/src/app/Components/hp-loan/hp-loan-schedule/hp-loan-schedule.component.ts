import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { HPSchedule } from '../../../model/HPSchedule';
import { HpLoanSchduleService } from '../../../service/hp-loan-schdule.service';
import { ActivatedRoute } from '@angular/router';
import { ApiResponse } from '../../../model/ApiResponse';

@Component({
  selector: 'app-hp-loan-schedule',
  standalone: false,
  templateUrl: './hp-loan-schedule.component.html',
  styleUrls: ['./hp-loan-schedule.component.css']
})
export class HpLoanScheduleComponent implements OnChanges {
  @Input() loanId: number | undefined; // Accept loanId as input
  @Input() loanData: any; // Accept loanData as input (optional, if needed)
  schedules: HPSchedule[] = [];
  errorMessage: string = '';

  constructor(private hpLoanScheduleService: HpLoanSchduleService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['loanId'] && this.loanId) {
      this.fetchSchedules();
    }
  }

  fetchSchedules(): void {
    if (this.loanId) {
      this.hpLoanScheduleService.getSchedulesByLoanId(this.loanId).subscribe({
        next: (response: ApiResponse<HPSchedule[]>) => {
          this.schedules = response.data;
          console.log(this.schedules);
          
          this.errorMessage = '';
        },
        error: (err) => {
          this.errorMessage = 'Error fetching schedules: ' + err.message;
          this.schedules = [];
        }
      });
    } else {
      this.errorMessage = 'No loan ID provided.';
    }
    
  }
  formatStatus(status: string): string {
    switch(status) {
      case 'NOT_DUE_YET': return 'Not Due Yet';
      case 'INTEREST_PAID_PRINCIPAL_OD': return 'Interest Paid (Principal OD)';
      case 'INTEREST_OD_PRINCIPAL_OD': return 'Interest OD (Principal OD)';
      case 'ALL_PAID': return 'All Paid';
      case 'IN_GRACE_PERIOD': return 'In Grace Period';
      default: return status;
    }
  }
  getRowClass(status: string): string {
    const baseClasses = 'transition-colors duration-200';
    switch (status) {
        case 'NOT_DUE_YET':
            return `${baseClasses} bg-slate-50 hover:bg-slate-100`;
        case 'IN_GRACE_PERIOD':
            return `${baseClasses} bg-sky-50 hover:bg-sky-200`;
        case 'INTEREST_OD_PRINCIPAL_OD':
            return `${baseClasses} bg-rose-100 hover:bg-rose-300 border-l-4 border-rose-300`;
        case 'INTEREST_PAID_PRINCIPAL_OD':
            return `${baseClasses} bg-orange-200 hover:bg-orange-300 border-l-4 border-rose-300`;
        case 'ALL_PAID':
            return `${baseClasses} bg-emerald-100 hover:bg-emerald-200`;
        default:
            return baseClasses;
    }
}
}