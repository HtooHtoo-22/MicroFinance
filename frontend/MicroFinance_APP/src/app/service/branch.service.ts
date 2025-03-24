import { Injectable } from '@angular/core';
import { Branch } from '../model/Branch';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

interface ApiResponse<T> {
  httpStatus: string;
  statusCode: number;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class BranchService {
  private apiUrl = 'http://localhost:8081/api/branches';

  constructor(private http: HttpClient) {}

  getBranches(): Observable<Branch[]> {
    return this.http.get<ApiResponse<Branch[]>>(`${this.apiUrl}/list`)
      .pipe(
        map(response => response.data)
      );
  }

  createBranch(branch: Branch): Observable<Branch> {
    return this.http.post<ApiResponse<Branch>>(`${this.apiUrl}/create`, branch)
      .pipe(
        map(response => response.data)
      );
  }

  getBranch(id: number): Observable<Branch> {
    return this.http.get<ApiResponse<Branch>>(`${this.apiUrl}/${id}`)
      .pipe(
        map(response => response.data)
      );
  }

  updateBranch(id: number, branch: Branch): Observable<Branch> {
    return this.http.put<ApiResponse<Branch>>(`${this.apiUrl}/${id}`, branch)
      .pipe(
        map(response => response.data)
      );
  }

  deleteBranch(id: number): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(`${this.apiUrl}/${id}`);
  }
}
