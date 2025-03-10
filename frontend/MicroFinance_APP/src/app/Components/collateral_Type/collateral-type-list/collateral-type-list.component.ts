import { Component, OnInit } from '@angular/core';
import { CollateralService } from '../../../service/collateral.service';
import { CollateralTypeDTO } from '../../../model/Collateral';
import { Router } from '@angular/router';

@Component({
  selector: 'app-collateral-type-list',
  standalone: false,
  templateUrl: './collateral-type-list.component.html',
  styleUrl: './collateral-type-list.component.css'
})
export class CollateralTypeListComponent implements OnInit {
  collateralTypes: CollateralTypeDTO[] = [];
  
  constructor(
    private collateralService: CollateralService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCollateralTypes();
  }

  loadCollateralTypes(): void {
    this.collateralService.getAllCollateralTypes().subscribe(
      (data) => {
        console.log('Raw API response:', data); // Debug log
        if (data && Array.isArray(data)) {
          this.collateralTypes = data;
        } else if (data && typeof data === 'object') {
          this.collateralTypes = Object.values(data);
        } else {
          console.error('Unexpected data format:', data);
          this.collateralTypes = [];
        }
      },
      (error) => {
        console.error('Error fetching collateral types:', error);
        this.collateralTypes = [];
      }
    );
  }

  editCollateralType(id: number): void {
    this.router.navigate([`/dashboard/edit-collateral-type/${id}`]);
  }

  deleteCollateralType(id: number): void {
    if (confirm('Are you sure you want to delete this collateral type?')) {
      this.collateralService.deleteCollateralType(id).subscribe(
        () => {
          this.loadCollateralTypes();
        },
        (error) => {
          console.error('Error deleting collateral type:', error);
        }
      );
    }
  }
}
