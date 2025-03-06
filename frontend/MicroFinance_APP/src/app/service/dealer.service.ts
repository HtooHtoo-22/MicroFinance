// dealer.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Dealer } from '../model/Dealer';
import { ApiResponse } from '../model/Apirespon';

@Injectable({
  providedIn: 'root'
})
export class DealerService {
  private apiUrl = 'http://localhost:8081/api/dealers';

  constructor(private http: HttpClient) { }

  createDealer(dealer: Dealer): Observable<any> {
    return this.http.post(this.apiUrl, dealer);
  }

  getAllDealers(): Observable<Dealer[]> {
    return this.http.get<ApiResponse<Dealer[]>>(`${this.apiUrl}/list`).pipe(
      map(response => response.data)
    );
  }

  approveDealer(dealerId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${dealerId}/approve`, {});
  }

  rejectDealer(dealerId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${dealerId}/reject`, {});
  }
}