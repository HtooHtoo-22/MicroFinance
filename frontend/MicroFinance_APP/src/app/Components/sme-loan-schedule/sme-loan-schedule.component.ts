import { Component, Input } from '@angular/core';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { Smeloan } from '../../model/SmeLoan';
import { SmeScheduleService } from '../../service/sme-schedule.service';
import { SMESchedule } from '../../model/SMESchedule';

@Component({
  selector: 'app-sme-loan-schedule',
  standalone: false,
  templateUrl: './sme-loan-schedule.component.html',
  styleUrl: './sme-loan-schedule.component.css'
})
export class SmeLoanScheduleComponent {
  @Input() loanId?: number;
  @Input() loanData?: Smeloan;
  schedules: SMESchedule[] = [];
  hasGracePeriod: boolean = false;
  isRefreshing = false;
  constructor(private scheduleService: SmeScheduleService) {}
  
  ngOnInit(): void {
    console.log('Component initialized with Loan ID:', this.loanId);
    console.log('Loan Data:Repay', this.loanData);
    if (this.loanId) {
      this.scheduleService.getSchedulesByLoanId(this.loanId).subscribe({
        next: (schedules) => {
          console.log('Repayment Schedules:', schedules);
          this.schedules = schedules.data;
            this.hasGracePeriod = schedules.data.some((schedule: SMESchedule) => schedule.gracePeriodEndDate !== null);
        },
        error: (err) => {
          console.error('Error fetching schedules:', err);
        }
      });
    }
  }
  getRowClass(status: string): string {
    const baseClasses = 'transition-colors duration-200';
    switch (status.toLowerCase()) {
        case 'not due yet':
            return `${baseClasses} bg-slate-50 hover:bg-slate-100`;
        case 'in grace period':
            return `${baseClasses} bg-sky-50 hover:bg-sky-200 `;  // Custom color if needed
        case 'partial overdue':
            return `${baseClasses} bg-orange-100 hover:bg-orange-300 border-l-4 border-rose-300`;
        case 'full overdue':
            return `${baseClasses} bg-rose-200 hover:bg-rose-300 border-l-4 border-rose-300`; // Added left border
        case 'paid':
            return `${baseClasses} bg-emerald-100 hover:bg-emerald-200`; // More visible green
        default:
            return baseClasses;
    }
}
  getStatusClasses(status: string) {
    switch (status.toLowerCase()) {
        case 'paid':
            return 'bg-slate-100 text-green-600 px-3 py-1 rounded-full text-sm font-medium';
        case 'in grace period':
            return 'bg-sky-100 text-sky-800 px-3 py-1 rounded-full text-sm font-medium';
        case 'partial overdue':
            return 'bg-amber-50 text-amber-900 px-3 py-1 rounded-full text-sm font-medium';
        case 'full overdue':
            return 'bg-rose-100 text-rose-800 px-3 py-1 rounded-full text-sm font-medium';
        case 'not due yet':
            return 'bg-slate-100 text-slate-600 px-3 py-1 rounded-full text-sm font-medium';
        default:
            return 'bg-slate-100 text-slate-600 px-3 py-1 rounded-full text-sm font-medium';
    }
}
refreshData(): void {
  this.isRefreshing = true;
  this.scheduleService.getSchedulesByLoanId(this.loanId!).subscribe({
    next: (response) => {
      this.schedules = response.data;
      this.isRefreshing = false;
    },
    error: (err) => {
      console.error('Refresh failed:', err);
      this.isRefreshing = false;
    }

  });
}
downloadCSV(): void {
  let csvContent = 'Term,Due Date,Grace End,Days,Principal,Interest,OD Interest,Total Repaid,Status,Status Color\n';
  this.schedules.forEach(schedule => {
    // Get the corresponding color class based on status
    csvContent += `${schedule.termNumber},${schedule.dueDate},${schedule.gracePeriodEndDate || ''},${schedule.totalDays},${schedule.principal} Ks,${schedule.interestAmount} Ks,${schedule.interestODAmount} Ks,${schedule.totalRepaidAmount} Ks,${schedule.status}\n`;
  });
  const blob = new Blob([csvContent], { type: 'text/csv' });
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'repayment_schedule.csv';
  a.click();
  window.URL.revokeObjectURL(url);
}
downloadPDF(): void {
  const doc = new jsPDF('landscape');
    doc.text('Repayment Schedule', 10, 10);

  const headers = [['Term', 'Due Date', 'Grace End', 'Days', 'Principal', 'Interest', 'OD Interest', 'Total Repaid', 'Status']];
  
  const body = this.schedules.map(schedule => [
    schedule.termNumber,
    schedule.dueDate,
    schedule.gracePeriodEndDate || '',
    schedule.totalDays,
    `${schedule.principal} Ks`,
    `${schedule.interestAmount} Ks`,
    `${schedule.interestODAmount} Ks`,
    `${schedule.totalRepaidAmount} Ks`,
    schedule.status
  ]);

  autoTable(doc, {
    head: headers,
    body: body,
    startY: 20,
    theme: 'striped', // Makes it more readable
    styles: { fontSize: 10, cellPadding: 2, valign: 'middle' },
    headStyles: { fillColor: [0, 51, 102], textColor: 255, fontStyle: 'bold' },
    columnStyles: {
      0: { cellWidth: 20 }, // Term
      1: { cellWidth: 30 }, // Due Date
      2: { cellWidth: 30 }, // Grace End
      3: { cellWidth: 20 }, // Days
      4: { cellWidth: 30 }, // Principal
      5: { cellWidth: 30 }, // Interest
      6: { cellWidth: 30 }, // OD Interest
      7: { cellWidth: 35 }, // Total Repaid
      8: { cellWidth: 35 }, // Status
    },
    willDrawCell: function (data) {
      if (data.section === 'body' && data.row.index !== undefined) {
        const statusText = String(body[data.row.index][8] || '').toLowerCase(); // Get status from last column

        let bgColor: [number, number, number] = [255, 255, 255]; // Default White

        switch (statusText) {
          case 'paid':
            bgColor = [187, 247, 208]; // Light Green
            break;
          case 'in grace period':
            bgColor = [224, 242, 254]; // Light Blue
            break;
          case 'partial overdue':
            bgColor = [254, 243, 199]; // Light Orange
            break;
          case 'full overdue':
            bgColor = [254, 202, 202]; // Light Red
            break;
          case 'not due yet':
          default:
            bgColor = [248, 250, 252]; // Light Gray
        }

        doc.setFillColor(...bgColor);
        doc.rect(data.cell.x, data.cell.y, data.cell.width, data.cell.height, 'F');
      }
    }
  });

  doc.save('repayment_schedule.pdf');
}



}



