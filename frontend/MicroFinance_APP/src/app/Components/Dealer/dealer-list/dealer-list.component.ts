// dealer-list.component.ts
import { Component, OnInit } from '@angular/core';
import { DealerService } from '../../../service/dealer.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Dealer } from '../../../model/Dealer';
import { ModelComponent } from '../../model/model.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-dealer-list',
  standalone: false,
  templateUrl: './dealer-list.component.html',
  styleUrls: ['./dealer-list.component.css']
})
export class DealerListComponent implements OnInit {
  dealers: Dealer[] = [];

  constructor(
    private dealerService: DealerService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) { }

  ngOnInit() {
    this.loadDealers();
  }

  loadDealers() {
    this.dealerService.getAllDealers().subscribe({
      next: (dealers) => this.dealers = dealers, // Only PENDING dealers are returned
      error: () => this.snackBar.open('Error loading dealers', 'Close', { duration: 3000 })
    });
  }

  approveDealer(dealerId: number) {
    this.dealerService.approveDealer(dealerId).subscribe({
      next: () => {
        this.snackBar.open('Dealer approved', 'Close', { duration: 3000 });
        this.loadDealers();
        this.showModal('Dealer approved successfully!', true);
      },
      error: (err) => {
        this.snackBar.open(err.error || 'Error approving dealer', 'Close', { duration: 3000 });
        this.showModal('Error approving dealer', false); // Show error message
      }     
    });
  }

  rejectDealer(dealerId: number) {
    this.dealerService.rejectDealer(dealerId).subscribe({
      next: () => {
        this.snackBar.open('Dealer rejected', 'Close', { duration: 3000 });
        this.loadDealers();
        this.showModal('Dealer rejected successfully!', true);
      },
      error: (err) => {
        this.snackBar.open(err.error || 'Error rejecting dealer', 'Close', { duration: 3000 });
        this.showModal('Error rejecting dealer', false); // Show error message
      }    });
  }

  showModal(message: string, success: boolean): void {
      this.dialog.open(ModelComponent, {
        width: '300px',
        data: { message, success, }
      });
    }
}