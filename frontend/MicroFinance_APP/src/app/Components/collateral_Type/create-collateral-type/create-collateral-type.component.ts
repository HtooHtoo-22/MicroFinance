import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CollateralService } from '../../../service/collateral.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-collateral-type',
  standalone: false,
  templateUrl: './create-collateral-type.component.html',
  styleUrl: './create-collateral-type.component.css'
})
export class CreateCollateralTypeComponent implements OnInit {
  collateralTypeForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private CollateralType: CollateralService,
    private router: Router
  ) {
    this.collateralTypeForm = this.fb.group({
      name: ['', Validators.required]
    });
  }

  ngOnInit(): void {
  }

  onSubmit(): void {
    if (this.collateralTypeForm.valid) {
      this.CollateralType.createCollateralType(this.collateralTypeForm.value).subscribe(
        () => this.router.navigate(['/list']),
        error => console.error('Error creating collateral type', error)
      );
    }
  }
}
