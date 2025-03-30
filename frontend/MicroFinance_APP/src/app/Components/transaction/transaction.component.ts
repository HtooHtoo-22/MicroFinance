import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { TransactionType } from '../../model/Transaction';
import { TransactionService } from '../../service/transaction.service';
import { CurrentAccount } from '../../model/CurrentAcc';

@Component({
  selector: 'app-transaction',
  standalone: false,
  templateUrl: './transaction.component.html',
  styleUrls: ['./transaction.component.css']
})
export class TransactionComponent implements OnInit {
  transactionForm: FormGroup;
  transactionTypes = Object.values(TransactionType);
  currentAccount: CurrentAccount[] = [];
  showSuccessModal = false;
  showErrorModal = false;
  errorMessage: string = '';
  showConfirmModal = false;
  accountId: any;
   // Holds the list of accounts to be displayed in the dropdown
  constructor(
    private fb: FormBuilder, 
    private transactionService: TransactionService,
    private route: ActivatedRoute,
  ) {
    this.transactionForm = this.fb.group({
      type: ['', Validators.required],
      amount: ['', [Validators.required, Validators.min(1)]],
      currentAccountId: [{value: '', disabled: true}, Validators.required]
    });
  }
  ngOnInit(): void {
    
    this.route.paramMap.subscribe(params => {
      this.accountId = params.get('currentAccountId');
      console.log("Account ID from route:", this.accountId);
      if (this.accountId) {
        this.transactionForm.patchValue({
          currentAccountId: this.accountId  // Fixed typo in variable name (was 'accurrentAccountcountId')
        });
      }
    });
  }
  
  
  onSubmit() {
    if (this.transactionForm.valid) {
      // Show confirmation modal instead of direct submission
      this.showConfirmModal = true;
    }
  }

  confirmTransaction() {
    // Get raw value including disabled fields
    const transactionData = this.transactionForm.getRawValue();
    
    this.transactionService.createTransaction(transactionData).subscribe({
      next: (response) => {
        console.log('Transaction created successfully:', response);
        this.showConfirmModal = false;
        this.showSuccessModal = true;
        this.resetForm();
        setTimeout(() => this.closeModal(), 3000);
      },
      error: (error) => {
        console.error('Error creating transaction:', error);
        this.showConfirmModal = false;
        this.errorMessage = error.error?.message || 'An error occurred while creating the transaction.';
        this.showErrorModal = true;
        this.resetForm();
        setTimeout(() => this.showErrorModal = false, 3000);
      }
    });
  }

  resetForm(): void {
    this.transactionForm.patchValue({
      type: '',
      amount: '',
      currentAccountId: this.accountId || ''
    });
  }

  closeModal(): void {
    this.showSuccessModal = false;
    this.showErrorModal = false;
    this.showConfirmModal = false;
  }
}
