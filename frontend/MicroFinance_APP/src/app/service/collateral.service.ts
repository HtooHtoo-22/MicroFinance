import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CollateralTypeDTO } from '../model/Collateral';
import { map, Observable } from 'rxjs';
import { CollateralDTO } from '../model/CollateralDTO';
import { ApiResponse } from '../model/ApiResponse';

@Injectable({
  providedIn: 'root'
})
export class CollateralService {
  private apiUrl = 'http://localhost:8081/api/collateral-types';
  private apiUrl2 = 'http://localhost:8081/api/collaterals';
  constructor(private http: HttpClient) { }

  getAllCollateralTypes(): Observable<CollateralTypeDTO[]> {
    return this.http.get<{ data: CollateralTypeDTO[] }>(`${this.apiUrl}/all`).pipe(
      map((response: { data: CollateralTypeDTO[] }) => response.data)
    );
  }

  getCollateralTypeById(id: number): Observable<CollateralTypeDTO> {
    return this.http.get<{ data: CollateralTypeDTO }>(`${this.apiUrl}/${id}`).pipe(
      map((response: { data: CollateralTypeDTO }) => response.data)
    );
  }

  createCollateralType(collateralType: CollateralTypeDTO): Observable<CollateralTypeDTO> {
    return this.http.post<CollateralTypeDTO>(`${this.apiUrl}/create`, collateralType);
  }
  createCollateral(collateral: CollateralDTO): Observable<ApiResponse<any>> {
    const formData = new FormData();
    formData.append("value", collateral.value?.toString() || '');
    formData.append("description", collateral.description || '');
    formData.append("address", collateral.address || '');
    formData.append("collateralTypeId", collateral.collateralTypeId?.toString() || '');
    formData.append("currentAccountId", collateral.currentAccountId?.toString() || '');
  
    if (collateral.imageFile) {
      formData.append("imageFile", collateral.imageFile);
    }
    formData.append("name", collateral.name || '');
  
    return this.http.post<ApiResponse<any>>(`${this.apiUrl2}/create`, formData);
  }
  

  updateCollateralType(id: number, collateralType: CollateralTypeDTO): Observable<CollateralTypeDTO> {
    return this.http.put<CollateralTypeDTO>(`${this.apiUrl}/${id}`, collateralType);
  }

  deleteCollateralType(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
  
  getAllCollaterals(): Observable<ApiResponse<CollateralDTO[]>> {
    return this.http.get<ApiResponse<CollateralDTO[]>>(`${this.apiUrl2}/list`);
  }
  getCollateralById(id: number): Observable<ApiResponse<CollateralDTO>> {
    return this.http.get<ApiResponse<CollateralDTO>>(`${this.apiUrl2}/${id}`);
  }
  getCollateralByCurrentAccountId(id: string): Observable<ApiResponse<CollateralDTO>> {
    return this.http.get<ApiResponse<CollateralDTO>>(`${this.apiUrl2}/getByAcc/${id}`);
  }

  getCollateralByBranchId(id: number): Observable<ApiResponse<CollateralDTO[]>> {
    return this.http.get<ApiResponse<CollateralDTO[]>>(`${this.apiUrl2}/getByBranchId/${id}`);
  }
}