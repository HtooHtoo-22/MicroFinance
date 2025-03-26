import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-dealer-dashboard',
  standalone: false,
  templateUrl: './dealer-dashboard.component.html',
  styleUrl: './dealer-dashboard.component.css'
})
export class DealerDashboardComponent {
  imagePath: string = "image/richcon-logo.png";
currentAccountId: any|string;
showLogoutModal: boolean = false;


constructor(private router: Router, private authService: AuthService) {}

navigateToProductList() {
  console.log('Navigating to product list');
  this.router.navigate(['/dealer-dashboard/product-list']).then(success => {
    console.log('Navigation success:', success);
  }).catch(err => {
    console.error('Navigation error:', err);
  });
}

openLogoutModal(): void {
  this.showLogoutModal = true;
}

closeLogoutModal(): void {
  this.showLogoutModal = false;
}

confirmLogout(): void {
  this.authService.logout().subscribe({
    next: () => {
      console.log('Logged out successfully');
      this.showLogoutModal = false;
    },
    error: (err) => {
      console.error('Logout failed:', err);
      this.showLogoutModal = false;
    }
  });
}
}
