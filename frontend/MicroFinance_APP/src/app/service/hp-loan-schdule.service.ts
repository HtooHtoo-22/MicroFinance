import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HPSchedule } from '../model/HPSchedule';
import { ApiResponse } from '../model/ApiResponse';

@Injectable({
  providedIn: 'root'
})
export class HpLoanSchduleService {
  private apiUrl = 'http://localhost:8081/api/hp-loans-schedule'; // Fixed typo and plural "loans"

  constructor(private http: HttpClient) {}

  getSchedulesByLoanId(loanId: number): Observable<ApiResponse<HPSchedule[]>> {
    return this.http.get<ApiResponse<HPSchedule[]>>(`${this.apiUrl}/${loanId}/schedules`);
  }
}