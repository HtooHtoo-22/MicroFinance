import { Component } from '@angular/core';
import { AuthService } from '../../service/auth.service';
import { StoreService } from '../../service/store.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: false,
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent {
  isRoleDropdownOpen: boolean = false;
  isCustomerDropdownOpen: boolean = false;
  isUserDropdownOpen: boolean = false;
  isLoanDropdownOpen: boolean = false;
  isBranchDropdownOpen: boolean = false;
  isRateDropdownOpen: boolean = false;
  isCifDropdownOpen: boolean = false;
  isDealerDropdownOpen: boolean = false;
  isACCDropdownOpen: boolean = false;
  imagePath: string = "image/richcon-logo.png";
  showLogoutModal: boolean = false;

  constructor(private authService: AuthService, private storageServie: StoreService) {}

  getCurrentRole(): string | null {
    return this.authService.getCurrentUserRole();
  }
  
  getCurrentRoleName(): string {
    return this.authService.getCurrentUserRoleName();
  }
  
  getCurrentEmail(): string | null {
    return this.authService.getStoredEmail();
  }


  toggleRoleDropdown(): void {
    this.isRoleDropdownOpen = !this.isRoleDropdownOpen;
  }

  toggleCustomerDropdown(): void {
    this.isCustomerDropdownOpen = !this.isCustomerDropdownOpen;
  }

  toggleUserDropdown(): void {
    this.isUserDropdownOpen = !this.isUserDropdownOpen;
  }

  toggleLoanDropdown(): void {
    this.isLoanDropdownOpen = !this.isLoanDropdownOpen;
  }

  toggleBranchDropdown(): void {
    this.isBranchDropdownOpen = !this.isBranchDropdownOpen;
  }

  toggleRateDropdown(): void {
    this.isRateDropdownOpen = !this.isRateDropdownOpen;
  }

  toggleCifDropdown(): void {
    this.isCifDropdownOpen = !this.isCifDropdownOpen;
  }

  toggleDealerDropdown(): void {
    this.isDealerDropdownOpen = !this.isDealerDropdownOpen;
  }

  toggleACCDropdown(): void {
    this.isACCDropdownOpen = !this.isACCDropdownOpen;
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  isManager(): boolean {
    return this.authService.isManager();
  }

  isEntry(): boolean {
    return this.authService.isEntry();
  }

  isOperation(): boolean {
    return this.authService.isOperation();
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