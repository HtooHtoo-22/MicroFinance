import { Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SmeLoanService } from '../../service/sme-loan.service';
import { Smeloan } from '../../model/SmeLoan';
import { ApiResponse } from '../../model/ApiResponse';

@Component({
  selector: 'app-sme-loan-detail',
  standalone: false,
  templateUrl: './sme-loan-detail.component.html',
  styleUrl: './sme-loan-detail.component.css'
})
export class SmeLoanDetailComponent {
  statusStyles: { [key: string]: string } = {
    'Approve': 'bg-green-100 text-green-800',
    'Pending': 'bg-yellow-100 text-yellow-800',
    'Reject': 'bg-red-100 text-red-800'
  };

  loan : Smeloan | undefined;
  activeTab: 'details' | 'schedule' | 'track' | 'lateFee' = 'details';
  constructor(private route: ActivatedRoute, 
                public router: Router,
                private smeloanService : SmeLoanService
               ) {}
  // Component Code
ngOnInit() {
  const idParam = this.route.snapshot.paramMap.get('id');
  
  if (idParam) {
    console.log("SME Loan Detail id:", idParam);
    
    // Determine ID type and call appropriate service
    if (this.isValidNumber(idParam)) {
      // Handle numeric ID
      const numericId = Number(idParam);
      this.handleNumericId(numericId);
    } else {
      // Handle string ID
      this.handleStringId(idParam);
    }
  } else {
    console.error('No loan ID provided');
  }
}

private isValidNumber(id: string): boolean {
  return Number.isInteger(Number(id));
}


private handleNumericId(id: number): void {
  this.smeloanService.getLoanById(id).subscribe({
    next: (response) => this.handleResponse(response),
    error: (err) => this.handleError(err)
  });
}

private handleStringId(id: string): void {
  this.smeloanService.getLoanByLoanId(id).subscribe({
    next: (response) => this.handleResponse(response),
    error: (err) => this.handleError(err)
  });
}

private handleResponse(response: ApiResponse<Smeloan>): void {
  if (response.data) {
    this.loan = response.data;
    console.log('Loan data:', this.loan);
  } else {
    console.error('Loan not found');
    // Optional: Navigate to error page or show message
  }
}

private handleError(err: any): void {
  console.error('API Error:', err);
  // Optional: Show error message to user
}
  viewCifDetail(){

  }
  viewAccountDetail(){
    
  }
  viewCollateralDetail(collateralId:number){
    if (collateralId) {
      this.router.navigate(['/dashboard/collateralDetail', collateralId]);
      console.log(collateralId);
      
    } else {
      console.warn('Collateral ID is missing');
    }
  }
  switchTab(tab: 'details' | 'schedule') {
    this.activeTab = tab;
  }
  downloadSchedule(schedule: any) {
    if (!schedule) return;
  
    const data = JSON.stringify(schedule, null, 2);
    const blob = new Blob([data], { type: 'application/json' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `repayment-schedule-${schedule.id}.json`;
    a.click();
    window.URL.revokeObjectURL(url);
  }
  
}