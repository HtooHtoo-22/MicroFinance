import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';  // ✅ Import this
import { CommonModule } from '@angular/common'; // ✅ Import this if you're using ngIf or ngFor
import { CollateralDTO } from '../../../model/CollateralDTO';
import { CollateralService } from '../../../service/collateral.service';
import { AuthService } from '../../../service/auth.service';
import { ApiResponse } from '../../../model/ApiResponse';

@Component({
  selector: 'app-list-collateral',
  standalone: true,  // ✅ Make sure this is true if you want to import FormsModule directly
  imports: [FormsModule, CommonModule],  // ✅ Include FormsModule here
  templateUrl: './list-collateral.component.html',
  styleUrls: ['./list-collateral.component.css']
})
export class ListCollateralComponent {
  collaterals: CollateralDTO[] = [];
  filteredCollaterals: CollateralDTO[] = [];
  selectedCollateralType: string = "";
  collateralTypes: string[] = [];
  errorMessage: string | null = null;
  
  constructor(private collateralService: CollateralService, private router: Router,
              private authService:AuthService
  ) {}

  ngOnInit(): void {
    this.collateralService.getCollateralByBranchId(Number(this.authService.getCurrentUserBranchId())).subscribe(
      (response: ApiResponse<CollateralDTO[]>) => {
        this.collaterals = response.data;
        this.filteredCollaterals = response.data;
  
        this.collateralTypes = [...new Set(this.collaterals.map(c => c.collateralTypeName))].filter((type): type is string => Boolean(type));
      },
      (error) => {
        this.errorMessage = 'Failed to fetch collaterals.';
        console.error(error);
      }
    );
  }

  sortByCollateralType(): void {
    if (this.selectedCollateralType) {
      this.filteredCollaterals = this.collaterals.filter(
        c => c.collateralTypeName === this.selectedCollateralType
      );
    } else {
      this.filteredCollaterals = this.collaterals;
    }
  }
  
  viewDetails(collateralId: number | undefined): void {
    if (collateralId) {
      this.router.navigate(['/dashboard/collateralDetail', collateralId]);
      console.log(collateralId);
    } else {
      console.warn('Collateral ID is missing');
    }
  }
}