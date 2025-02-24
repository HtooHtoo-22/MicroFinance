import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {
  isRoleDropdownOpen: boolean = false;
  isCustomerDropdownOpen: boolean = false;
  isUserDropdownOpen: boolean = false;
  isLoanDropdownOpen: boolean = false;
  // Add method to toggle dropdown
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
}