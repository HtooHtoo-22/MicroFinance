import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CifService } from '../../../service/cif.service';
import { CurrentAccService } from '../../../service/current-acc.service';
import { Cif } from '../../../model/CIF';
import { CurrentAccount } from '../../../model/CurrentAcc';
import { ApiResponse } from '../../../model/ApiResponse';
import { Transaction } from '../../../model/Transaction';
import { TransactionService } from '../../../service/transaction.service';

@Component({
  selector: 'app-customer-profile',
  standalone: false,
  templateUrl: './customer-detail.component.html',
  styleUrls: ['./customer-detail.component.css']
})
export class CustomerDetailComponent implements OnInit {
  cifId: number | null = null;
  cif: Cif | null = null;
  currentAccounts: CurrentAccount[] = [];
  transactions: Transaction[] = [];
  loading = true;
  errorMessage: string | null = null;
  showSuccessModal = false;
  showErrorModal = false;
  message = '';
  showAllTransactions = false;

  constructor(
    private route: ActivatedRoute,
    private cifService: CifService,
    private currentAccountService: CurrentAccService,
    private transactionService: TransactionService
  ) {}

  ngOnInit(): void {
    this.cifId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.cifId) {
      this.loadCifData(this.cifId);
      this.getCurrentAccs(this.cifId);
      this.loadTransactions();
    } else {
      this.errorMessage = 'CIF ID is missing.';
      this.loading = false;
    }
  }

  get recentTransactions(): Transaction[] {
    const sevenDaysAgo = new Date();
    sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7);
    
    return this.transactions.filter(transaction => {
      const transactionDate = transaction.date ? new Date(transaction.date) : new Date();
      return transactionDate >= sevenDaysAgo;
    }).sort((a, b) => {
      const dateA = new Date(a.date || 0);
      const dateB = new Date(b.date || 0);
      return dateB.getTime() - dateA.getTime();
    });
  }

  trackByTransactionId(index: number, transaction: Transaction): number {
    return transaction.id || index;
  }

  private loadCifData(id: number): void {
    this.cifService.getCifById(id).subscribe({
      next: (response: Cif) => {
        this.cif = response;
      },
      error: (error: any) => {
        this.handleError('Failed to load CIF details.', error);
      },
      complete: () => this.loading = false
    });
  }

  private getCurrentAccs(id: number): void {
    this.currentAccountService.getAccountsByCifId(id).subscribe({
      next: (response: ApiResponse<CurrentAccount[]>) => {
        this.currentAccounts = response?.data || [];
      },
      error: (error: any) => {
        this.handleError('Failed to load Current Accounts.', error);
      },
      complete: () => this.loading = false
    });
  }

  private loadTransactions(): void {
    if (!this.cifId) return;

    this.transactionService.getTransactionsByCifId(this.cifId).subscribe({
      next: (response) => {
        this.transactions = response.data?.sort((a, b) => {
          const dateA = new Date(a.date || 0);
          const dateB = new Date(b.date || 0);
          return dateB.getTime() - dateA.getTime();
        }) || [];
      },
      error: (error) => {
        this.handleError('Error loading transactions', error);
      }
    });
  }

  private handleError(message: string, error: any): void {
    this.errorMessage = message;
    console.error(message, error);
    this.showErrorModal = true;
    this.message = message;
    this.loading = false;
  }

  closeModal(): void {
    this.showSuccessModal = false;
    this.showErrorModal = false;
  }
}