import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../service/auth.service';
import { DealerService } from '../../../service/dealer.service';
import { TransactionService } from '../../../service/transaction.service';
import { Transaction } from '../../../model/Transaction';

@Component({
  selector: 'app-dealer-transaction',
  standalone: false,
  templateUrl: './dealer-transaction.component.html',
  styleUrls: ['./dealer-transaction.component.css']
})
export class DealerTransactionComponent implements OnInit {
  transactions: Transaction[] = [];
  currentAccountId: string | null = null;

  constructor(
    private authService: AuthService,
    private dealerService: DealerService,
    private transactionService: TransactionService
  ) {}

  ngOnInit(): void {
    this.authService.getCurrentUserEmail().subscribe((email: string | null) => {
      if (email) {
        this.dealerService.getDealerByEmail(email).subscribe({
          next: (dealer: any) => {
            this.currentAccountId = dealer.currentAccount?.accountId;
            if (this.currentAccountId) {
              this.loadTransactions();
            }
          },
          error: (err: any) => console.error('Error fetching dealer', err)
        });
      }
    });
  }

  private loadTransactions(): void {
    if (this.currentAccountId) {
      this.transactionService.getTransactionsByAccountId(Number(this.currentAccountId))
        .subscribe({
          next: (response: any) => {
            this.transactions = response.data || [];
          },
          error: (err: any) => console.error('Error loading transactions', err)
        });
    }
  }
}