import { Component, OnInit } from '@angular/core';
import { BranchService } from '../../../service/branch.service';
import { UserService } from '../../../service/user.service';
import { CurrentAccService } from '../../../service/current-acc.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Branch } from '../../../model/user';
import { User } from '../../../model/user';
import { CurrentAccount } from '../../../model/current-account';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-branch-view-detail',
  standalone: false,
  templateUrl: './branch-view-detail.component.html',
  styleUrls: ['./branch-view-detail.component.css']
})
export class BranchViewDetailComponent implements OnInit {
  branch: Branch | null = null;
  users: User[] = [];
  accounts: CurrentAccount[] = [];
  loading = false;
  loadingUsers = false;
  loadingAccounts = false;
  errorMessage: string | null = null;
  branchId: number = 0;

  // Pagination for users
  currentUserPage = 1;
  itemsPerUserPage = 5;
  totalUsers = 0;

  // Pagination for accounts
  currentAccountPage = 1;
  itemsPerAccountPage = 5;
  totalAccounts = 0;

  constructor(
    private branchService: BranchService,
    private userService: UserService,
    private currentAccService: CurrentAccService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.branchId = +params['id'];
      this.loadBranchDetails();
      this.loadBranchUsers();
      this.loadBranchAccounts();
    });
  }

  loadBranchDetails(): void {
    this.loading = true;
    this.errorMessage = null;
    
    this.branchService.getBranch(this.branchId)
      .pipe(
        finalize(() => this.loading = false)
      )
      .subscribe({
        next: (data) => {
          this.branch = data;
        },
        error: (error) => {
          console.error('Error fetching branch details:', error);
          this.errorMessage = 'Failed to load branch details. Please try again later.';
        }
      });
  }

  loadBranchUsers(): void {
    this.loadingUsers = true;
    this.userService.getActiveUserCount(this.branchId)
      .pipe(
        finalize(() => this.loadingUsers = false)
      )
      .subscribe({
        next: (response) => {
          this.users = response.data;
          this.totalUsers = response.total;
        },
        error: (error) => {
          console.error('Error fetching branch users:', error);
        }
      });
  }

  loadBranchAccounts(): void {
    this.loadingAccounts = true;
    this.currentAccService.getActiveCurrentAccountCount(this.branchId, this.currentAccountPage, this.itemsPerAccountPage)
      .pipe(
        finalize(() => this.loadingAccounts = false)
      )
      .subscribe({
        next: (response) => {
          this.accounts = response.data;
          this.totalAccounts = response.total;
        },
        error: (error) => {
          console.error('Error fetching branch accounts:', error);
        }
      });
  }

  // User pagination
  onUserPageChange(page: number): void {
    this.currentUserPage = page;
    this.loadBranchUsers();
  }

  // Account pagination
  onAccountPageChange(page: number): void {
    this.currentAccountPage = page;
    this.loadBranchAccounts();
  }

  goBack(): void {
    this.router.navigate(['/admin-dashboard/branch-list']);
  }
}