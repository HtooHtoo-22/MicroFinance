import { Component } from '@angular/core';
import { AuthService } from '../../service/auth.service';

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
  isBranchDropdownOpen: boolean = false;
  isRateDropdownOpen: boolean = false;
  isCifDropdownOpen: boolean = false;
  isDealerDropdownOpen: boolean = false;
  isACCDropdownOpen: boolean = false;
  imagePath: string = "image/richcon-logo.png";

  constructor(private authService: AuthService) {}
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

  toggleBranchDropdown(): void{
    this.isBranchDropdownOpen = !this.isBranchDropdownOpen;
  } 

  toggleRateDropdown(): void{
    this.isRateDropdownOpen = !this.isRateDropdownOpen;
  } 

  toggleCifDropdown(): void{
    this.isCifDropdownOpen = !this.isCifDropdownOpen;
  }

  toggleDealerDropdown(): void{
    this.isDealerDropdownOpen = !this.isDealerDropdownOpen;
  }

  toggleACCDropdown(): void{
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