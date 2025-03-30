import { Component, OnInit } from '@angular/core';
import { BranchService } from '../../../service/branch.service';
import { UserService } from '../../../service/user.service';
import { CurrentAccService } from '../../../service/current-acc.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Branch } from '../../../model/user';
import { finalize } from 'rxjs/operators';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-branch-view-detail',
  standalone: false,
  templateUrl: './branch-view-detail.component.html',
  styleUrls: ['./branch-view-detail.component.css']
})
export class BranchViewDetailComponent implements OnInit {
  branch: Branch | null = null;
  activeUsersCount: number = 0;
  activeAccountsCount: number = 0;
  loading = false;
  loadingCounts = false;
  errorMessage: string | null = null;
  branchId: number = 0;

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
          this.loadActiveCounts();
        },
        error: (error: any) => {
          console.error('Error fetching branch details:', error);
          this.errorMessage = 'Failed to load branch details. Please try again later.';
        }
      });
  }

  loadActiveCounts(): void {
    this.loadingCounts = true;
    
    forkJoin([
      this.userService.getActiveUserCount(this.branchId),
      this.currentAccService.getActiveCurrentAccountCount(this.branchId)
    ]).pipe(
      finalize(() => this.loadingCounts = false)
    ).subscribe({
      next: ([userCount, accountCount]: [number, number]) => {
        this.activeUsersCount = userCount;
        this.activeAccountsCount = accountCount;
      },
      error: (error: any) => {
        console.error('Error fetching counts:', error);
        this.errorMessage = 'Failed to load activity counts. Please try again later.';
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/admin-dashboard/branch-list']);
  }
}