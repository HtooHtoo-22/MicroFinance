import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { Smeloan } from '../model/SmeLoan';

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
}
