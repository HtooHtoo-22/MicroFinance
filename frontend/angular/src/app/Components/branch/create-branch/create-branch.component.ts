import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BranchService } from '../../../services/branch.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-branch',
  standalone: false,
  templateUrl: './create-branch.component.html',
  styleUrl: './create-branch.component.css'
})
export class CreateBranchComponent implements OnInit {
  branchForm!: FormGroup;

  constructor(private fb: FormBuilder, private branchService: BranchService, private router: Router) {}

  ngOnInit(): void {
    this.branchForm = this.fb.group({
      code: ['', Validators.required],
      name: ['', Validators.required],
      address: ['', Validators.required],
      state: ['', Validators.required],
      township: ['', Validators.required],
      status: ['ACTIVE', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.branchForm.valid) {
      this.branchService.createBranch(this.branchForm.value).subscribe({
        next: (response) => {
          console.log('Branch created successfully', response);
          alert('Branch created successfully');
          this.router.navigate(['/list-branch']);
        },
        error: (err) => {
          console.error('Error creating branch', err);
          alert('Failed to create branch');
        }
      });
    }
  }
}