import { Component, Input, OnInit } from '@angular/core';
import { CurrentAccService } from '../../../service/current-acc.service';
import { AuthService } from '../../../service/auth.service';

@Component({
  selector: 'app-branch-account-count',
  standalone: false,
  templateUrl: './branch-account-count.component.html',
  styleUrl: './branch-account-count.component.css'
})
export class BranchAccountCountComponent implements OnInit {
  accountCount: number | null = null;
  isLoading = false;
  
  constructor(
    private currentAccService: CurrentAccService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadCurrentAccountCount();
  }

  loadCurrentAccountCount(): void {
    this.isLoading = true;
    const branchId = this.authService.getCurrentUserBranchId();
    
    if (branchId) {
      this.currentAccService.getCurrentAccountCount(+branchId).subscribe({
        next: (count) => {
          this.accountCount = count;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error fetching current account count:', error);
          this.isLoading = false;
        }
      });
    }
  }
}