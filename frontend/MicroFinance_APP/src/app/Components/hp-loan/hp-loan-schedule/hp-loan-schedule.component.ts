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
}