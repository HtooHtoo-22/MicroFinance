import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { Smeloan } from '../model/SmeLoan';
import { map } from 'rxjs';
import { ApiResponse } from '../model/Apirespon';
import { SMERepaymentTrack } from '../model/SMERepaymentTrack';


@Injectable({
  providedIn: 'root'
})
export class SmeLoanService {
  private apiUrl = 'http://localhost:8081/api/sme-loans';  
  constructor(private http: HttpClient) { }
  createLoan(loanData: Smeloan): Observable<any> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<any>(`${this.apiUrl}/register`, loanData, { headers });// ✅ Fixed URL
  }
  getLoans(branchId: number): Observable<Smeloan[]> {
    return this.http.get<any>(`${this.apiUrl}/loans/${branchId}`).pipe(
      map(response => {
        if (Array.isArray(response)) {
          return response; // ✅ Correct format
        } else if (response && Array.isArray(response.data)) {
          return response.data; // ✅ Fix if response is { data: [...] }
        } else {
          console.error("Unexpected API response format:", response);
          return []; // ✅ Prevent errors
        }
      })
    );
  }
  getLoanById(id: number): Observable<ApiResponse<Smeloan>> {
      return this.http.get<ApiResponse<Smeloan>>(`${this.apiUrl}/${id}`);
    }
  getLoanByLoanId(id: string): Observable<ApiResponse<Smeloan>> {
      return this.http.get<ApiResponse<Smeloan>>(`${this.apiUrl}/getByLoanID/${id}`);
  }
  rejectLoan(loanId: number): Observable<ApiResponse<Smeloan>> {
    return this.http.post<ApiResponse<Smeloan>>(`${this.apiUrl}/reject/${loanId}`, {});
  }
  approveLoan(loanId: number): Observable<ApiResponse<Smeloan>> {
    return this.http.post<ApiResponse<Smeloan>>(`${this.apiUrl}/approve/${loanId}`, {});
  }
  getRepaymentTracksByLoanId(loanId: number): Observable<ApiResponse<SMERepaymentTrack[]>> {
    return this.http.get<ApiResponse<SMERepaymentTrack[]>>(
      `${this.apiUrl}/getRepaymentTracks/${loanId}`
    );
  }
  
}
