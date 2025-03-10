import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CollateralService } from '../../../service/collateral.service';
import { CollateralTypeDTO } from '../../../model/Collateral';

@Component({
  selector: 'app-collateral-type-update',
  standalone: false,
  templateUrl: './collateral-type-update.component.html',
  styleUrl: './collateral-type-update.component.css'
})
export class CollateralTypeUpdateComponent implements OnInit {
  updateForm: FormGroup;
  collateralTypeId: number;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private collateralService: CollateralService
  ) {
    this.updateForm = this.fb.group({
      name: ['', Validators.required]
    });
    this.collateralTypeId = 0;
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.collateralTypeId = +params['id'];
      this.loadCollateralType();
    });
  }

  loadCollateralType(): void {
    this.collateralService.getCollateralTypeById(this.collateralTypeId).subscribe(
      (response: CollateralTypeDTO) => {
        console.log('Loaded collateral type:', response);
        this.updateForm.patchValue({
          name: response.name
        });
      },
      (error) => {
        console.error('Error loading collateral type:', error);
      }
    );
  }

  onSubmit(): void {
    if (this.updateForm.valid) {
      const updatedCollateralType: CollateralTypeDTO = {
        id: this.collateralTypeId,
        name: this.updateForm.get('name')?.value
      };

      this.collateralService.updateCollateralType(this.collateralTypeId, updatedCollateralType).subscribe(
        () => {
          this.router.navigate(['/dashboard/collateral-type-list']);
        },
        (error) => {
          console.error('Error updating collateral type:', error);
        }
      );
    }
  }

  onCancel(): void {
    this.router.navigate(['/dashboard/collateral-type-list']);
  }
}
