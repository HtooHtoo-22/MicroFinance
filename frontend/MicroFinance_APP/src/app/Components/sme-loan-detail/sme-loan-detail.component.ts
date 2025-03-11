import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SmeLoanService } from '../../service/sme-loan.service';
import { Smeloan } from '../../model/SmeLoan';

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
  constructor(private route: ActivatedRoute, 
                public router: Router,
                private smeloanService : SmeLoanService
               ) {}
  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const loanId = Number(idParam);
      console.log("SME Loan  Detail id : "+idParam);
      this.smeloanService.getLoanById(loanId).subscribe({
        next: (response) => {
          if (response.data) {
            this.loan = response.data;
            console.log(this.loan);
          } else {
            console.error('Loan not found');
          }
        },
        error: (err) => {
          console.error('API Error:', err);
        },
        complete: () => {
          // Optional cleanup/loading state removal
        }
      });
    }
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
}