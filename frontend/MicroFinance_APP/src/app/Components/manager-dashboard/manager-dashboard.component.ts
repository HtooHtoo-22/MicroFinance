import { Component } from '@angular/core';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-manager-dashboard',
  standalone: false,
  templateUrl: './manager-dashboard.component.html',
  styleUrl: './manager-dashboard.component.css'
})
export class ManagerDashboardComponent {
  isCustomerDropdownOpen: boolean = false;
  isLoanDropdownOpen: boolean = false;
  isCifDropdownOpen: boolean = false;
  isDealerDropdownOpen: boolean = false;
  isACCDropdownOpen: boolean = false;
  imagePath: string = "image/richcon-logo.png";

  constructor(private authService: AuthService) {}

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
}