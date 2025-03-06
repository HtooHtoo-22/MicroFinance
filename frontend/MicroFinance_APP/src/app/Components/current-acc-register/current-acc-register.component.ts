import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-current-account-register',
  standalone: false,
  templateUrl: './current-acc-register.component.html',
  styleUrl: './current-acc-register.component.css'
})
export class CurrentAccountRegisterComponent {
  currentAccountForm!: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.currentAccountForm = this.fb.group({
      accountId: ['', [ Validators.maxLength(30)]],
      maxAmount: ['', [Validators.required, Validators.min(0)]],
      minAmount: ['', [Validators.required, Validators.min(0)]],
      createdDate: [new Date().toISOString().split('T')[0]], // Set today's date as default
      totalBalence: ['', [ Validators.min(0)]],
      freezeStatus: [false],
      cifId: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.currentAccountForm.valid) {
      console.log('Form Data:', this.currentAccountForm.value);
    } else {
      console.log('Form is invalid');
    }
  }
}
