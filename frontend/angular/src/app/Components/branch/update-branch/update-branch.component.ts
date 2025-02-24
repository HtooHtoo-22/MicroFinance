import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { BranchService } from '../../../services/branch.service';
import { Branch } from '../../../models/branch';

@Component({
  selector: 'app-update-branch',
   standalone: false,
  templateUrl: './update-branch.component.html',
  styleUrls: ['./update-branch.component.css']
})
export class UpdateBranchComponent implements OnInit {
  branchForm: FormGroup;
  branchId!: number;

  constructor(
    private fb: FormBuilder,
    private branchService: BranchService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.branchForm = this.fb.group({
      code: ['', Validators.required],
      name: ['', Validators.required],
      address: ['', Validators.required],
      state: ['', Validators.required],
      township: ['', Validators.required],
      status: ['ACTIVE', Validators.required]
    });
    this.branchId = 0;
  }

  ngOnInit(): void {
    this.branchId = +this.route.snapshot.paramMap.get('id')!;
    console.log('Branch ID:', this.branchId); // Log branch ID
    if (this.branchId) {
      this.loadBranchData();
    }
  }

  loadBranchData(): void {
    this.branchService.getBranch(this.branchId).subscribe((branch: Branch) => {
      this.branchForm.patchValue(branch);
    });
  }

  onSubmit(): void {
    if (this.branchForm.valid) {
      this.branchService.updateBranch(this.branchId, this.branchForm.value).subscribe({
        next: () => {
          alert('Branch updated successfully');
          this.router.navigate(['/list-branch']);
        },
        error: (err) => {
          console.error('Update failed', err);
          alert('Failed to update branch');
        }
      });
    }
  }
}