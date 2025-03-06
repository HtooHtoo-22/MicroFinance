import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Cif } from '../model/CIF';
import { AuthService } from './auth.service';
import { ApiResponse } from '../model/Apirespon';

@Injectable({
  providedIn: 'root'
})
export class CifService {
  private apiUrl = 'http://localhost:8081/api/cif';

  constructor(private http: HttpClient, private authService: AuthService) {}

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

  listCif(): Observable<ApiResponse<Cif[]>> {
    const branchId = this.authService.getCurrentUserBranchId();
    const isAdmin = this.authService.getCurrentUserRole() === 'ADMIN'; // Add a method to get the user's role

    if (isAdmin) {
      return this.http.get<ApiResponse<Cif[]>>(`${this.apiUrl}/list`);
    } else {
      return this.http.get<ApiResponse<Cif[]>>(`${this.apiUrl}/list?branchId=${branchId}`);
    }
  }

  checkNRC(nrc: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/check-nrc?nrc=${nrc}`);
  }

  checkEmail(email: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/check-email?email=${email}`);
  }
}
