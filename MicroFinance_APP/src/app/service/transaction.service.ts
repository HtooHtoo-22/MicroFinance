import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Transaction } from '../model/Transaction';
import { ApiResponse } from '../model/Apirespon';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private apiUrl = 'http://localhost:8081/transactions';

  constructor(private http: HttpClient) {}

  createTransaction(transactionData: any): Observable<ApiResponse<Transaction[]>> {
    return this.http.post<ApiResponse<Transaction[]>>(`${this.apiUrl}/create`, transactionData);
  }

  getAllTransactions(): Observable<ApiResponse<Transaction[]>> {
    return this.http.get<ApiResponse<Transaction[]>>(`${this.apiUrl}/list`);
  }

  getTransactionsByCifId(cifId: number): Observable<ApiResponse<Transaction[]>> {
    return this.http.get<ApiResponse<Transaction[]>>(`${this.apiUrl}/by-cif/${cifId}`);
  }

  getTransactionsByAccountId(accountId: number): Observable<ApiResponse<Transaction[]>> {
    return this.http.get<ApiResponse<Transaction[]>>(`${this.apiUrl}/by-account/${accountId}`);
  }


  downloadTransactionReport(transactionId: number) {
    return this.http.get(`${this.apiUrl}/download-report/${transactionId}`, {
      responseType: 'blob'
    });
  }

}
