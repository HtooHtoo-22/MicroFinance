import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { SmeLoanService } from '../../service/sme-loan.service';
import { CurrentAccService } from '../../service/current-acc.service';
import { RateService } from '../../service/rate.service';
import { Rate } from '../../model/Rate';

@Component({
  selector: 'app-sme-loan-register',
  standalone:false,
  templateUrl: './sme-loan-register.component.html',
  styleUrls: ['./sme-loan-register.component.css']
})
export class SmeLoanRegisterComponent implements OnInit {
  smeLoanForm!: FormGroup;
  collateralIds: number[] = [15];
  accountList: any[] = [];
  filteredAccounts: any[] = [];
  rateValue: number = 0;
  serviceChargeValue : number = 0;
  constructor(
    private fb: FormBuilder,
    private smeLoanService: SmeLoanService,
    private router: Router,
    private authService: AuthService,
    private currentAccService: CurrentAccService,
    private rateService : RateService
  ) {}

  ngOnInit(): void {
    this.smeLoanForm = this.fb.group({
      loanAmount: ['', [Validators.required, Validators.min(1)]],
      interestRate: ['', ],
      gracePeriod: ['', [Validators.min(0)]],  // ✅ Changed from `null` to `''`
      loanPurpose: ['', [Validators.required, Validators.maxLength(200)]],
      documentFee: ['', [Validators.required, Validators.min(0)]],
      serviceCharge: ['', [Validators.required, Validators.min(0)]],
      duration: ['', [Validators.required, Validators.min(1)]],
      currentAccountaccId: ['', [Validators.required, Validators.min(1)]],  
      entryUserGenerateId: [this.authService.getCurrentUserId() || '', [Validators.required, Validators.min(1)]],  // ✅ Added
    });
    this.loadCurrentAccounts();
    this.loadSMERate('SME Loan Interest Rate');
    this.loadServiceChargeRate("Service Charges Rate");
    this.onChanges();
    

  }

  onSubmit(): void {
    console.log("Debug");
    console.log('Form Value:', this.smeLoanForm.value);
    console.log('Form Valid:', this.smeLoanForm.valid);
  
    if (this.smeLoanForm.valid) {
      const loanData = {
        ...this.smeLoanForm.value,
        collateralIds: this.collateralIds
      };
  
      this.smeLoanService.createLoan(loanData).subscribe(
        response => {
          console.log('Loan Created Successfully:', response);
          alert('Loan Registered Successfully!');
          this.router.navigate(['/dashboard']);
        },
        error => {
          console.error('Error while creating loan:', error);
          alert('Error while creating loan. Please try again.');
        }
      );
    } else {
      alert('Please fill in the required fields.');
    }
  }

  loadSMERate(rateType: string): void {
    this.rateService.getRateByType(rateType).subscribe({
      next: (rate: Rate) => {
        this.rateValue = rate.value; // Assuming the rate object has a 'value' property
        console.log('Rate Value:', this.rateValue); // Log the rate value for debugging
        this.smeLoanForm.patchValue({ interestRate: this.rateValue });
      },
      error: (err) => {
        console.error('Error fetching rate:', err);
      }
    });
  }

  loadServiceChargeRate(rateType: string): void {
    this.rateService.getRateByType(rateType).subscribe({
      next: (rate: Rate) => {
        this.serviceChargeValue = rate.value; // Assuming the rate object has a 'value' property
        console.log('Service Rate Value:', this.serviceChargeValue); // Log the rate value for debugging
      },
      error: (err) => {
        console.error('Error fetching rate:', err);
      }
    });
  }

  private loadCurrentAccounts(): void {
    this.currentAccService.listCurrentAcc().subscribe({
      next: (accounts) => {
        this.accountList = accounts.data;
        console.log('Current Accounts:', this.accountList);
      },
      error: (err) => {
        console.error('Error fetching Current Account types:', err);
      }
    });
  }

  private onChanges(): void {
    this.smeLoanForm.get('currentAccountaccId')?.valueChanges.subscribe(value => {
      this.filterAccounts(value);
    });
  }

  private filterAccounts(searchTerm: string): void {
    if (!searchTerm) {
      this.filteredAccounts = [];
      return;
    }

    const lowerCaseTerm = searchTerm.toLowerCase();
    this.filteredAccounts = this.accountList.filter(account => 
      account.accountId.toLowerCase().includes(lowerCaseTerm)
    );
  }

  selectAccount(account: any): void {
    this.smeLoanForm.get('currentAccountaccId')?.setValue(account.accountId);
    this.filteredAccounts = []; // Clear the suggestions
  }
  logClick(): void {
    console.log('Submit button clicked');
  }
}