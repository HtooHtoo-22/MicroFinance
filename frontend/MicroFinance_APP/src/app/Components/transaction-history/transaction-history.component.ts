import { Component, OnInit } from '@angular/core';
import { TransactionService } from '../../service/transaction.service'; // Adjust the path as necessary
import { Transaction } from '../../model/Transaction'; // Adjust the path as necessary
import { ApiResponse } from '../../model/ApiResponse';

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
  
  showModal: boolean = false;
  selectedTransaction: Transaction | null = null;

  constructor(private transactionService: TransactionService) {}

  ngOnInit(): void {
    this.getTransactions();
  }

  getTransactions() {
    this.transactionService.getAllTransactions().subscribe({
      next: (response: ApiResponse<Transaction[]>) => {
        this.loading = false;
        this.transactions = response.data || [];
      },
      error: (error) => {
        this.loading = false;
        console.error('Error fetching transactions:', error);
        this.errorMessage = error.error.message || 'An error occurred while fetching transactions.';
      }
    });
  }

  openModal(transaction: Transaction) {
    this.selectedTransaction = transaction;
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
    this.selectedTransaction = null;
  }

  downloadReport() {
    if (this.selectedTransaction && this.selectedTransaction.id !== undefined) {
      this.transactionService.downloadTransactionReport(this.selectedTransaction.id).subscribe(response => {
        const blob = new Blob([response], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `TransactionReport_${this.selectedTransaction?.id}.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);


        this.closeModal();

      });
    }
  }
}
