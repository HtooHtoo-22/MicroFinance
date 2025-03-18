import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DealerService } from '../../../service/dealer.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-dealer-form',
  standalone: false,
  templateUrl: './create-dealer.component.html',
  styleUrls: ['./create-dealer.component.css']
})
export class CreateDealerComponent {
  dealerForm: FormGroup;
  showSuccessModal = false;

  constructor(
    private fb: FormBuilder,
    private dealerService: DealerService,
    private snackBar: MatSnackBar
  ) {
    this.dealerForm = this.fb.group({
      businessName: ['', Validators.required],
      address: ['', Validators.required],
      phone: ['', [Validators.required, Validators.pattern('[0-9]{10,11}')]],
      email: ['', [Validators.required, Validators.email]],
      currentAccountId: ['', Validators.required],
      companyValue: ['', [Validators.required, Validators.min(0)]]
    });
  }

  onSubmit() {
    if (this.dealerForm.valid) {
      this.dealerService.createDealer(this.dealerForm.value).subscribe({
        next: () => {
          this.dealerForm.reset();
          this.showSuccessModal = true;
          setTimeout(() => {
            this.showSuccessModal = false;
          }, 3000);
        },
        error: (err) => {
          this.snackBar.open(err.error?.message || 'Error creating dealer', 'Close', { duration: 3000 });
        }
      });
    }
  }

  closeModal() {
    this.showSuccessModal = false;
  }
}