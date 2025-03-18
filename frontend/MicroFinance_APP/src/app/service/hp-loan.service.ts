import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiResponse } from '../model/Apirespon';

export interface HPLoan {
  id?: number;
  loanId?: string;
  loanAmount: number;
  interestRate: number;
  gracePeriod: number;
  registeredDate?: string;
  approvedDate?: string;
  status?: string;
  endDate?: string;
  duration: number;
  entryUserId: number;
  approvedUserId?: number;
  currentAccountId: number;
  productId: number;
  downPaymentRate?: number;
  dealerCommissionRate: number;


  productName?: string; // NEW
  productValue?: number; // NEW
  currentCode?: string; // NEW

}


@Injectable({
  providedIn: 'root'
})
export class HpLoanService {

  private apiUrl = 'http://localhost:8081/api/hp-loans';

  constructor(private http:HttpClient) { }

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
 

  
}
