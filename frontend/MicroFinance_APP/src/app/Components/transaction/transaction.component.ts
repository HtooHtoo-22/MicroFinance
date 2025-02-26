import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TransactionType } from '../../model/Transaction';
@Component({
  selector: 'app-transaction',
  standalone: false,
  templateUrl: './transaction.component.html',
  styleUrl: './transaction.component.css'
})
export class TransactionComponent {
  transactionForm: FormGroup;
  transactionTypes = Object.values(TransactionType); // DR, CR

  constructor(private fb: FormBuilder) {
    this.transactionForm = this.fb.group({
      type: ['', Validators.required],
      amount: ['', [Validators.required, Validators.min(1)]],
      currentAccountId: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.transactionForm.valid) {
      console.log('Transaction Submitted:', this.transactionForm.value);
      // You can send this data to your backend API
    }
  }

}
