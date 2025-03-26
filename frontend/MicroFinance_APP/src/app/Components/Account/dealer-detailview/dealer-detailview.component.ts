import { Component, OnInit } from '@angular/core';
import { DealerService } from '../../../service/dealer.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Dealer } from '../../../model/Dealer';
import { CifService } from '../../../service/cif.service';
import { TransactionService } from '../../../service/transaction.service';
import { Transaction } from '../../../model/Transaction';
import { CurrentAccount } from '../../../model/CurrentAcc';
import { CurrentAccService } from '../../../service/current-acc.service';

@Component({
  selector: 'app-dealer-detailview',
  standalone: false,
  templateUrl: './dealer-detailview.component.html',
  styleUrls: ['./dealer-detailview.component.css']
})
export class DealerDetailviewComponent implements OnInit {
  dealer?: Dealer;
  transactions: Transaction[] = [];
  currentAccount?: CurrentAccount; // Add this property
  isLoading = true;
  errorMessage: string = '';
  dashboardData: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dealerService: DealerService,
    private cifService: CifService,
    private transactionService: TransactionService,
    private currentAccountService: CurrentAccService // Add this

  ) { }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.dealerService.getDealerById(+id).subscribe({
        next: (dealer) => {
          this.dealer = dealer;
          this.loadUserPhoto(dealer.id);
          this.loadTransactions(dealer.id); // Load transactions by dealer ID
          this.loadCurrentAccount(dealer.currentAccount?.accountId); // Add this

        },
        error: () => this.router.navigate(['/manager-dashboard/dealer-detail'])
      });
    }
  }

  loadCurrentAccount(accountId: string | undefined) {
    if (!accountId) return;
    
    this.currentAccountService.getCurrentAccountById(accountId).subscribe({
      next: (account) => {
        this.currentAccount = account.data;
      },
      error: (err) => {
        console.error('Error loading current account:', err);
      }
    });
  }

  loadUserPhoto(dealerId: number) {
    this.cifService.getCifById(dealerId).subscribe({
      next: (cif) => {
        if (this.dealer) {
          this.dealer.userPhotoURL = cif.userPhotoURL;
        }
      },
      error: () => {
        console.error(`Error loading photo for dealer ID ${dealerId}`);
      }
    });
  }

// dealer-detailview.component.ts
loadTransactions(dealerId: number) {
  this.isLoading = true;
  this.errorMessage = '';
  
  this.transactionService.getTransactionsByDealerId(dealerId).subscribe({
    next: (response) => {
      if (response.data) {
        this.transactions = response.data;
      }
      this.isLoading = false;
    },
    error: (err) => {
      console.error('Error loading transactions:', err);
      this.errorMessage = err.message || 'Failed to load transactions';
      this.isLoading = false;
    }
  });
}


  goBack() {
    this.router.navigate(['/manager-dashboard/dealer-detail']);
  }
}