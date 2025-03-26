import { Component, Input } from '@angular/core';
import { HPLateFeeSummary } from '../../../model/HPLateFeeSummary';
import { HPLoan, HpLoanService } from '../../../service/hp-loan.service';
import { ApiResponse } from '../../../model/ApiResponse';

@Component({
  selector: 'app-hp-loan-latefee',
  standalone: false,
  templateUrl: './hp-loan-latefee.component.html',
  styleUrl: './hp-loan-latefee.component.css'
})
export class HpLoanLatefeeComponent {
     @Input() loanId: number | undefined; // Accept loanId as input
     lateFeeSummary: HPLateFeeSummary | null = null;
     hasGracePeriod: boolean = false;
     constructor(private hpLoanService: HpLoanService) { }

     ngOnInit(): void {
      if (this.loanId) {
        this.getLateFeeSummary();
      } else {
        console.error('No loan ID provided');
      }
    }
    getLateFeeSummary(): void {
      if (this.loanId) {
        this.hpLoanService.getLateFeeSummaryByLoanId(this.loanId).subscribe(
          (response: ApiResponse<HPLateFeeSummary>) => {
            if (response && response.data) {
              this.lateFeeSummary = response.data;
              console.log('Late Fee Summary:', this.lateFeeSummary);
              
              this.lateFeeSummary.odSchedules.forEach(schedule => {
                console.log('Schedule:', schedule);  // Log the entire schedule object
                
                if (schedule && schedule.interestODAmount !== undefined && schedule.interestODAmount !== null) {
                  console.log(schedule.interestODAmount);
                } else {
                  console.log("Interest OD Amount is undefined or null for this schedule");
                }
              });
              
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
    
      getTotalODInterest(): number {
        if (!this.lateFeeSummary?.odSchedules) return 0;
        return this.lateFeeSummary.odSchedules.reduce((sum, schedule) => sum + (schedule.interestODAmount || 0), 0);
      }
      
      getTotalODPrincipal(): number {
        if (!this.lateFeeSummary?.odSchedules) return 0;
        return this.lateFeeSummary.odSchedules.reduce((sum, schedule) => sum + (schedule.principalOdAmount || 0), 0);
      }
      
      getTotalOD(): number {
        return this.getTotalODInterest() + this.getTotalODPrincipal();
      }
      
      calculateDailyFee(): number {
        if (!this.lateFeeSummary) return 0;
        
        if (this.lateFeeSummary.lateDays <= 90) {
            return this.getTotalOD() * (this.lateFeeSummary.lateFeeRateBf90 / 100);
        }
        return this.lateFeeSummary.outStandingAmount * (this.lateFeeSummary.lateFeeRateAf90 / 100);
      }
      getOverdueRowClass(status: string): string {
        const base = 'transition-colors duration-200';
        switch (status.toLowerCase()) {
            case 'INTEREST_PAID_PRINCIPAL_OD': 
                return `${base} bg-orange-100 hover:bg-orange-200`;
            case '"INTEREST_OD_PRINCIPAL_OD"':
                return `${base} bg-red-100 hover:bg-red-200`;
            default:
                return base;
        }
    }
    
    getOverdueStatusClasses(status: string) {
        switch (status.toLowerCase()) {
            case 'INTEREST_PAID_PRINCIPAL_OD':
                return 'bg-amber-100 text-amber-900 px-3 py-1 rounded-full text-sm';
            case 'INTEREST_OD_PRINCIPAL_OD':
                return 'bg-red-100 text-red-900 px-3 py-1 rounded-full text-sm';
            default:
                return 'bg-gray-100 text-gray-600 px-3 py-1 rounded-full text-sm';
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
