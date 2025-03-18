import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { TransactionType } from '../../model/Transaction';
import { TransactionService } from '../../service/transaction.service';

@Component({
  selector: 'app-transaction',
  standalone: false,
  templateUrl: './transaction.component.html',
  styleUrls: ['./transaction.component.css']
})
export class TransactionComponent implements OnInit {
  transactionForm: FormGroup;
  transactionTypes = Object.values(TransactionType); 
  showSuccessModal = false;
  showErrorModal = false; 
  errorMessage: string = '';
  accountId: string | null = null;
  showConfirmModal = false;

  constructor(
    private fb: FormBuilder, 
    private transactionService: TransactionService,
    private route: ActivatedRoute
  ) {
    this.transactionForm = this.fb.group({
      type: ['', Validators.required],
      amount: ['', [Validators.required, Validators.min(1)]],
      currentAccountId: [{value: '', disabled: true}, Validators.required]
    });
  }

  ngOnInit(): void {
    // Check if route parameters exist before accessing them
    this.route.paramMap.subscribe(params => {
      this.accountId = params.get('currentAccountId'); 
      if (this.accountId) {
        this.transactionForm.patchValue({ currentAccountId: this.accountId });
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
      currentAccountId: this.accountId
    });
  }

  closeModal(): void {
    this.showSuccessModal = false;
    this.showErrorModal = false;
    this.showConfirmModal = false;
  }
}
