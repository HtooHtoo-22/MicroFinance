import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms'

@Component({
  selector: 'app-sme-laon-register',
  standalone: false,

  templateUrl: './sme-loan-register.component.html',
  styleUrls: ['./sme-loan-register.component.css']
})
export class SmeLoanRegisterComponent implements OnInit {
  smeLoanForm!: FormGroup; // Add '!' to tell TypeScript that it will be initialized

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.smeLoanForm = this.fb.group({
      loanId: ['', [Validators.required, Validators.maxLength(30)]],
      loanAmount: ['', [Validators.required, Validators.min(0)]],
      interestRate: ['', [Validators.required, Validators.min(0)]],
      gracePeriod: ['', [Validators.min(0)]],
      loanPurpose: ['', [Validators.required, Validators.maxLength(200)]],
      documentFee: ['', [Validators.required, Validators.min(0)]],
      serviceCharge: ['', [Validators.required, Validators.min(0)]],
      duration: ['', [Validators.required, Validators.min(1)]],
      principal: ['', [Validators.required, Validators.min(0)]],
      expiredDate: ['']
    });
  }

  onSubmit(): void {
    if (this.smeLoanForm.valid) {
      console.log('Form Submitted:', this.smeLoanForm.value);
    } else {
      console.log('Form is invalid');
    }
  }
}
