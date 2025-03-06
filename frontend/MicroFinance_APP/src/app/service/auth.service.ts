import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, tap } from 'rxjs';
import { StoreService } from './store.service';
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private loggedIn = new BehaviorSubject<boolean>(false);
  private apiUrl = 'http://localhost:8081/api/v1/auth';  // Base URL for auth endpoints


  constructor(
    private http: HttpClient,
    private router: Router,
    private storageService: StoreService
  ) {
    const token = this.getAccessToken();
    this.loggedIn.next(!!token);
  }
  login(username: string, password: string) {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');
    return this.http.post<any>(
      `${this.apiUrl}/authenticate`, 
      { username, password },
      { headers }
    ).pipe(
      tap(response => {
        if (response.access_token) {
          this.setTokens(response.access_token, response.refresh_token);

          // Ensure these lines are executed
          this.storageService.setItem('userId', response.userId);
          this.storageService.setItem('branchId', response.branchId);
          this.storageService.setItem('role', response.role); // This should set the role
        }
      })
    );
}

  getCurrentUserId(): string | null {
    return this.storageService.getItem('userId');
  }

  getCurrentUserBranchId(): string | null {
    return this.storageService.getItem('branchId');
  }

  getCurrentUserRole(): string | null {
    return this.storageService.getItem('role'); // Ensure this matches the key used in setItem
}

  logout() {
    this.storageService.removeItem('access_token');
    this.storageService.removeItem('refresh_token');
    this.storageService.removeItem('userId');
    this.loggedIn.next(false);
    this.router.navigate(['/login']);
  }

  isLoggedIn() {
    return this.loggedIn.asObservable();
  }

  setTokens(accessToken: string, refreshToken: string) {
    this.storageService.setItem('access_token', accessToken);
    this.storageService.setItem('refresh_token', refreshToken);
    this.loggedIn.next(true);
  }


  getAccessToken() {
    return this.storageService.getItem('access_token');
  }

  getRefreshToken() {
    return this.storageService.getItem('refresh_token');
  }
}
