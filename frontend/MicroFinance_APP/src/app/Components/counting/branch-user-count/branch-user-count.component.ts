import { Component, OnInit } from '@angular/core';
import { BranchService } from '../../../service/branch.service';
import { UserService } from '../../../service/user.service';

@Component({
  selector: 'app-branch-user-count',
  standalone: false,
  templateUrl: './branch-user-count.component.html',
  styleUrl: './branch-user-count.component.css'
})
export class BranchUserCountComponent implements OnInit {
  branches: any[] = [];
  selectedBranchId: number | null = null;
  activeUserCount: number | null = null;

  constructor(
    private branchService: BranchService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadBranches();
  }

  loadBranches(): void {
    this.branchService.getBranches().subscribe({
      next: (data) => (this.branches = data),
      error: (error) => console.error('Error fetching branches:', error),
    });
  }

  loadActiveUserCount(): void {
    if (this.selectedBranchId) {
      this.userService.getActiveUserCount(this.selectedBranchId).subscribe({
        next: (count) => (this.activeUserCount = count),
        error: (error) => console.error('Error fetching active user count:', error),
      });
    } else {
      this.activeUserCount = null;
    }
  }
}