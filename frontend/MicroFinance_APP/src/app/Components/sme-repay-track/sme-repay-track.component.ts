import { Component, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';

import { SmeLoanService } from '../../service/sme-loan.service';
import { SMERepaymentTrack } from '../../model/SMERepaymentTrack';

@Component({
  selector: 'app-sme-repay-track',
  standalone: false,
  templateUrl: './sme-repay-track.component.html',
  styleUrls: ['./sme-repay-track.component.css']
})
export class SmeRepayTrackComponent implements OnInit, OnChanges {

  @Input() loanId?: number;
  repaymentTracks: SMERepaymentTrack[] = [];
  currentDate = new Date();
  selectedFilter: 'all' | 'od' | 'lateFee' = 'all';
  isRefreshing = false;
  isLoading = false;
  constructor(private smeLoanService: SmeLoanService) {}

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
    this.smeLoanService.getRepaymentTracksByLoanId(this.loanId!).subscribe({
      next: (response) => {
        this.repaymentTracks = response.data;
        console.log('Repayment Tracks:', this.repaymentTracks);
      },
      error: (err) => {
        console.error('Failed to load repayment tracks:', err);
      }
    });
  }
  getStatusDisplay(status: string): { text: string, class: string, icon: string } {
    const statusMap: { [key: string]: { text: string, class: string, icon: string } } = {
      'partial repayment with od occurred': {
        text: 'Partial Repayment (OD)',
        class: 'bg-amber-100 text-amber-800',
        icon: '⚠️'
      },
      'repayment paid successfully': {
        text: 'Paid Successfully',
        class: 'bg-green-100 text-green-800',
        icon: '✅'
      },
      'od amount paid successfully': {
        text: 'OD Cleared',
        class: 'bg-emerald-100 text-emerald-800',
        icon: '✔️'
      },
      'od amount remaining': {
        text: 'OD Balance',
        class: 'bg-red-100 text-red-800',
        icon: '⏳'
      },
      'late fee paid for': {
        text: this.getLateFeeText(status),
        class: 'bg-purple-100 text-purple-800',
        icon: '⏰'
      }
    };
  
    const key = status.toLowerCase().split(' for ')[0];
    return statusMap[key] || { 
      text: status, 
      class: 'bg-gray-100 text-gray-800',
      icon: 'ℹ️'
    };
  }
  
  private getLateFeeText(status: string): string {
    const daysMatch = status.match(/\d+/);
    const days = daysMatch ? daysMatch[0] : '0';
    return `Late Fee (${days} Day${days !== '1' ? 's' : ''})`;
  }
  getPaymentPurposeIcon(purpose: string): string {
    const icons: { [key: string]: string } = {
      'normal repayment': '📅',
      'od repayment': '🚨',
      'late fee': '⏳'
    };
    return icons[purpose.toLowerCase()] || '📝';
  }
  get filteredRepaymentTracks(): SMERepaymentTrack[] {
    if (!this.repaymentTracks) return [];
    
    switch (this.selectedFilter) {
      case 'od':
        return this.repaymentTracks.filter(t => 
          t.paymentPurpose.toLowerCase() === 'od repayment'
        );
      case 'lateFee':
        return this.repaymentTracks.filter(t => 
          t.paymentPurpose.toLowerCase() === 'late fee repayment'
        );
      default:
        return this.repaymentTracks;
    }
  }
  refreshData(): void {
    this.isRefreshing = true;
    this.smeLoanService.getRepaymentTracksByLoanId(this.loanId!).subscribe({
      next: (response) => {
        this.repaymentTracks = response.data;
        this.currentDate = new Date();
        this.isRefreshing = false;
      },
      error: (err) => {
        console.error('Refresh failed:', err);
        this.isRefreshing = false;
      }
    });
  }
  getStatusStyles(status: string): any {
    const base = 'inline-flex items-center transition-colors';
    
    if (status.includes('Paid') || status.includes('All Paid')) {
      return `${base} bg-emerald-100 text-emerald-700`;
    }
    if (status.includes('Partial')) {
      return `${base} bg-amber-100 text-amber-700`;
    }
    if (status.includes('OD Balence') || status.includes('Neither')) {
      return `${base} bg-rose-100 text-rose-700`;
    }
    if (status.includes('Late Fee')) {
      return `${base} bg-indigo-100 text-indigo-700`;
    }
    return `${base} bg-gray-100 text-gray-700`;
  }
}
