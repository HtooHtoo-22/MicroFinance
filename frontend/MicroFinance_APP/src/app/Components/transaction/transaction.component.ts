import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Transaction, TransactionType } from '../../model/Transaction';
import { TransactionService } from '../../service/transaction.service';
import { ApiResponse } from '../../model/ApiResponse';
import { CurrentAccService } from '../../service/current-acc.service';
import { AuthService } from '../../service/auth.service';


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
  id: number | null = null;
  accountList: any[] = [];
  filteredAccounts: any[] = [];


  constructor(
    private fb: FormBuilder, 
    private transactionService: TransactionService,
    private route: ActivatedRoute,
     private currentAccService: CurrentAccService,
     private auth: AuthService
  ) {
    this.transactionForm = this.fb.group({
      type: ['', Validators.required],
      amount: ['', [Validators.required, Validators.min(1)]],
      currentAccountId: [{value: '', disabled: true}, Validators.required]
    });
   
  }

  // ngOnInit(): void {
  //   // Check if route parameters exist before accessing them
  //   this.route.paramMap.subscribe(params => {
  //     this.accountId = params.get('currentAccountId'); 
  //     if (this.accountId) {
  //       this.transactionForm.patchValue({ currentAccountId: this.accountId });
  //     }
  //   });
  // }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.accountId = params.get('currentAccountId');
      if (this.accountId) {
        this.transactionForm.patchValue({ currentAccountId: this.accountId });
        this.transactionForm.get('currentAccountId')?.disable();
      } else {
        this.transactionForm.get('currentAccountId')?.enable();
      }
    });
    this.loadCurrentAccounts();
    
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
      next: (response: ApiResponse<Transaction[]>) => {
        console.log('Transaction created successfully:', response);
        this.showConfirmModal = false;
        this.showSuccessModal = true;

       
        const transaction = response.data as unknown as Transaction;
        if (transaction && !Array.isArray(transaction) && transaction.id) {
          this.id = (response.data as unknown as Transaction).id ?? null;
          this.downloadReport();  // Auto-download the report
        }
        this.resetForm();
        setTimeout(() => this.closeModal(), 10000);

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


  

  downloadReport() {
    if (!this.id) return;

    this.transactionService.downloadTransactionReport(this.id).subscribe(response => {
      if (response) {
        const blob = new Blob([response], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'TransactionReport.pdf';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      } else {
        console.error('Received empty response for transaction report');
      }
    }, error => {
      console.error('Error downloading transaction report:', error);
    });
  }
  
  private loadCurrentAccounts(): void {
    this.currentAccService.listCurrentAcc(Number(this.auth.getCurrentUserBranchId())).subscribe({
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
    const currentAccountControl = this.transactionForm.get('currentAccountId');
    if (currentAccountControl) {
      currentAccountControl.setValue(account.accountId);
    }
    this.filteredAccounts = []; // Clear the suggestions
  }


  
}