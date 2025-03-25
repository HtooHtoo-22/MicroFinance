import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../model/ApiResponse';
import { HPRepaymentTrack } from '../model/HPRepaymentTrack';
import { HPLateFeeSummary } from '../model/HPLateFeeSummary';

export interface HPLoan {
  id: number;
  loanId: string;
  loanAmount: number;
  interestRate: number;
  gracePeriod: number;
  registeredDate: string;
  approvedDate: string | null;
  status: string;
  endDate: string | null;
  duration: number;
  entryUserId: number;
  entryUserName: string;
  approvedUserId: number | null; // Adjusted to match API (nullable)
  approvedUserName: string | null; // Adjusted to match API (nullable)
  currentAccountId: string;
  cifId: string;
  borrowerName: string;
  productId: number;
  downPaymentRate: number;
  dealerCommissionRate: number;
  currentCode: string | null;
  productName: string | null;
  productPhoto: string | null;
  productValue: number | null;
  tenor: number;
  loanStatus: string;
}

@Injectable({
  providedIn: 'root'
})
export class HpLoanService {
  private apiUrl = 'http://localhost:8081/api/hp-loans';

  constructor(private http: HttpClient) {}

  registerLoan(loanData: HPLoan): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, loanData);
  }

  approveLoan(loanId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/approve/${loanId}`, {});
  }

  rejectLoan(loanId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/reject/${loanId}`, {});
  }

  getHPLoans(): Observable<ApiResponse<HPLoan[]>> {
    return this.http.get<ApiResponse<HPLoan[]>>(`${this.apiUrl}/list`);
  }

  getHPLoanById(loanId: number): Observable<ApiResponse<HPLoan>> {
    return this.http.get<ApiResponse<HPLoan>>(`${this.apiUrl}/${loanId}`);
  }

  getApprovedHPLoans(): Observable<ApiResponse<HPLoan[]>> {
    return this.http.get<ApiResponse<HPLoan[]>>(`${this.apiUrl}/approved`);
  }
  getRepaymentTracksByLoanId(loanId: number): Observable<ApiResponse<HPRepaymentTrack[]>> {
    return this.http.get<ApiResponse<HPRepaymentTrack[]>>(
      `${this.apiUrl}/getRepaymentTracks/${loanId}`
    );
  }
  getLateFeeSummaryByLoanId(loanId: number): Observable<ApiResponse<HPLateFeeSummary>> {
      return this.http.get<ApiResponse<HPLateFeeSummary>>(
        `${this.apiUrl}/getLateFeeSummary/${loanId}`
      );
    }
}