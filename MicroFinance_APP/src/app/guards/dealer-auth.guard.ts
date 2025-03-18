import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class DealerAuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    if (this.authService.isDealer()) { // Check if the user is a dealer
      return true; // Allow access to the route
    } else {
      this.router.navigate(['/login']); // Redirect to the normal dashboard
      return false; // Block access to the route
    }
  }
}