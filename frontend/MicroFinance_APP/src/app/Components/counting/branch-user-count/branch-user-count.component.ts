import { Component, OnInit } from '@angular/core';
import { BranchService } from '../../../service/branch.service';
import { UserService } from '../../../service/user.service';
import { AuthService } from '../../../service/auth.service';

@Component({
  selector: 'app-branch-user-count',
  standalone: false,
  templateUrl: './branch-user-count.component.html',
  styleUrl: './branch-user-count.component.css'
})
export class BranchUserCountComponent implements OnInit {
  activeUserCount: number | null = null;
  isLoading = false;

  constructor(
    private userService: UserService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadActiveUserCount();
  }

  loadActiveUserCount(): void {
    this.isLoading = true;
    const branchId = this.authService.getCurrentUserBranchId();
    
    if (branchId) {
      this.userService.getActiveUserCount(+branchId).subscribe({
        next: (count) => {
          this.activeUserCount = count;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error fetching active user count:', error);
          this.isLoading = false;
        }
      });
    }
  }
}