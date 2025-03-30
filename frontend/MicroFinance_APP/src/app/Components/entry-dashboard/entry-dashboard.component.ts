import { Component } from '@angular/core';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-entry-dashboard',
  standalone: false,
  templateUrl: './entry-dashboard.component.html',
  styleUrl: './entry-dashboard.component.css'
})
export class EntryDashboardComponent {
  isCustomerDropdownOpen: boolean = false;
  isLoanDropdownOpen: boolean = false;
  isCifDropdownOpen: boolean = false;
  isDealerDropdownOpen: boolean = false;
  isACCDropdownOpen: boolean = false;
  imagePath: string = "image/richcon-logo.png";
  showLogoutModal: boolean = false;

  constructor(private authService: AuthService) {}

  getCurrentRole(): string | null {
    return this.authService.getCurrentUserRole();
  }
  
  getCurrentRoleName(): string {
    return this.authService.getCurrentUserRoleName();
  }
  
  getCurrentEmail(): string | null {
    return this.authService.getStoredEmail();
  }

  toggleCustomerDropdown(): void {
    this.isCustomerDropdownOpen = !this.isCustomerDropdownOpen;
  }

  toggleLoanDropdown(): void {
    this.isLoanDropdownOpen = !this.isLoanDropdownOpen;
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