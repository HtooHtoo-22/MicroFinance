// loan.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoanService {
  private apiUrl = 'http://localhost:8081/api/loans'; // Replace with your API URL

  constructor(private http: HttpClient) { }

  getApprovedLoanCounts(): Observable<any> {
    return this.http.get(`${this.apiUrl}/approved-counts`);
  }

  getApprovedLoanCountsByBranch(branchId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/approved-counts/${branchId}`);
  }
}