import { Component, OnInit } from '@angular/core';
import { Branch } from '../../../model/Branch';
import { BranchService } from '../../../service/branch.service';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-branch-list',
  standalone: false,
  templateUrl: './branch-list.component.html',
  styleUrl: './branch-list.component.css'
})
export class BranchListComponent implements OnInit {
  branches: Branch[] = [];
  loading = false;
  errorMessage: string | null = null;
  
  // Pagination
  currentPage: number = 1;
  itemsPerPage: number = 7;
  totalBranches: number = 0;
  totalPages: number = 0;

  constructor(
    private branchService: BranchService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadBranches();
  }

  loadBranches(): void {
    this.loading = true;
    this.errorMessage = null;
    
    this.branchService.getBranches()
      .pipe(
        finalize(() => this.loading = false)
      )
      .subscribe({
        next: (data) => {
          this.branches = data;
          this.totalBranches = data.length;
          this.totalPages = Math.ceil(this.totalBranches / this.itemsPerPage);
        },
        error: (error) => {
          console.error('Error fetching branches:', error);
          this.errorMessage = 'Failed to load branches. Please try again later.';
        }
      });
  }

  deleteBranch(id: number): void {
    if (confirm('Are you sure you want to delete this branch?')) {
      this.loading = true;
      this.errorMessage = null;

      this.branchService.deleteBranch(id).subscribe({
        next: (response) => {
          this.loading = false;
          if (response.statusCode === 200) {
            alert('Branch deleted successfully');
            this.loadBranches(); // Refresh the list
          } else {
            this.errorMessage = response.message || 'Failed to delete branch';
          }
        },
        error: (error) => {
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
