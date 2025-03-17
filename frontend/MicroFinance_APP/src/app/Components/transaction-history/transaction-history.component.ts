import { Component, OnInit } from '@angular/core';
import { TransactionService } from '../../service/transaction.service'; // Adjust the path as necessary
import { Transaction } from '../../model/Transaction'; // Adjust the path as necessary
import { ApiResponse } from '../../model/ApiResponse'; // Adjust the path as necessary

@Component({
  selector: 'app-transaction-history',
  standalone: false,
  templateUrl: './transaction-history.component.html',
  styleUrls: ['./transaction-history.component.css']
})
export class TransactionHistoryComponent implements OnInit {
  transactions: Transaction[] = [];
  loading: boolean = true;
  errorMessage: string | null = null;

  constructor(private transactionService: TransactionService) {}

  ngOnInit(): void {
    this.getTransactions();
  }

  getTransactions() {
    this.transactionService.getAllTransactions().subscribe({
      next: (response: ApiResponse<Transaction[]>) => {
        this.loading = false;
        if (response && response.data) {
          this.transactions = response.data;
        } else {
          this.transactions = [];
        }
      },
      error: (error) => {
        this.loading = false;
        console.error('Error fetching transactions:', error);
        this.errorMessage = error.error.message || 'An error occurred while fetching transactions.';
      }
    });
  }
}