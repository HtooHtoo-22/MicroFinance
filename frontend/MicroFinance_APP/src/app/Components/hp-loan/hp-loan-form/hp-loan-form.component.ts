import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CurrentAccount } from '../../../model/CurrentAcc';
import { HPLoan, HpLoanService } from '../../../service/hp-loan.service';
import { RateService } from '../../../service/rate.service';
import { MatDialog } from '@angular/material/dialog';
import { ProductService } from '../../../service/product.service';
import { CurrentAccService } from '../../../service/current-acc.service';
import { ModelComponent } from '../../model/model.component';
import { Rate } from '../../../model/Rate';
import { Router } from '@angular/router';
import { AuthService } from '../../../service/auth.service';

@Component({
  selector: 'app-hp-loan-form',
  standalone: false,
  templateUrl: './hp-loan-form.component.html',
  styleUrls: ['./hp-loan-form.component.css']
})
export class HpLoanFormComponent {
  loanForm: FormGroup;
  isSubmitted = false;
  interestRate: number = 0;
  currentAccounts: CurrentAccount[] = [];
  products: any[] = []; // Add this line


  constructor(
    private fb: FormBuilder,
    private hpLoanService: HpLoanService,
    private rateService: RateService,
    private dialog: MatDialog,
    private productService: ProductService,
    private currentAccService: CurrentAccService,
    private authService: AuthService,
    private router: Router
  ) {
    this.loanForm = this.fb.group({
      interestRate: [0.0, Validators.required],
      gracePeriod: [null, Validators.required], // Changed from empty array to null
      tenor: [null, [Validators.required, Validators.min(1)]], // Added min validator
      currentAccountId: [null, Validators.required], // Changed to null
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
        this.interestRate = rate.value;
        console.log('Rate Value:', this.interestRate);
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
      error: () => this.showModal('Failed to load current Account', false)
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
    const branchId = this.authService.getCurrentUserBranchId();
    if (branchId) {
      this.productService.getProductsByBranchId(+branchId).subscribe({
        next: (res) => this.products = res.data,
        error: (err) => console.error(err)
      });
    }
  }

  goToProductList() {
    const branchId = this.authService.getCurrentUserBranchId();
    if (branchId) {
      this.router.navigate(['/operation-dashboard/all-list', { branchId }]);
    }
  }
  
  submitLoan() {
    const entryUserId = this.authService.getCurrentUserId();

    if (!entryUserId) {
      console.error('No user ID found. Please log in again.');
      this.router.navigate(['/login']);
      return;
    }

    if (this.loanForm.invalid) {
      console.log('Form is invalid. Errors:', this.loanForm.errors);
      Object.keys(this.loanForm.controls).forEach((key) => {
        const control = this.loanForm.get(key);
        if (control?.invalid) {
          console.log(`Field ${key} is invalid:`, control.errors);
        }
      });
      return;
    }

    const loanData: HPLoan = {
      ...this.loanForm.value,
      entryUserId: parseInt(entryUserId, 10) // Convert to number
    };

    this.hpLoanService.registerLoan(loanData).subscribe({
      next: (response) => {
        console.log('Loan registered successfully:', response);
        this.showModal('Loan registered successfully!', true);
        this.isSubmitted = true;
        this.loanForm.reset();
      },
      error: (err) => {
        console.error('Error registering HP loan:', err);
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
      data: { message, success }
    });
  }

}