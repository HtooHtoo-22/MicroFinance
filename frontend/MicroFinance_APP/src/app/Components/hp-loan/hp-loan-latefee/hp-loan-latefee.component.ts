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
}
