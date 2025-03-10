// dealer-list.component.ts
import { Component, OnInit } from '@angular/core';
import { DealerService } from '../../../service/dealer.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Dealer } from '../../../model/Dealer';

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
    private snackBar: MatSnackBar
  ) { }

  ngOnInit() {
    this.loadDealers();
  }

  loadDealers() {
    this.dealerService.getAllDealers().subscribe({
      next: (dealers) => this.dealers = dealers,
      error: () => this.snackBar.open('Error loading dealers', 'Close', { duration: 3000 })
    });
  }

  approveDealer(dealerId: number) {
    this.dealerService.approveDealer(dealerId).subscribe({
      next: () => {
        this.snackBar.open('Dealer approved', 'Close', { duration: 3000 });
        this.loadDealers();
      },
      error: (err) => this.snackBar.open(err.error || 'Error approving dealer', 'Close', { duration: 3000 })
    });
  }

  rejectDealer(dealerId: number) {
    this.dealerService.rejectDealer(dealerId).subscribe({
      next: () => {
        this.snackBar.open('Dealer rejected', 'Close', { duration: 3000 });
        this.loadDealers();
      },
      error: (err) => this.snackBar.open(err.error || 'Error rejecting dealer', 'Close', { duration: 3000 })
    });
  }
}