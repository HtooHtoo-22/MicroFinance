import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CurrentAccService } from '../../../service/current-acc.service'; // Adjust the path as necessary
import { CurrentAccount } from '../../../model/CurrentAcc';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { ModelComponent } from '../../model/model.component';
import { ConfirmDialogComponent } from '../../confirm-dialog/confirm-dialog.component';
import { Cif } from '../../../model/CIF';
import { CifService } from '../../../service/cif.service';
import { AuthService } from '../../../service/auth.service';

@Component({
  selector: 'app-list',
  standalone: false,
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class ListComponent implements OnInit {
  currentAccounts: CurrentAccount[] = [];
  cifs: Cif[] = [];
  accountId!: string;

  // Pagination properties
  currentPage: number = 1;
  itemsPerPage: number = 7;
  totalAccounts: number = 0;
  totalPages: number = 0;

  constructor(private CurrentAccService: CurrentAccService,  private dialog:MatDialog,private cdr: ChangeDetectorRef,private router:Router, private cifService: CifService, private auth: AuthService) {}

  ngOnInit(): void {
    this.getCurrentAccs();
    this.getCifs();
  }

  getCifs() {
    this.cifService.listCif().subscribe({
      next: (response) => {
        if (response && response.data) {
          this.cifs = response.data;
        } else {
          this.cifs = [];
        }
      },
      error: (error) => {
        console.error('Error fetching CIFs:', error);
      }
    });
  }

  freezeAccount(account: CurrentAccount): void {
    const action = account.freezeStatus ? 'Freeze' : 'Unfreeze';
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: { 
        message: `Are you sure you want to ${action.toLowerCase()} account ${account.accountId}?`, 
        accountId: account.accountId,
        userName: account.userName,
        totalBalance: account.totalBalance
      }
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) { // If user confirmed
        const originalStatus = account.freezeStatus;
        const newFreezeStatus = !originalStatus;
  
        account.freezeStatus = newFreezeStatus; // Update UI immediately
  
        this.CurrentAccService.updateFreezeStatus(account.accountId, newFreezeStatus).subscribe({
          next: () => {
            const statusMessage = newFreezeStatus ? 'Active' : 'Frozen';
            this.showModal(`Account ${statusMessage} Successfully!`, true);
          },
          error: (error) => {
            console.error('Error updating freeze status:', error);
            account.freezeStatus = originalStatus; // Revert UI if API fails
            this.showModal('Failed to update account status.', false);
          }
        });
      }
    });
  }

  showModal(message: string, success: boolean): void {
    this.dialog.open(ModelComponent, {
      width: '300px',
      data: { message, success, }
    });
  }

  getCurrentAccs() {
    this.CurrentAccService.listCurrentAcc(Number(this.auth.getCurrentUserBranchId())).subscribe({
      next: (response) => {
        if (response && response.data) {
          this.currentAccounts = response.data; // Assign the data to the component property
          this.totalAccounts = this.currentAccounts.length;
          this.totalPages = Math.ceil(this.totalAccounts / this.itemsPerPage);
        } else {
          this.currentAccounts = []; // Handle empty response
        }
      },
      error: (error) => {
        console.error('Error fetching Current Accounts:', error);
      },
    });
  }

  onUpdateAccount(account: CurrentAccount): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        message: `Are you sure you want to update account ${account.accountId}?`, // Only message, no other data
      }
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) { // If user confirmed
        this.router.navigate(['/entry-dashboard/update-current-account', account.accountId]);
      }
    });
  }

  // Pagination methods
  get paginatedAccounts(): CurrentAccount[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.currentAccounts.slice(start, start + this.itemsPerPage);
  }

  get startIndex(): number {
    return (this.currentPage - 1) * this.itemsPerPage + 1;
  }

  get endIndex(): number {
    return Math.min(this.currentPage * this.itemsPerPage, this.totalAccounts);
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  previousPage(): void {
    if (this.currentPage > 1) this.currentPage--;
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) this.currentPage++;
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) this.currentPage = page;
  }
}