// dealer-list.component.ts
import { Component, OnDestroy, OnInit, ChangeDetectorRef } from '@angular/core';
import { DealerService } from '../../../service/dealer.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Dealer } from '../../../model/Dealer';
import { Subject } from 'rxjs';
import { WebSocketService } from '../../../service/websocket.service';

@Component({
  selector: 'app-dealer-list',
  standalone: false,
  templateUrl: './dealer-list.component.html',
  styleUrls: ['./dealer-list.component.css']
})
export class DealerListComponent implements OnInit, OnDestroy{
  private destroy$ = new Subject<void>();
  isConnected = false;

  dealers: Dealer[] = [];

  constructor(
    private dealerService: DealerService,
    private snackBar: MatSnackBar,
    private webSocketService: WebSocketService,

    private cd: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadDealers();
    this.setupWebSocketListeners();

  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadDealers() {
    this.dealerService.getAllDealers().subscribe({
      next: (dealers) => this.dealers = dealers, // Only PENDING dealers are returned
      error: () => this.snackBar.open('Error loading dealers', 'Close', { duration: 3000 })
    });
  }

  private setupWebSocketListeners() {
    this.webSocketService.getNewDealers().subscribe(dealer => {
      if (dealer && dealer.status === 'PENDING') {
        if (!this.dealers.some(d => d.id === dealer.id)) {
          this.dealers = [dealer, ...this.dealers];
          this.snackBar.open('New dealer created: ' + dealer.businessName, 'Close', { duration: 3000 });
          this.cd.detectChanges();
        }
      }
    });

    this.webSocketService.getStatusUpdates().subscribe(updatedDealer => {
      if (updatedDealer) {
        this.dealers = this.dealers.filter(d => d.id !== updatedDealer.id);
        this.snackBar.open(`Dealer ${updatedDealer.businessName} status updated to ${updatedDealer.status}`,
          'Close', { duration: 3000 });
        this.cd.detectChanges();
      }
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
