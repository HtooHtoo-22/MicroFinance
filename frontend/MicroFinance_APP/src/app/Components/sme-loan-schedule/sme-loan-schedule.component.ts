import { Component, Input } from '@angular/core';
import { Smeloan } from '../../model/SmeLoan';
import { SmeLoanService } from '../../service/sme-loan.service';
import { SmeScheduleService } from '../../service/sme-schedule.service';

@Component({
  selector: 'app-sme-loan-schedule',
  standalone: false,
  templateUrl: './sme-loan-schedule.component.html',
  styleUrl: './sme-loan-schedule.component.css'
})
export class SmeLoanScheduleComponent {
  @Input() loanId?: number;
  @Input() loanData?: Smeloan;
  constructor(private scheduleService: SmeScheduleService) {}
  ngOnInit(): void {
    console.log('Component initialized with Loan ID:', this.loanId);
    console.log('Loan Data:Repay', this.loanData);
    if (this.loanId) {
      this.scheduleService.getSchedulesByLoanId(this.loanId).subscribe({
        next: (schedules) => {
          console.log('Repayment Schedules:', schedules);
        },
        error: (err) => {
          console.error('Error fetching schedules:', err);
        }
      });
    }
  }

  
}
