import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, map, Observable, tap } from 'rxjs';
import { Rate } from '../model/Rate';


@Injectable({
  providedIn: 'root'
})
export class RateService {
  private apiUrl = 'http://localhost:8081/rates';

  constructor(private http: HttpClient) {}

  getAllRates(): Observable<Rate[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      tap(response => console.log('Response:', response)),
      map(response => response.data)
    );
  }
  

  getRateById(id: number): Observable<Rate> {
    return this.http.get<Rate>(`${this.apiUrl}/${id}`).pipe(
      catchError((error) => {
        console.error('Error retrieving rate:', error);
        throw error; // Re-throw the error for the component to handle
      })
    );
  }
  createRate(rate: Rate): Observable<Rate> {
    return this.http.post<any>(`${this.apiUrl}/create`, rate).pipe(
      map(response => response.data)
    );
  }

  updateRate(id: number, rate: Rate): Observable<Rate> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, rate).pipe(
      map(response => {
        if (!response || response.status === 'error') {
          throw new Error(response?.message || 'Rate not found');
        }
        return response.data || response;
      }),
      catchError((error) => {
        console.error('Error updating rate:', error);
        throw error;
      })
    );
  }

  deleteRate(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getRateByType(rateType: string): Observable<Rate> {
    return this.http.get<any>(`${this.apiUrl}/type/${rateType}`).pipe(
      map(response => response.data)
    );
  }
}
