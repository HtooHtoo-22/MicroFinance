import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { Smeloan } from '../model/SmeLoan';
import { map } from 'rxjs';

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
  
}
