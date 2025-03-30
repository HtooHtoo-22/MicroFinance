import { Component, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { Subscription } from 'rxjs';
import { AuthService } from '../../service/auth.service';
import { WebSocketService } from '../../service/websocket.service';
import { NavigationEnd, Router } from '@angular/router';

@Component({
  selector: 'app-manager-dashboard',
  standalone: false,
  templateUrl: './manager-dashboard.component.html',
  styleUrls: ['./manager-dashboard.component.css']
})
export class ManagerDashboardComponent implements OnDestroy {
  imagePath: string = "image/richcon-logo.png";
  hpLoanCount: number = 0;
  newDealerCount: number = 0;
  smeLoanCount: number = 0;
  showLogoutModal: boolean = false;

  private dealerSubscription!: Subscription;
  private routerSubscription!: Subscription;
  private hpLoanSubscription!: Subscription;
  private smeLoanSubscription!: Subscription;

  constructor(
    private authService: AuthService,
    private webSocketService: WebSocketService,
    private router: Router,
    private cd: ChangeDetectorRef
  ) {
    this.setupWebSocketListeners();
    this.setupRouteListener();
  }

  getCurrentRole(): string | null {
    return this.authService.getCurrentUserRole();
  }
  
  getCurrentRoleName(): string {
    return this.authService.getCurrentUserRoleName();
  }
  
  getCurrentEmail(): string | null {
    return this.authService.getStoredEmail();
  }


  private setupWebSocketListeners() {
    this.dealerSubscription = this.webSocketService.getNewDealers().subscribe(dealer => {
      if (dealer && dealer.status === 'PENDING') {
        this.newDealerCount++;
      }
    });
    this.hpLoanSubscription = this.webSocketService.getNewHPLoans().subscribe(loan => {
      if (loan && loan.status === 'PENDING') {
        this.hpLoanCount++;
      }
    });
    this.smeLoanSubscription = this.webSocketService.getNewSMELoans().subscribe(loan => {
      if (loan && loan.status === 'PENDING') {
        this.smeLoanCount++;
        this.cd.detectChanges(); // Trigger change detection
      }
    });
  }

  private setupRouteListener() {
    this.routerSubscription = this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        if (event.url.includes('/sme-loan-list')) {
          this.smeLoanCount = 0;
        }
        if (event.url.includes('/dealer-list')) {
          this.newDealerCount = 0;
        }
        if (event.url.includes('/hp-loan-list')) {
          this.hpLoanCount = 0;
        }
      }
    });
  }

  ngOnDestroy() {
    this.dealerSubscription?.unsubscribe();
    this.routerSubscription?.unsubscribe();
    this.hpLoanSubscription?.unsubscribe();
    this.smeLoanSubscription?.unsubscribe();
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  isManager(): boolean {
    return this.authService.isManager();
  }

  isEntry(): boolean {
    return this.authService.isEntry();
  }

  isOperation(): boolean {
    return this.authService.isOperation();
  }

  openLogoutModal(): void {
    this.showLogoutModal = true;
  }

  closeLogoutModal(): void {
    this.showLogoutModal = false;
  }

  confirmLogout(): void {
    this.authService.logout().subscribe({
      next: () => {
        console.log('Logged out successfully');
        this.showLogoutModal = false;
      },
      error: (err) => {
        console.error('Logout failed:', err);
        this.showLogoutModal = false;
      }
    });
  }
}