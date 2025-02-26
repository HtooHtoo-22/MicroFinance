import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { BranchService } from '../../../service/branch.service';
import { Branch } from '../../../model/Branch';

@Component({
  selector: 'app-update-branch',
  standalone: false,
  templateUrl: './update-branch.component.html',
  styleUrl: './update-branch.component.css'
})
export class UpdateBranchComponent implements OnInit {
  branchForm!: FormGroup;
  branchId!: number;
  loading = false;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private branchService: BranchService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.branchId = +id;
      this.loadBranchData();
    } else {
      this.errorMessage = 'Invalid branch ID';
      this.router.navigate(['/dashboard/branch-list']);
    }
  }

  private initForm(): void {
    this.branchForm = this.fb.group({
      code: ['', Validators.required],
      name: ['', Validators.required],
      address: ['', Validators.required],
      state: ['', Validators.required],
      township: ['', Validators.required],
      status: ['ACTIVE', Validators.required]
    });
  }

  private loadBranchData(): void {
    this.loading = true;
    this.branchService.getBranch(this.branchId).subscribe({
      next: (branch) => {
        this.branchForm.patchValue({
          code: branch.code,
          name: branch.name,
          address: branch.address,
          state: branch.state,
          township: branch.township,
          status: branch.status
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading branch:', error);
        this.errorMessage = 'Failed to load branch details';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.branchForm.valid) {
      this.loading = true;
      const updatedBranch: Branch = {
        id: this.branchId,
        ...this.branchForm.value
      };
  
      this.branchService.updateBranch(this.branchId, updatedBranch).subscribe({
        next: () => {
          alert('Branch updated successfully');
          this.router.navigate(['/dashboard/branch-list']);
        },
        error: (error) => {
          console.error('Error updating branch:', error);
          this.errorMessage = 'Failed to update branch';
          this.loading = false;
        }
      });
    }
  }
}
