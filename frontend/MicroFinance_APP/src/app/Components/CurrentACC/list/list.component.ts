import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CurrentAccService } from '../../../service/current-acc.service'; // Adjust the path as necessary
import { CurrentAccount } from '../../../model/CurrentAcc';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { ModelComponent } from '../../model/model.component';
import { ConfirmDialogComponent } from '../../confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-list',
  standalone: false,
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class ListComponent implements OnInit {
  currentAccounts: CurrentAccount[] = [];
  accountId!: string;

  constructor(private CurrentAccService: CurrentAccService,  private dialog:MatDialog,private cdr: ChangeDetectorRef,private router:Router) {}

  ngOnInit(): void {
    this.getCurrentAccs();
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
    this.CurrentAccService.listCurrentAcc().subscribe({
      next: (response) => {
        if (response && response.data) {
          this.currentAccounts = response.data; // Assign the data to the component property
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
        this.router.navigate(['/dashboard/update-current-account', account.accountId]);
      }
    });
  }
}