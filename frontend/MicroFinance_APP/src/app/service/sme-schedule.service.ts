import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SMESchedule } from '../model/SMESchedule';
import { Observable } from 'rxjs';
import { ApiResponse } from '../model/ApiResponse';

@Injectable({
  providedIn: 'root'
})
export class SmeScheduleService {
  private apiUrl = 'http://localhost:8081/api/sme-schedules';

  constructor(private http: HttpClient) { }

  getSchedulesByLoanId(loanId: number): Observable<ApiResponse<SMESchedule[]>> {
    return this.http.get<ApiResponse<SMESchedule[]>>(
      `${this.apiUrl}/scheduleListByLoanId/${loanId}`
    );
  }
  
}
