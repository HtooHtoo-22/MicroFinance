// permission.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Permission } from '../model/Permission';

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  private apiUrl = 'http://localhost:8081/api/permissions';

  constructor(private http: HttpClient) { }

  getAllPermissions(): Observable<string[]> {
    return this.http.get<Permission[]>(`${this.apiUrl}/list`).pipe(
      map(permissions => permissions.map(p => p.permissionName))
    );
  }
}