import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { CollateralService } from '../../../service/collateral.service';

import { CollateralDTO } from '../../../model/CollateralDTO';
import { SmeLoanService } from '../../../service/sme-loan.service';
import { ApiResponse } from '../../../model/ApiResponse';

@Component({
  selector: 'app-collateral-detail',
  standalone: false,
  templateUrl: './collateral-detail.component.html',
  styleUrl: './collateral-detail.component.css'
})
export class CollateralDetailComponent {
  selectedCollateral: CollateralDTO | undefined;
  errorMessage: string | undefined;
  

  constructor(private route: ActivatedRoute, 
              public router: Router,
              private collateralService : CollateralService,
              private smeLoanService: SmeLoanService) {}
  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const collateralId = Number(idParam);
      console.log("Collateral Detail id : "+idParam);
      
      this.collateralService.getCollateralById(collateralId).subscribe({
        next: (response: ApiResponse<CollateralDTO>) => {
          if (response.data) {
            this.selectedCollateral = response.data;
            console.log(this.selectedCollateral);
          } else {
            this.errorMessage = 'Collateral not found';
          }
        },
        error: (err) => {
          this.errorMessage = 'Error loading collateral details';
          console.error('API Error:', err);
        },
        complete: () => {
          // Optional cleanup/loading state removal
        }
      });
      
      // Fetch your data here
    } else {
      // Handle missing ID (redirect or show error)
      this.router.navigate(['/error']);
    }
  }
  viewLoanDetail(loanId:string){
      console.log(loanId);
      if (loanId) {
        this.router.navigate(['/dashboard/sme-loan-detail', loanId]);
        console.log(loanId);
        
      } else {
        console.warn('Loan ID is missing');
      }
  }
}
