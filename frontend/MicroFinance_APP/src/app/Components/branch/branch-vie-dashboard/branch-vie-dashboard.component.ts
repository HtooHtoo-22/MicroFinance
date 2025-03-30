import { Component, OnInit } from '@angular/core';
import { BranchService } from '../../../service/branch.service';
import { Router } from '@angular/router';
import { Branch } from '../../../model/Branch';
import { Observable, forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { UserService } from '../../../service/user.service';
import { CurrentAccService } from '../../../service/current-acc.service';

@Component({
  selector: 'app-branch-vie-dashboard',
  standalone: false,
  templateUrl: './branch-vie-dashboard.component.html',
  styleUrls: ['./branch-vie-dashboard.component.css']
})
export class BranchVieDashboardComponent implements OnInit {
  branches: Branch[] = [];
  loading = false;
  loadingCounts = false;
  errorMessage: string | null = null;
  
  // Pagination
  currentPage: number = 1;
  itemsPerPage: number = 7;
  totalBranches: number = 0;
  totalPages: number = 0;

  constructor(
    private branchService: BranchService,
    private userService: UserService,
    private currentAccountService: CurrentAccService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadBranches();
  }

  loadBranches(): void {
    this.loading = true;
    this.errorMessage = null;
    
    this.branchService.getBranches().subscribe({
      next: (data) => {
        this.branches = data;
        this.totalBranches = data.length;
        this.totalPages = Math.ceil(this.totalBranches / this.itemsPerPage);
        this.loadActiveCounts();
      },
      error: (error) => {
        console.error('Error fetching branches:', error);
        this.errorMessage = 'Failed to load branches. Please try again later.';
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  loadActiveCounts(): void {
    this.loadingCounts = true;
    
    const requests = this.branches.map(branch => {
      return forkJoin({
        userCount: this.userService.getActiveUserCount(branch.id),
        accountCount: this.currentAccountService.getActiveCurrentAccountCount(branch.id)
      }).pipe(
        finalize(() => {
          const allLoaded = this.branches.every(b => 
            b.activeUserCount !== undefined && 
            b.activeAccountCount !== undefined
          );
          if (allLoaded) {
            this.loadingCounts = false;
          }
        })
      );
    });

    requests.forEach((request, index) => {
      request.subscribe({
        next: (counts: {userCount: number, accountCount: number}) => {
          this.branches[index].activeUserCount = counts.userCount;
          this.branches[index].activeAccountCount = counts.accountCount;
        },
        error: (error: any) => {
          console.error(`Error fetching counts for branch ${this.branches[index].id}:`, error);
          this.branches[index].activeUserCount = 0;
          this.branches[index].activeAccountCount = 0;
        }
      });
    });
  }

  deleteBranch(id: number): void {
    if (confirm('Are you sure you want to delete this branch?')) {
      this.loading = true;
      this.errorMessage = null;

      this.branchService.deleteBranch(id).subscribe({
        next: (response: any) => {
          this.loading = false;
          if (response.statusCode === 200) {
            alert('Branch deleted successfully');
            this.loadBranches();
          } else {
            this.errorMessage = response.message || 'Failed to delete branch';
          }
        },
        error: (error: any) => {
          this.loading = false;
          console.error('Error deleting branch:', error);
          this.errorMessage = 'Failed to delete branch. Please try again.';
        }
      });
    }
  }

  // Pagination methods
  get paginatedBranches(): Branch[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.branches.slice(start, start + this.itemsPerPage);
  }

  get startIndex(): number {
    return (this.currentPage - 1) * this.itemsPerPage + 1;
  }

  get endIndex(): number {
    return Math.min(this.currentPage * this.itemsPerPage, this.totalBranches);
  }

  get pages(): number[] {
    return Array.from({length: this.totalPages}, (_, i) => i + 1);
  }

  previousPage(): void {
    if (this.currentPage > 1) this.currentPage--;
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) this.currentPage++;
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) this.currentPage = page;
  }
}