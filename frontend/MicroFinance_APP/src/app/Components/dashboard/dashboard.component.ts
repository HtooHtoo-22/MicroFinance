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
  isHpLoanDropdownOpen: boolean = false
  isBranchDropdownOpen: boolean = false;
  isRateDropdownOpen: boolean = false;
  isCifDropdownOpen: boolean = false;
  isDealerDropdownOpen: boolean = false;
  imagePath: string = "image/richcon-logo.png";
  isProductDropdownOpen: boolean = false;
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

  toggleHpLoanDropdown(): void {
    this.isHpLoanDropdownOpen = !this.isHpLoanDropdownOpen;
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


  toggleProductDropdown(): void{
    this.isProductDropdownOpen = !this.isProductDropdownOpen;
  }
}