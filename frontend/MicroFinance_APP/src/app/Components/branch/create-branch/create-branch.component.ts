import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BranchService } from '../../../service/branch.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-branch',
  standalone: false,
  templateUrl: './create-branch.component.html',
  styleUrls: ['./create-branch.component.css']
})
export class CreateBranchComponent implements OnInit {
  branchForm!: FormGroup;
  showSuccessModal = false;
  showAlert = false;
  alertType = '';
  alertMessage = '';

  constructor(private fb: FormBuilder, private branchService: BranchService, private router: Router) {}

  ngOnInit(): void {
    this.branchForm = this.fb.group({
      code: ['', [Validators.required, Validators.maxLength(5)]],
      name: ['', [Validators.required, Validators.maxLength(100)]],
      address: ['', [Validators.required, Validators.maxLength(200)]],
      state: ['', [Validators.required, Validators.maxLength(50)]],
      township: ['', [Validators.required, Validators.maxLength(50)]]
    });
  }

  onSubmit(): void {
    if (this.branchForm.valid) {
      this.branchService.createBranch(this.branchForm.value).subscribe({
        next: (response) => {
          this.showSuccessModal = true;
          this.branchForm.reset(); // Reset form after success
          setTimeout(() => {
            this.closeModal();
          }, 3000);
        },
        error: (error) => {
          this.alertType = 'error';
          this.alertMessage = 'Error creating branch';
          this.showAlert = true;
          setTimeout(() => this.showAlert = false, 3000);
        }
      });
    }
  }

  closeModal(): void {
    this.showSuccessModal = false;
    this.router.navigate(['/branch-list']);
  }
}