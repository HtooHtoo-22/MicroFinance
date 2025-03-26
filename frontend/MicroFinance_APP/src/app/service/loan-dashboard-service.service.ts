import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoanDashboardDTO, LoanDashboardService } from '../model/LoanDashboardDTO';

@Injectable({
  providedIn: 'root'
})
export class LoanDashboardServiceImpl implements LoanDashboardService {
  private apiUrl = '/api/dashboard/loan-metrics';

  constructor(private http: HttpClient) {}

  getLoanMetrics(startDate: string, endDate: string): Observable<LoanDashboardDTO> {
    const params = {
      startDate: startDate,
      endDate: endDate
    };
    return this.http.get<LoanDashboardDTO>(this.apiUrl, { params });
  }
}