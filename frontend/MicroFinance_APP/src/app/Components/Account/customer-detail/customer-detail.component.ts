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
  loading: boolean = true;
  errorMessage: string | null = null;

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

  loadCifData(id: number): void {
    this.cifService.getCifById(id).subscribe({
      next: (response: Cif) => {
        this.cif = response;
      },
      error: (error: any) => {
        this.errorMessage = 'Failed to load CIF details.';
        console.error('Error loading CIF details:', error);
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  getCurrentAccs(id: number): void {
    this.currentAccountService.getAccountsByCifId(id).subscribe({
      next: (response: ApiResponse<CurrentAccount[]>) => {
        if (response?.data) {
          this.currentAccounts = response.data;
        } else {
          this.currentAccounts = [];
        }
      },
      error: (error: any) => {
        this.errorMessage = 'Failed to load Current Accounts. Please try again later.';
        console.error('Error fetching Current Accounts:', error);
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  loadTransactions(): void {
    this.transactionService.getTransactionsByCifId(this.cifId!).subscribe({
      next: (response) => {
        if (response.data) this.transactions = response.data;
      },
      error: (error) => {
        this.errorMessage = 'Error loading transactions';
      }
    });
  }
}