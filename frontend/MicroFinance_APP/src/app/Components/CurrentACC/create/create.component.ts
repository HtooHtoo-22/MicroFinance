import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CurrentAccService } from '../../../service/current-acc.service'; // Adjust the path as necessary
import { CurrentAccount } from '../../../model/CurrentAcc';

@Component({
  selector: 'app-current-account-register',
  standalone: false,
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.css']
})
export class CreateComponent implements OnInit {
  currentAccountForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private currentAccService: CurrentAccService // Inject the service
  ) {}

  ngOnInit(): void {
    this.currentAccountForm = this.fb.group({
      cifId: ['', Validators.required], // CIF ID is required
      maxAmount: ['', [Validators.required, Validators.min(0)]], // Max amount is required
      minAmount: ['', [Validators.required, Validators.min(0)]], // Min amount is required
      totalBalance: [0], // Default value for total balance
      freezeStatus: [false], // Default value for freeze status
      accountId: [''] // Optional, can be generated on the backend
    });

    // Get the CIF ID from the route parameters
    this.route.params.subscribe(params => {
      const cifId = params['cifId'];
      if (cifId) {
        this.currentAccountForm.patchValue({ cifId: cifId });
      }
    });
  }

  onSubmit() {
    if (this.currentAccountForm.valid) {
      const formValue = this.currentAccountForm.value;
      
      // Convert cifId to a number and ensure it's valid
      const cifId = Number(formValue.cifId);
      if (isNaN(cifId)) {
          console.error('CIF ID is not a valid number');
          alert('Invalid CIF ID');
          return;
      }

      const accountData: CurrentAccount = {
          
          accountId: '',
          maxAmount: Number(formValue.maxAmount),
          minAmount: Number(formValue.minAmount),
          cifId: cifId,
          totalBalance: 0,
          freezeStatus: false,
          createDate: '',
          userName: ''
      };


        this.currentAccService.createCurrentAccount(accountData).subscribe({
            next: (response) => {
                console.log('Current Account created successfully:', response);
                this.router.navigate(['/dashboard/current-acc-list']); // Navigate to the list after creation
            },
            error: (error) => {
                console.error('Error creating Current Account:', error);
                alert('Error creating current account. Please try again.');
            }
        });
    } else {
        console.log('Form is invalid');
    }
}
}