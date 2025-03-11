import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-sme-loan-detail',
  standalone: false,
  templateUrl: './sme-loan-detail.component.html',
  styleUrl: './sme-loan-detail.component.css'
})
export class SmeLoanDetailComponent {
  constructor(private route: ActivatedRoute, 
                public router: Router
               ) {}
  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const collateralId = Number(idParam);
      console.log("SME Loan  Detail id : "+idParam);

    }
  }
}