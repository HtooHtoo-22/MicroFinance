import { Component, Input, OnInit } from '@angular/core';
import { CurrentAccService } from '../../../service/current-acc.service';
import { BranchService } from '../../../service/branch.service';

@Component({
  selector: 'app-branch-account-count',
  standalone: false,
  templateUrl: './branch-account-count.component.html',
  styleUrl: './branch-account-count.component.css'
})
export class BranchAccountCountComponent implements OnInit {
  branches: any[] = [];
  selectedBranchId: number | null = null;
  accountCount: number | null = null;
  
  constructor(private currentAccService: CurrentAccService,private branchService: BranchService) {}

  ngOnInit(): void {
    this.loadBranches();
  }

  // Load all branches
  loadBranches(): void {
      
      
      this.branchService.getBranches()
        
        .subscribe({
          next: (data) => {
            this.branches = data;
           
          },
          error: (error) => {
            console.error('Error fetching branches:', error);
            
          }
        });
    }
  
  

  loadCurrentAccountCount(): void {
    if (this.selectedBranchId) {
      this.currentAccService.getCurrentAccountCount(this.selectedBranchId).subscribe(
        (count) => {
          this.accountCount = count;
        },
        (error) => {
          console.error('Error fetching current account count:', error);
        }
      );
    } else {
      this.accountCount = null;
    }
  }
  
}
