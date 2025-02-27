import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Cif } from '../model/CIF';

@Injectable({
  providedIn: 'root'
})
export class CifService {
  private apiUrl = 'http://localhost:8081/api/cif';

  constructor(private http: HttpClient) {}

  getCifById(id: number): Observable<Cif> {
    return this.http.get<{ data: Cif }>(`${this.apiUrl}/${id}`).pipe(
      map(response => {
        console.log("API Response:", response); 
        return response.data; 
      })
    );
  }
  

  createCif(cifData: Cif, frontNRC: File, backNRC: File, userPhoto: File): Observable<any> {
    const formData = new FormData();
    formData.append('userName', cifData.userName);
    formData.append('gender', cifData.gender);
    formData.append('job', cifData.job);
    formData.append('incomeAmount', cifData.incomeAmount.toString());
    formData.append('nrc', cifData.nrc);
    formData.append('phone', cifData.phone);
    formData.append('email', cifData.email);
    formData.append('state', cifData.state);
    formData.append('township', cifData.township);
    formData.append('address', cifData.address);
    formData.append('frontNRC', frontNRC);
    formData.append('backNRC', backNRC);
    formData.append('userPhoto', userPhoto);
    formData.append('branchName', cifData.branchName);
    formData.append('userFullName', cifData.userFullName);

   

    return this.http.post(`${this.apiUrl}`, formData);
}

  updateCif(id: number, cifData: Cif): Observable<any> {
    const updatedData = {
      ...cifData,
      incomeAmount: Number(cifData.incomeAmount) 
    };
    return this.http.patch(`${this.apiUrl}/${id}`, updatedData);
  }
  
  listCif(): Observable<{ data: Cif[] }> {
    return this.http.get<{ data: Cif[] }>(`${this.apiUrl}/list`);
  }
}