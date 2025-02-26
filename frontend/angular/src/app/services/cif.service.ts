import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Cif } from '../models/cif';

@Injectable({
  providedIn: 'root'
})
export class CifService {
  private apiUrl = 'http://localhost:8081/api/cif';

  constructor(private http: HttpClient) {}

  // Get CIF by ID
  getCifById(id: number): Observable<Cif> {
    return this.http.get<{ data: Cif }>(`${this.apiUrl}/${id}`).pipe(
      map(response => {
        console.log("API Response:", response); // Debugging
        return response.data; // Ensure correct mapping
      })
    );
  }
  

  // Create CIF
  createCif(cifData: Cif, frontNRC: File, backNRC: File, userPhoto: File): Observable<any> {
    const formData = new FormData();
    formData.append('cif', JSON.stringify(cifData));
    formData.append('frontNRC', frontNRC);
    formData.append('backNRC', backNRC);
    formData.append('userPhoto', userPhoto);
  
    return this.http.post(`${this.apiUrl}`, formData);
  }
  

  // Update CIF
  // updateCif(id: number, cifData: Cif): Observable<any> {
  //   return this.http.patch(`${this.apiUrl}/${id}`, cifData); // Use PUT for full updates
  // }
  

  updateCif(id: number, cifData: Cif): Observable<any> {
    const updatedData = {
      ...cifData,
      incomeAmount: Number(cifData.incomeAmount) // Ensure it's a number
    };
    return this.http.patch(`${this.apiUrl}/${id}`, updatedData);
  }
  
  listCif(): Observable<{ data: Cif[] }> {
    return this.http.get<{ data: Cif[] }>(this.apiUrl); // Assuming the response contains an object with a 'data' field
  }
  softDeleteCif(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/cif/${id}/soft-delete`, {});
  }
  
}