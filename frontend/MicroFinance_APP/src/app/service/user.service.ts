import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';
import { Branch, Role, UserDTO, UserResponseDTO } from '../model/user';
import { ApiResponse } from '../model/ApiResponse';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiUrl = 'http://localhost:8081/api';

  constructor(private http: HttpClient) { }

  createUser(user: UserDTO): Observable<ApiResponse<UserResponseDTO>> {
    return this.http.post<ApiResponse<UserResponseDTO>>(`${this.apiUrl}/users/create`, user)
      .pipe(
        catchError(this.handleError)
      );
  }

  getBranches(): Observable<Branch[]> {
    return this.http.get<ApiResponse<Branch[]>>(`${this.apiUrl}/branches/list`)
      .pipe(
        map(response => {
          if (!response.data || response.data.length === 0) {
            throw new Error('No branches available');
          }
          return response.data;
        }),
        catchError(this.handleError)
      );
  }

  getRoles(): Observable<Role[]> {
    return this.http.get<ApiResponse<Role[]>>(`${this.apiUrl}/roles`)
      .pipe(
        map(response => {
          if (!response.data || response.data.length === 0) {
            throw new Error('No roles available');
          }
          return response.data;
        }),
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = error.error.message;
    } else {
      // Server-side error
      if (error.error?.message) {
        errorMessage = error.error.message;
      } else {
        switch (error.status) {
          case 400:
            errorMessage = 'Invalid data submitted. Please check your input.';
            break;
          case 403:
            errorMessage = 'You do not have permission to perform this action. Please contact your administrator.';
            break;
          case 404:
            errorMessage = 'Resource not found.';
            break;
          default:
            errorMessage = 'An unexpected error occurred. Please try again later.';
        }
      }
    }
    
    return throwError(() => errorMessage);
  }

  getUser(id: number): Observable<UserResponseDTO> {
    return this.http.get<ApiResponse<UserResponseDTO>>(`${this.apiUrl}/users/${id}`)
      .pipe(
        map(response => {
          console.log('Raw API Response:', response); // Log the raw response
          return response.data ?? response;  // Handle cases where "data" may not exist
        }),
        catchError(this.handleError)
      );
  }
  
  

  updateUser(id: number, user: UserDTO): Observable<ApiResponse<UserResponseDTO>> {
    return this.http.put<ApiResponse<UserResponseDTO>>(`${this.apiUrl}/users/${id}`, user)
      .pipe(
        catchError(this.handleError)
      );
  }

  deleteUser(userId: string): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/users/${userId}`)
      .pipe(
        catchError(this.handleError)
      );
  }
  getUsers(): Observable<UserResponseDTO[]> {
    return this.http.get<ApiResponse<UserResponseDTO[]>>(`${this.apiUrl}/users`)
      .pipe(
        map(response => {
          if (!response.data) {
            return []; // Return empty array instead of throwing error
          }
          return response.data;
        }),
        catchError(this.handleError)
      );
  }

  getActiveUserCount(branchId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/users/active/count/${branchId}`);
  }
  
}