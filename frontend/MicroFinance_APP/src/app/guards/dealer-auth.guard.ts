import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class DealerAuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    console.log('DealerAuthGuard checking...');
    const isDealer = this.authService.isDealer();
    console.log('Is Dealer:', isDealer);
    if (!isDealer) {
      this.router.navigate(['/login']);
      return false;
    }
    return true;
  }
}