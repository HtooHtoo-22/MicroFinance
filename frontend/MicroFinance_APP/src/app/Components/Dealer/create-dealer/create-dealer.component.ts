import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DealerService } from '../../../service/dealer.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CurrentAccService } from '../../../service/current-acc.service';

@Component({
  selector: 'app-dealer-form',
  standalone: false,
  templateUrl: './create-dealer.component.html',
  styleUrls: ['./create-dealer.component.css']
})
export class CreateDealerComponent {
  dealerForm: FormGroup;
  showSuccessModal = false;
  accountList: any[] = [];
  filteredAccounts: any[] = [];

  constructor(
    private fb: FormBuilder,
    private dealerService: DealerService,
    private snackBar: MatSnackBar,
     private currentAccService: CurrentAccService,
  ) {
    this.dealerForm = this.fb.group({
      businessName: ['', Validators.required],
      address: ['', Validators.required],
      phone: ['', [Validators.required, Validators.pattern('[0-9]{10,11}')]],
      email: ['', [Validators.required, Validators.email]],
      currentAccountId: ['', Validators.required],
      companyValue: ['', [Validators.required, Validators.min(0)]]
    });

    this.loadCurrentAccounts();
  }

  private loadCurrentAccounts(): void {
    this.currentAccService.listCurrentAcc().subscribe({
      next: (accounts) => {
        this.accountList = accounts.data;
        console.log('Current Accounts:', this.accountList);
      },
      error: (err) => {
        console.error('Error fetching Current Account types:', err);
      }
    });
  }

  onAccountInputChange(event: Event): void {
    const input = event.target as HTMLInputElement; // Cast to HTMLInputElement
    const value = input.value; // Now you can safely access the value
  
    if (!value) {
      this.filteredAccounts = [];
      return;
    }
  
    const lowerCaseValue = value.toLowerCase();
    this.filteredAccounts = this.accountList.filter(account =>
      account.accountId.toLowerCase().includes(lowerCaseValue)
    );
  }
  selectAccount(account: any): void {
    const currentAccountControl = this.dealerForm.get('currentAccountId');
    if (currentAccountControl) {
      currentAccountControl.setValue(account.accountId);
    }
    this.filteredAccounts = []; // Clear the suggestions
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