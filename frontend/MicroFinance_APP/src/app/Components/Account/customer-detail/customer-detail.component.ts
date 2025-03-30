import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CifService } from '../../../service/cif.service';
import { CurrentAccService } from '../../../service/current-acc.service';
import { Cif } from '../../../model/CIF';
import { CurrentAccount } from '../../../model/CurrentAcc';
import { ApiResponse } from '../../../model/ApiResponse';
import { Transaction } from '../../../model/Transaction';
import { TransactionService } from '../../../service/transaction.service';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';


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
  exportToCSV(): void {
    let csvContent = 'Transaction ID, Date, Type, Amount (MMK)\n';
    this.transactions.forEach((tx) => {
      csvContent += `${tx.id},${tx.date},${tx.type === 'CR' ? 'Credit' : 'Debit'},${tx.amount}\n`;
    });

    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'transactions.csv';
    a.click();
    window.URL.revokeObjectURL(url);
  }

  generatePDF(): void {
    const doc = new jsPDF();
    doc.text('Transaction History', 14, 10);
  
    if (!this.transactions || this.transactions.length === 0) {
      doc.text('No transactions available.', 14, 20);
    } else {
      const tableData = this.transactions.map((tx) => {
        return [
          tx.id ?? 'N/A',  // Ensure id exists
          tx.date ? new Date(tx.date).toLocaleDateString() : 'N/A', // Format date
          tx.type === 'CR' ? 'Credit' : 'Debit',
          tx.amount ? `${tx.amount.toLocaleString()} MMK` : 'N/A' // Format amount
        ];
      });
  
      console.log('Table Data:', tableData); // Debugging step
  
      autoTable(doc, {
        head: [['Transaction ID', 'Date', 'Type', 'Amount']],
        body: tableData, // This must be a valid 2D array
        startY: 20
      });
    }
  
    doc.save('transactions.pdf');
  }
  
  
}