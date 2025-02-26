import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CollateralTypeDTO } from '../model/Collateral';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CollateralService {
  private apiUrl = 'http://localhost:8080/api/collateral-types';

  constructor(private http: HttpClient) { }

  getAllCollateralTypes(): Observable<CollateralTypeDTO[]> {
    return this.http.get<CollateralTypeDTO[]>(`${this.apiUrl}/all`);
  }

  getCollateralTypeById(id: number): Observable<CollateralTypeDTO> {
    return this.http.get<CollateralTypeDTO>(`${this.apiUrl}/${id}`);
  }

  createCollateralType(collateralType: CollateralTypeDTO): Observable<CollateralTypeDTO> {
    return this.http.post<CollateralTypeDTO>(`${this.apiUrl}/`, collateralType);
  }

  updateCollateralType(id: number, collateralType: CollateralTypeDTO): Observable<CollateralTypeDTO> {
    return this.http.put<CollateralTypeDTO>(`${this.apiUrl}/${id}`, collateralType);
  }

  deleteCollateralType(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
