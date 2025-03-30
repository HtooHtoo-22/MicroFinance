import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { StoreService } from './store.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private loggedIn = new BehaviorSubject<boolean>(false);
  private emailSubject = new BehaviorSubject<string | null>(null);
  private apiUrl = 'http://localhost:8081/api/v1/auth';  // Base URL for auth endpoints

  constructor(
    private http: HttpClient,
    private router: Router,
    private storageService: StoreService
  ) {
    const token = this.getAccessToken();
    this.loggedIn.next(!!token);

    // Initialize email from storage
    const storedEmail = this.storageService.getItem('email');
    if (storedEmail) {
      this.emailSubject.next(storedEmail);
    }
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
          this.setCurrentUserEmail(username);

          if (response.role === 'DEALER' && response.dealerId) {
            console.log('Setting dealerId:', response.dealerId); // Debug log
            this.storageService.setItem('dealerId', response.dealerId);
          }
        }
      })
    );
  }


  // In auth.service.ts
getCurrentUserRoleName(): string {
  const role = this.getCurrentUserRole();
  switch(role) {
    case 'ADMIN': return 'Administrator';
    case 'MANAGER': return 'Manager';
    case 'ENTRY': return 'Entry User';
    case 'OPERATION': return 'Operation User';
    case 'DEALER': return 'Dealer';
    default: return 'User';
  }
}

  getCurrentDealerId(): string | null {
    const dealerId = this.storageService.getItem('dealerId');
    console.log('Retrieved dealerId:', dealerId); // Debug log
    return dealerId;
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

  // Email handling
  setCurrentUserEmail(email: string): void {
    this.emailSubject.next(email);
    this.storageService.setItem('email', email);
  }

  getCurrentUserEmail(): Observable<string | null> {
    return this.emailSubject.asObservable();
  }

  getStoredEmail(): string | null {
    return this.storageService.getItem('email');
  }

  isLoggedIn() {
    return this.loggedIn.asObservable();
  }

  setTokens(accessToken: string, refreshToken: string) {
    this.storageService.setItem('access_token', accessToken);
    this.storageService.setItem('refresh_token', refreshToken);
    this.loggedIn.next(true);
  }

  logout(): Observable<any> {
    // First clear all local data for immediate UI response
    this.clearAuthData();
    
    // Make API call to backend logout endpoint
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${this.getAccessToken()}`
    });

    return this.http.post(`${this.apiUrl}/logout`, {}, { headers }).pipe(
      tap(() => {
        // On successful logout
        this.postLogoutCleanup();
      }),
      catchError(error => {
        // Even if API call fails, ensure client is logged out
        this.postLogoutCleanup();
        return throwError(error);
      })
    );
  }

  private postLogoutCleanup(): void {
    this.clearAuthData();
    this.loggedIn.next(false);
    this.emailSubject.next(null);
    this.router.navigate(['/login']);
  }

  private clearAuthData(): void {
    // Clear all stored authentication data
    this.storageService.removeItem('access_token');
    this.storageService.removeItem('refresh_token');
    this.storageService.removeItem('userId');
    this.storageService.removeItem('branchId');
    this.storageService.removeItem('role');
    this.storageService.removeItem('dealerId');
    this.storageService.removeItem('email');
  }

  getAccessToken() {
    return this.storageService.getItem('access_token');
  }

  getRefreshToken() {
    return this.storageService.getItem('refresh_token');
  }

  isAdmin(): boolean {
    return this.getCurrentUserRole() === 'ADMIN';
  }

  isManager(): boolean {
    return this.getCurrentUserRole() === 'MANAGER';
  }

  isEntry(): boolean {
    return this.getCurrentUserRole() === 'ENTRY';
  }

  isOperation(): boolean {
    return this.getCurrentUserRole() === 'OPERATION';
  }

  isDealer(): boolean {
    return this.getCurrentUserRole() === 'DEALER';
  }
}