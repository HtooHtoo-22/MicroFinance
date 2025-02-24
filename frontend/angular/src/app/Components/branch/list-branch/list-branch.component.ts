import { Component, OnInit } from '@angular/core';
import { map, Observable } from 'rxjs';
import { BranchService } from '../../../services/branch.service';
import { Branch } from '../../../models/branch';
import { Router } from '@angular/router';

@Component({
  selector: 'app-list-branch',
  standalone: false,
  templateUrl: './list-branch.component.html',
  styleUrl: './list-branch.component.css'
})
export class ListBranchComponent  implements OnInit {
 
goToUpdate() {
throw new Error('Method not implemented.');
}
updateBranch(arg0: number|undefined) {
throw new Error('Method not implemented.');
}
  branches$: Observable<Branch[]> | undefined;

  constructor(
    private branchService: BranchService,
    private router: Router
  ) {}

  ngOnInit(): void {
    
    this.branches$ = this.branchService.getBranches().pipe(
      map((response: any) => response.data || [])
    );
}
// onUpdate(branchId: number): void {
//   this.router.navigate(['/update-branch', branchId]);
// }

logBranchId(id: number | undefined) {
  console.log('Navigating to branch ID:', id);
}


onDelete(branchId: number | undefined): void {
  if (!branchId) {
    console.error('Branch ID is undefined');
    return;
  }
  if (confirm('Are you sure you want to delete this branch?')) {
    this.branchService.deleteBranch(branchId).subscribe({
      next: () => {
        alert('Branch deleted successfully');
        this.ngOnInit(); // Refresh the list
      },
      error: (err: any) => {
        console.log('Raw error:', err); // Print the raw error
        if (err && typeof err === 'object') {
          console.error('Delete failed:', err);
          alert(`Failed to delete branch: ${err.message || JSON.stringify(err)}`);
        } else {
          console.error('Delete failed. Error is not an object:', err);
          alert(`Failed to delete branch: ${String(err)}`);
        }
      }
      
      
    });
  }
}
}