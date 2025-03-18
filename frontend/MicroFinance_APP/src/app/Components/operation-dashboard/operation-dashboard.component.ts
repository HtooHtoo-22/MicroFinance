import { Component } from '@angular/core';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-operation-dashboard',
  standalone: false,
  templateUrl: './operation-dashboard.component.html',
  styleUrl: './operation-dashboard.component.css'
})
export class OperationDashboardComponent {
  isCustomerDropdownOpen: boolean = false;
  imagePath: string = "image/richcon-logo.png";

  constructor(private authService: AuthService) {}

  toggleCustomerDropdown(): void {
    this.isCustomerDropdownOpen = !this.isCustomerDropdownOpen;
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