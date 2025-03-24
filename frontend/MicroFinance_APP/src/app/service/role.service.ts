// role.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { get } from 'http';
import { Role } from '../model/Role';

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private apiUrl = 'http://localhost:8081/api/roles';

  constructor(private http: HttpClient) {}

  getAllRoles(): Observable<Role[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      map(response => response.data) // Extract the data property
    );
  }
  
  createRole(role: Role): Observable<Role> {
    return this.http.post<any>(`${this.apiUrl}/create`, role).pipe(
      map(response => response.data)
    );
  }

  updateRole(id: number, role: Role): Observable<Role> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, role).pipe(
      map(response => response.data)
    );
  }

  getRoleById(id: number): Observable<Role> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }




}
