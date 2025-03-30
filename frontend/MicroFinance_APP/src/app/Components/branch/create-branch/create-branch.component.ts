import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BranchService } from '../../../service/branch.service';

@Component({
  selector: 'app-create-branch',
  standalone: false,
  templateUrl: './create-branch.component.html',
  styleUrl: './create-branch.component.css'
})
export class CreateBranchComponent implements OnInit {
  branchForm!: FormGroup;
  successMessage: string = '';

  constructor(private fb: FormBuilder, private branchService: BranchService) {}

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
      this.branchService.createBranch(this.branchForm.value).subscribe(
        response => {
          this.successMessage = 'Branch created successfully!';
          this.branchForm.reset();
        },
        error => {
        }
      );
    }
  }
}