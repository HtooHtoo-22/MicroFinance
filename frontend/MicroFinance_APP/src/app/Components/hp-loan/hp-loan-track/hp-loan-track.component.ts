import { Component, Input, SimpleChanges } from '@angular/core';
import { HPRepaymentTrack } from '../../../model/HPRepaymentTrack';
import { HpLoanService } from '../../../service/hp-loan.service';

@Component({
  selector: 'app-hp-loan-track',
  standalone: false,
  templateUrl: './hp-loan-track.component.html',
  styleUrl: './hp-loan-track.component.css'
})
export class HpLoanTrackComponent {
    @Input() loanId: number | undefined; // Accept loanId as input
    repaymentTracks: HPRepaymentTrack[] = [];
    currentDate = new Date();
    selectedFilter: 'all' | 'od' | 'lateFee' = 'all';
    filteredTracks: HPRepaymentTrack[] = [];
    isRefreshing = false;
    isLoading  = false;
    constructor(private hpLoanService: HpLoanService) {}
    ngOnInit(): void {
      if (this.loanId) {
        this.loadRepaymentTracks();
      }
    }
    ngOnChanges(changes: SimpleChanges): void {
      if (changes['loanId'] && this.loanId) {
        this.loadRepaymentTracks();
      }
    }
    loadRepaymentTracks(): void {
      this.hpLoanService.getRepaymentTracksByLoanId(this.loanId!).subscribe({
        next: (response) => {
          this.repaymentTracks = response.data;
          this.applyFilter(); // Apply initial filter
          console.log('Repayment Tracks:', this.repaymentTracks);
        },
        error: (err) => {
          console.error('Failed to load repayment tracks:', err);
        }
      });
    }
    formatPaymentPurpose(purpose: string): string {
      return purpose.replace(' Repayment', '');
    }
    
    formatStatusMessage(status: string): string {
      const statusMap: { [key: string]: string } = {
        'Fully Paid For Normal': 'Fully Paid (Principal + Interest)',
        'Interest Paid, Principal Overdue For Normal': 'Interest Paid, Principal Overdue',
        'Partially Paid, Interest and Principal Overdue For Normal': 'Partial Payment (Both Overdue)',
        'All Paid': 'All Overdues Cleared',
        'Interest Paid, Principal OD': 'Interest Overdue Paid',
        'Neither Interest nor Principal Paid': 'Overdue Balance Remaining',
        'Late Fee Paid for': 'Late Fees Paid for'
      };
    
      // Handle late fee status with dynamic days
      if (status.startsWith('Late Fee Paid for')) {
        const days = status.match(/\d+/)?.[0] || '';
        return `Late Fees Paid (${days} Day${days !== '1' ? 's' : ''})`;
      }
    
      return statusMap[status] || status;
    }
    
    getStatusStyles(status: string): any {
      const base = 'inline-flex items-center transition-colors';
      
      if (status.includes('Fully Paid') || status.includes('All Paid')) {
        return `${base} bg-emerald-100 text-emerald-700`;
      }
      if (status.includes('Interest Paid')) {
        return `${base} bg-amber-100 text-amber-700`;
      }
      if (status.includes('Overdue') || status.includes('Neither')) {
        return `${base} bg-rose-100 text-rose-700`;
      }
      if (status.includes('Late Fee')) {
        return `${base} bg-indigo-100 text-indigo-700`;
      }
      return `${base} bg-gray-100 text-gray-700`;
    }
    applyFilter() {
      if (this.selectedFilter === 'all') {
        this.filteredTracks= this.repaymentTracks;
      } else {
        this.filteredTracks = this.repaymentTracks.filter(track => 
          this.selectedFilter === 'od' ? 
          track.paymentPurpose === 'OD Repayment' :
          track.paymentPurpose === 'Late Fee Repayment'
        );
      }
    }
    refreshData(): void {
      this.isRefreshing = true;
      this.hpLoanService.getRepaymentTracksByLoanId(this.loanId!).subscribe({
        next: (response) => {
          this.repaymentTracks = response.data;
          this.applyFilter();
          this.currentDate = new Date();
          this.isRefreshing = false;
        },
        error: (err) => {
          console.error('Refresh failed:', err);
          this.isRefreshing = false;
        }
      });
    }
}
