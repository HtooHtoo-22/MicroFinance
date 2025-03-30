import { Component, Input, SimpleChanges } from '@angular/core';
import { HPRepaymentTrack } from '../../../model/HPRepaymentTrack';
import { HpLoanService } from '../../../service/hp-loan.service';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

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

    generateReport(): void {
      const doc = new jsPDF();
      doc.setFont('helvetica', 'bold');
      doc.setFontSize(16);
      doc.text('HP Loan Repayment Report', 14, 15);
    
      doc.setFontSize(10);
      doc.text(`Generated Date: ${new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}`, 14, 32);
      doc.text(`Loan ID: ${this.loanId}`, 14, 39);
    
      // Prepare table headers based on the selected filter
      let tableHeaders = ['Payment Date', 'Amount (Ks)', 'Purpose', 'Status'];
      if (this.selectedFilter === 'lateFee') {
        tableHeaders.push('Late Days', 'Late Fees (Ks)');
      } else {
        tableHeaders.push('Term');
      }
    
      // Prepare table data
      const tableData = this.filteredTracks.map(track => {
        let row = [
          new Date(track.paymentDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }),
          track.paymentAmount.toLocaleString(),
          this.formatPaymentPurpose(track.paymentPurpose),
          this.formatStatusMessage(track.status)
        ];
    
        if (this.selectedFilter === 'lateFee') {
          row.push(track.lateDays?.toString() || '-');
          row.push(track.lateFees ? track.lateFees.toLocaleString() : '-');
        } else {
          row.push(track.term ? `Term ${track.term}` : '-');
        }
    
        return row;
      });
    
      // Generate table
      autoTable(doc, {
        startY: 45,
        head: [tableHeaders],
        body: tableData,
        theme: 'striped',
        styles: { fontSize: 9 },
        headStyles: { fillColor: [22, 160, 133] },
        columnStyles: {
          0: { cellWidth: 30 }, // Payment Date
          1: { cellWidth: 25 }, // Amount
          2: { cellWidth: 30 }, // Purpose
          3: { cellWidth: 40 }, // Status
          4: { cellWidth: 20 }, // Late Days or Term
          ...(this.selectedFilter === 'lateFee' ? { 5: { cellWidth: 25 } } : {}) // Late Fees if applicable
        }
      });
    
      // Save PDF
      doc.save(`HP_Loan_Repayment_Report_${this.loanId}.pdf`);
    }
}
