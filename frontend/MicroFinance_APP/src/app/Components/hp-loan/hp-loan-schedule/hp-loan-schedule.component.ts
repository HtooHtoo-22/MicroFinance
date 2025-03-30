import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { HPSchedule } from '../../../model/HPSchedule';
import { HpLoanSchduleService } from '../../../service/hp-loan-schdule.service';
import { ActivatedRoute } from '@angular/router';
import { ApiResponse } from '../../../model/ApiResponse';
import autoTable from 'jspdf-autotable';
import jsPDF from 'jspdf';

@Component({
  selector: 'app-hp-loan-schedule',
  standalone: false,
  templateUrl: './hp-loan-schedule.component.html',
  styleUrls: ['./hp-loan-schedule.component.css']
})
export class HpLoanScheduleComponent implements OnChanges {
  @Input() loanId: number | undefined; // Accept loanId as input
  @Input() loanData: any; // Accept loanData as input (optional, if needed)
  schedules: HPSchedule[] = [];
  errorMessage: string = '';

  constructor(private hpLoanScheduleService: HpLoanSchduleService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['loanId'] && this.loanId) {
      this.fetchSchedules();
    }
  }

  fetchSchedules(): void {
    if (this.loanId) {
      this.hpLoanScheduleService.getSchedulesByLoanId(this.loanId).subscribe({
        next: (response: ApiResponse<HPSchedule[]>) => {
          this.schedules = response.data;
          console.log(this.schedules);
          
          this.errorMessage = '';
        },
        error: (err) => {
          this.errorMessage = 'Error fetching schedules: ' + err.message;
          this.schedules = [];
        }
      });
    } else {
      this.errorMessage = 'No loan ID provided.';
    }
    
  }
  formatStatus(status: string): string {
    switch(status) {
      case 'NOT_DUE_YET': return 'Not Due Yet';
      case 'INTEREST_PAID_PRINCIPAL_OD': return 'Interest Paid (Principal OD)';
      case 'INTEREST_OD_PRINCIPAL_OD': return 'Interest OD (Principal OD)';
      case 'ALL_PAID': return 'All Paid';
      case 'IN_GRACE_PERIOD': return 'In Grace Period';
      default: return status;
    }
  }
  getRowClass(status: string): string {
    const baseClasses = 'transition-colors duration-200';
    switch (status) {
        case 'NOT_DUE_YET':
            return `${baseClasses} bg-slate-50 hover:bg-slate-100`;
        case 'IN_GRACE_PERIOD':
            return `${baseClasses} bg-sky-50 hover:bg-sky-200`;
        case 'INTEREST_OD_PRINCIPAL_OD':
            return `${baseClasses} bg-rose-100 hover:bg-rose-300 border-l-4 border-rose-300`;
        case 'INTEREST_PAID_PRINCIPAL_OD':
            return `${baseClasses} bg-orange-200 hover:bg-orange-300 border-l-4 border-rose-300`;
        case 'ALL_PAID':
            return `${baseClasses} bg-emerald-100 hover:bg-emerald-200`;
        default:
            return baseClasses;
    }
}
downloadCSV(): void {
  let csvContent = `"Term","Due Date","Grace End","Days","Principal (Ks)","Interest (Ks)","OD Interest (Ks)","Total Repaid (Ks)","Status"\n`;

  this.schedules.forEach(schedule => {
    csvContent += `"${schedule.termNumber}","${schedule.dueDate}","${schedule.gracePeriodEndDate || ''}","${schedule.totalDays}","${schedule.principal} Ks","${schedule.interestAmount} Ks","${schedule.interestODAmount} Ks","${schedule.totalRepaidAmount} Ks","${this.formatStatus(schedule.status)}"\n`;
  });

  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'repayment_schedule.csv';
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  window.URL.revokeObjectURL(url);
}

downloadPDF(): void {
  const doc = new jsPDF('landscape');
  doc.setFontSize(14);
  doc.text('Repayment Schedule', 10, 10);

  const headers = [['Term', 'Due Date', 'Grace End', 'Days', 'Principal (Ks)', 'Interest (Ks)', 'OD Interest (Ks)', 'Total Repaid (Ks)', 'Status']];

  const body = this.schedules.map(schedule => [
    schedule.termNumber,
    schedule.dueDate,
    schedule.gracePeriodEndDate || '',
    schedule.totalDays,
    `${schedule.principal} Ks`,
    `${schedule.interestAmount} Ks`,
    `${schedule.interestODAmount} Ks`,
    `${schedule.totalRepaidAmount} Ks`,
    this.formatStatus(schedule.status)
  ]);

  autoTable(doc, {
    head: headers,
    body: body,
    startY: 20,
    theme: 'striped',
    styles: { fontSize: 10, cellPadding: 2, valign: 'middle' },
    headStyles: { fillColor: [0, 51, 102], textColor: 255, fontStyle: 'bold' },
    columnStyles: {
      0: { cellWidth: 20 },
      1: { cellWidth: 30 },
      2: { cellWidth: 30 },
      3: { cellWidth: 20 },
      4: { cellWidth: 30 },
      5: { cellWidth: 30 },
      6: { cellWidth: 30 },
      7: { cellWidth: 35 },
      8: { cellWidth: 35 }
    },
    willDrawCell: function (data) {
      if (data.section === 'body' && data.row.index !== undefined) {
        const statusText = String(body[data.row.index][8] || '').toLowerCase();
        let bgColor: [number, number, number] = [255, 255, 255];

        switch (statusText) {
          case 'all paid':
            bgColor = [187, 247, 208];
            break;
          case 'in grace period':
            bgColor = [224, 242, 254];
            break;
          case 'interest paid (principal od)':
            bgColor = [254, 243, 199];
            break;
          case 'interest od (principal od)':
            bgColor = [254, 202, 202];
            break;
          case 'not due yet':
          default:
            bgColor = [248, 250, 252];
        }

        doc.setFillColor(...bgColor);
        doc.rect(data.cell.x, data.cell.y, data.cell.width, data.cell.height, 'F');
      }
    }
  });

  doc.save('repayment_schedule.pdf');
}
}