import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { SmeLoanService } from '../../service/sme-loan.service';
import { SMELateFeeSummary } from '../../model/SMELateFeeSummary';
import { ApiResponse } from '../../model/ApiResponse';
import { SmeScheduleService } from '../../service/sme-schedule.service';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';



@Component({
  selector: 'app-sme-od-latefee',
  standalone: false,
  templateUrl: './sme-od-latefee.component.html',
  styleUrls: ['./sme-od-latefee.component.css'] // Corrected the styleUrl to styleUrls
})
export class SmeOdLatefeeComponent implements OnInit {
  @Input() loanId?: number;
  @ViewChild('reportContent', { static: false }) reportContent!: ElementRef;
  lateFeeSummary: SMELateFeeSummary | null = null;
  hasGracePeriod: boolean = false;
  constructor(private smeLoanService: SmeLoanService,private smeScheduleService: SmeScheduleService) { }

  ngOnInit(): void {
    if (this.loanId) {
      this.getLateFeeSummary();
    } else {
      console.error('No loan ID provided');
    }
  }

  getLateFeeSummary(): void {
    if (this.loanId) {
      this.smeLoanService.getLateFeeSummaryByLoanId(this.loanId).subscribe(
        (response: ApiResponse<SMELateFeeSummary>) => {
          if (response && response.data) {
            this.lateFeeSummary = response.data;
            console.log('Late Fee Summary:', this.lateFeeSummary);
            this.hasGracePeriod = this.lateFeeSummary.odSchedules.some(schedule => schedule.gracePeriodEndDate !== null);
          } else {
            console.error('No data found for late fee summary');
          }
        },
        (error) => {
          console.error('Error fetching late fee summary:', error);
        }
      );
    } else {
      console.error('Loan ID is not provided or invalid.');
    }
  }
  getOverdueRowClass(status: string): string {
    const base = 'transition-colors duration-200';
    switch (status.toLowerCase()) {
        case 'partial overdue': 
            return `${base} bg-orange-100 hover:bg-orange-200`;
        case 'full overdue':
            return `${base} bg-red-100 hover:bg-red-200`;
        default:
            return base;
    }
}

getOverdueStatusClasses(status: string) {
    switch (status.toLowerCase()) {
        case 'partial overdue':
            return 'bg-amber-100 text-amber-900 px-3 py-1 rounded-full text-sm';
        case 'full overdue':
            return 'bg-red-100 text-red-900 px-3 py-1 rounded-full text-sm';
        default:
            return 'bg-gray-100 text-gray-600 px-3 py-1 rounded-full text-sm';
    }
}
getTotalODInterest(): number {
  if (!this.lateFeeSummary?.odSchedules) return 0;
  return this.lateFeeSummary.odSchedules.reduce((sum, schedule) => sum + (schedule.interestODAmount || 0), 0);
}

calculateDailyFee(): number {
  if (!this.lateFeeSummary) return 0;
  
  if (this.lateFeeSummary.lateDays <= 90) {
      return this.getTotalODInterest() * (this.lateFeeSummary.lateFeeRateBf90 / 100);
  }
  return this.lateFeeSummary.outStandingAmount * (this.lateFeeSummary.lateFeeRateAf90 / 100);
}


downloadLateFeeReport(): void {
  if (!this.lateFeeSummary || !this.lateFeeSummary.odSchedules.length) {
    console.error('No overdue terms to generate report.');
    return;
  }

  const doc = new jsPDF({ orientation: 'portrait', unit: 'mm', format: 'a4' });

  const primaryColor = '#3B82F6'; // Blue
  const secondaryColor = '#6B7280'; // Gray
  const headerBgColor = '#F3F4F6'; // Light gray
  const margin = 15;
  let yPos = margin;

  // Title
  doc.setFont('helvetica', 'bold').setFontSize(16).setTextColor(primaryColor);
  doc.text('Loan Overdue Report', margin, yPos);
  yPos += 10;

  // Overdue Terms Table
  doc.setFontSize(12).setTextColor(primaryColor).text('Overdue Terms', margin, yPos);
  yPos += 6;

  const overdueColumns = [
    { header: 'Term', dataKey: 'termNumber' },
    { header: 'Due Date', dataKey: 'dueDate' },
    ...(this.hasGracePeriod ? [{ header: 'Grace End', dataKey: 'gracePeriodEndDate' }] : []),
    { header: 'Days', dataKey: 'totalDays' },
    { header: 'Principal', dataKey: 'principal' },
    { header: 'OD Interest', dataKey: 'interestODAmount' },
    { header: 'Total Repaid', dataKey: 'totalRepaidAmount' },
    { header: 'Status', dataKey: 'status' }
  ];

  const overdueData = this.lateFeeSummary.odSchedules.map(schedule => ({
    termNumber: schedule.termNumber,
    dueDate: this.formatDate(schedule.dueDate),
    gracePeriodEndDate: schedule.gracePeriodEndDate ? this.formatDate(schedule.gracePeriodEndDate) : 'N/A',
    totalDays: schedule.totalDays,
    principal: `${schedule.principal.toLocaleString()} Ks`,
    interestODAmount: `${schedule.interestODAmount.toLocaleString()} Ks`,
    totalRepaidAmount: `${schedule.totalRepaidAmount.toLocaleString()} Ks`,
    status: {
      content: schedule.status,
      styles: { fillColor: this.getStatusColor(schedule.status), textColor: '#FFFFFF', fontSize: 9 }
    }
  }));

  autoTable(doc, {
    startY: yPos,
    columns: overdueColumns,
    body: overdueData,
    theme: 'grid',
    styles: { fontSize: 9, textColor: secondaryColor },
    headStyles: { fillColor: primaryColor, textColor: '#FFFFFF' },
    didDrawPage: (data) => { if (data.cursor) { yPos = data.cursor.y + 5; } }
  });

  // Late Fee Details
  doc.setFontSize(12).setTextColor(primaryColor).text('Late Fee Details', margin, yPos);
  yPos += 8;

  autoTable(doc, {
    startY: yPos,
    body: [
      ['Late Days', this.lateFeeSummary.lateDays.toString()],
      ['Late Fees', `${this.lateFeeSummary.lateFees.toLocaleString()} Ks`]
    ],
    styles: { fontSize: 10, textColor: secondaryColor },
    theme: 'grid',
    didDrawPage: (data) => { if (data.cursor) { yPos = data.cursor.y + 5; } }
  });

  // Hold Amount (if applicable)
  if (this.lateFeeSummary.holdAmount > 0) {
    autoTable(doc, {
      startY: yPos,
      body: [['Hold Amount', `${this.lateFeeSummary.holdAmount.toLocaleString()} Ks`]],
      styles: { fontSize: 10, textColor: secondaryColor },
      theme: 'grid',
      didDrawPage: (data) => { if (data.cursor) { yPos = data.cursor.y + 5; } }
    });
  }

  // Before/After 90 Days Late Fee Calculation
  doc.setFontSize(12).setTextColor(primaryColor).text('Daily Late Fee Calculation', margin, yPos);
  yPos += 8;

  let calculationData;
  if (this.lateFeeSummary.lateDays <= 90) {
    calculationData = [
      ['Total OD Interest', `${this.getTotalODInterest().toLocaleString()} Ks`],
      ['Daily Calculation', `${this.lateFeeSummary.lateFeeRateBf90}% × ${this.getTotalODInterest().toLocaleString()} Ks`],
      ['Daily Fee Increase', `${this.calculateDailyFee().toLocaleString()} Ks`]
    ];
  } else {
    calculationData = [
      ['Outstanding Amount', `${this.lateFeeSummary.outStandingAmount.toLocaleString()} Ks`],
      ['(Principal + OD Amount + Remaining Interest)', ''],
      ['Daily Calculation', `${this.lateFeeSummary.lateFeeRateAf90}% × ${this.lateFeeSummary.outStandingAmount.toLocaleString()} Ks`],
      ['Daily Fee Increase', `${this.calculateDailyFee()} Ks`]
    ];
  }

  autoTable(doc, {
    startY: yPos,
    body: calculationData,
    styles: { fontSize: 10, textColor: secondaryColor },
    theme: 'grid',
    didDrawPage: (data) => { if (data.cursor) { yPos = data.cursor.y + 5; } }
  });

  // Save PDF
  const fileName = `Overdue_Report_Loan_${this.loanId}.pdf`;
  doc.save(fileName);
}

// Helper function for date formatting (M d, y)
private formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}

// Helper function for status colors
private getStatusColor(status: string): string {
  switch (status.toLowerCase()) {
    case 'overdue': return '#EF4444'; // Red
    case 'grace period': return '#F59E0B'; // Orange
    case 'paid': return '#22C55E'; // Green
    default: return '#6B7280'; // Gray
  }
}

}