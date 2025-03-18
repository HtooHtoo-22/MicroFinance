import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HPLoan, HpLoanService } from '../../../service/hp-loan.service';
import { MatDialog } from '@angular/material/dialog';
import { ModelComponent } from '../../model/model.component';
import { ProductService } from '../../../service/product.service';
import { Router } from '@angular/router';
import { RateService } from '../../../service/rate.service';

import { Rate } from '../../../model/Rate';
import { CurrentAccount } from '../../../model/CurrentAcc';
import { CurrentAccService } from '../../../service/current-acc.service';

@Component({
  selector: 'app-hp-loan-form',
  standalone: false,
  templateUrl: './hp-loan-form.component.html',
  styleUrl: './hp-loan-form.component.css'
})


export class HpLoanFormComponent {
  loanForm: FormGroup;
  isSubmitted = false;
  interestRate:number=0;
  currentAccounts: CurrentAccount[] = [];

  constructor(
    private fb: FormBuilder,
     private hpLoanService: HpLoanService,
     private rateService: RateService,
      private dialog: MatDialog,
      private productService: ProductService,
      private currentAccService: CurrentAccService,
      private router: Router) {


    this.loanForm = this.fb.group({
      // loanAmount: [null, Validators.required],
      interestRate: [0.0, Validators.required],
      gracePeriod: [0, Validators.required],
      tenor: [null, Validators.required],
      entryUserId: [1, Validators.required],  // Replace with dynamic user ID
      currentAccountId: [0, Validators.required],
      productId: [null, Validators.required],
      downPaymentRate: [null],
      dealerCommissionRate: [null, Validators.required],
      productName: [''], 
      productValue: [0],
      
    });
  }


  loadHPRate(rateType: string): void {
    this.rateService.getRateByType(rateType).subscribe({
      next: (rate: Rate) => {
        this.interestRate = rate.value; // Assuming the rate object has a 'value' property
        console.log('Rate Value:', this.interestRate); // Log the rate value for debugging
        this.loanForm.patchValue({ interestRate: this.interestRate });
      },
      error: (err) => {
        console.error('Error fetching rate:', err);
      }
    });
  }


  private loadCurrentAccounts() {
   
    
    this.currentAccService.listCurrentAcc().subscribe({
      next: (response) => {
        this.currentAccounts = response.data;
        console.log('Current Accounts:', this.currentAccounts);
      },
      error: () => this.showModal('Failed to load currenat Account',false)
    });

    
  }
  
  ngOnInit(): void {

    this.loadCurrentAccounts();
    this.loadHPRate('HP Loan Interest Rate');

    const selectedProduct = this.productService.getSelectedProduct();
    if (selectedProduct) {
      this.loanForm.patchValue({
        productId: selectedProduct.id,
        productName: selectedProduct.productName,
        productValue: selectedProduct.value,
      });
      this.productService.clearSelectedProduct();
    }

   
  }
  goToProductList() {
    this.router.navigate(['dashboard/product-list']); 
  }

  submitLoan() {
    if (this.loanForm.invalid) return;
    
    const loanData: HPLoan = this.loanForm.value;
    
    this.hpLoanService.registerLoan(loanData).subscribe({
      next: (response) => {
        console.log('Loan registered successfully:', response);
        this.showModal('Loan registered successfully!', true);
        this.isSubmitted = true;
        this.loanForm.reset();
      },
      error: (err) => {
        console.error('Error registering hp loan:', err);
  
          let errorMessage = 'Failed to register loan';
          if (err.error && err.error.message) {
            errorMessage = err.error.message;
          }
  
          this.showModal(errorMessage, false);
        }
    });
  }

  showModal(message: string, success: boolean): void {
        this.dialog.open(ModelComponent, {
          width: '300px',
          data: { message, success, }
        });
      }

     
      
    
}