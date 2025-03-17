import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, map, Observable, of } from 'rxjs';
import { CurrentAccount } from '../model/CurrentAcc';
import { ApiResponse } from '../model/ApiResponse';
import { Branch } from '../model/user';

@Injectable({
  providedIn: 'root'
})
export class CurrentAccService {
  private apiUrl = 'http://localhost:8081/accounts'; // Adjust the base URL as necessary

  private branchUrl = 'http://localhost:8081/api/branches';

  constructor(private http: HttpClient) {}

  // Create a new current account
  createCurrentAccount(accountData: CurrentAccount): Observable<ApiResponse<CurrentAccount>> {
    return this.http.post<ApiResponse<CurrentAccount>>(`${this.apiUrl}/currentAcc`, accountData);
  }

  // Get a current account by account ID
  getCurrentAccountById(accountId: string): Observable<ApiResponse<CurrentAccount>> {
    return this.http.get<ApiResponse<CurrentAccount>>(`${this.apiUrl}/${accountId}`);
  }

  updateCurrentAccount(accountId: string, accountData: CurrentAccount): Observable<ApiResponse<CurrentAccount>> {
    return this.http.put<ApiResponse<CurrentAccount>>(
      `${this.apiUrl}/currentAcc/${accountId}`, 
      accountData
    );
  }

  listCurrentAcc(): Observable<{ data: CurrentAccount[] }> {
    return this.http.get<{ data: CurrentAccount[] }>(`${this.apiUrl}`);
  }

  getAccountsByCifId(cifId: number): Observable<ApiResponse<CurrentAccount[]>> {
    return this.http.get<ApiResponse<CurrentAccount[]>>(`${this.apiUrl}/by-cif/${cifId}`);
  }

  getBranches(): Observable<{data: Branch[]}> {
    return this.http.get<{data: Branch[] }>(`${this.branchUrl}/list`);
  }

  getCurrentAccountCount(branchId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/count/${branchId}`);
  }

  updateFreezeStatus(accountId: string, freeze: boolean): Observable<ApiResponse<CurrentAccount>> {
    return this.http.patch<ApiResponse<CurrentAccount>>(
      `${this.apiUrl}/${accountId}/freeze?freeze=${freeze}`,
      {}
    );
  }
  
  
}