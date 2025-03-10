import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { SmeLoanService } from '../../service/sme-loan.service';
import { CurrentAccService } from '../../service/current-acc.service';
import { RateService } from '../../service/rate.service';
import { Rate } from '../../model/Rate';
import { CollateralService } from '../../service/collateral.service';
import { ApiResponse } from '../../model/Apirespon';
import { CollateralDTO } from '../../model/CollateralDTO';

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
  collaterals: any[] = [];
  loanAmount: number = 0;
  requiredCollateral: number = 1; // Add this line
  message:string = '';
  error:boolean = false;
  constructor(
    private fb: FormBuilder,
    private smeLoanService: SmeLoanService,
    private router: Router,
    private authService: AuthService,
    private currentAccService: CurrentAccService,
    private rateService : RateService,
    private collateralService: CollateralService
  ) {}

  ngOnInit(): void {
    this.smeLoanForm = this.fb.group({
      loanAmount: ['', [Validators.required, Validators.min(1)]],
      interestRate: ['', ],
      gracePeriod: [''],  // ✅ Changed from `null` to `''`
      loanPurpose: ['', [Validators.required, Validators.maxLength(200)]],
      documentFee: ['', [Validators.required, Validators.min(0)]],
      serviceCharge: [''],

      duration: ['', [Validators.required, Validators.min(1)]],
      currentAccountaccId: ['', [Validators.required, Validators.min(1)]],  
      entryUserId: [this.authService.getCurrentUserId() || '', [Validators.required, Validators.min(1)]],  // ✅ Added
    });
    this.loadCurrentAccounts();
    this.loadSMERate('SME Loan Interest Rate');
    this.loadServiceChargeRate("Service Charges Rate");
    this.onChanges();
    

  }

  onSubmit(): void {
    console.log('Form Value:', this.smeLoanForm.value);
    console.log('Form Valid:', this.smeLoanForm.valid);
    
    
    if (this.smeLoanForm.valid) {
      const loanData = {
        ...this.smeLoanForm.value,
        collateralIds: this.selectedCollateralIds
      };
  
      this.smeLoanService.createLoan(loanData).subscribe({
        next: (response: any) => {
          console.log('Loan Created Successfully:', response);
          this.message = response.message;
        
          // Navigate after a short delay (optional like teacherService)
          
        },
        error: (error: any) => {
          this.message = error.error.message; // or error.message depending on backend structure
          this.error = true;
          console.error('Error while creating loan:', error);
        }
      });
      
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
        this.serviceChargeValue = rate.value;
  
        console.log('Service Rate Value:', this.serviceChargeValue);
  
        // 🔁 Recalculate serviceCharge if loanAmount was already filled
        const amount = this.smeLoanForm.get('loanAmount')?.value || 0;
        const charge = amount * (this.serviceChargeValue / 100);
        this.smeLoanForm.get('serviceCharge')?.setValue(charge.toFixed(2), { emitEvent: false });
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
    // Current Account Autocomplete filter
    this.smeLoanForm.get('currentAccountaccId')?.valueChanges.subscribe(value => {
      this.filterAccounts(value);
    });
  
    // 💡 Add this: Calculate serviceCharge when loanAmount changes
    this.smeLoanForm.get('loanAmount')?.valueChanges.subscribe((amount: number) => {
      if (amount && this.serviceChargeValue) {
        const charge = amount * (this.serviceChargeValue / 100);
        this.smeLoanForm.get('serviceCharge')?.setValue(charge.toFixed(2), { emitEvent: false });
      } else {
        this.smeLoanForm.get('serviceCharge')?.setValue(0, { emitEvent: false });
      }
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
  isStepOne: boolean = true;

goToNextStep() {
  console.log("Hi");
  
  const accountId = this.smeLoanForm.value.currentAccountaccId;
  this.isStepOne = false;
  this.fetchCollateral(accountId);
}

goToPreviousStep() {
  this.selectedCollaterals.clear();
  this.isStepOne = true;
}
fetchCollateral(accountId: string) {
  this.collateralService.getCollateralByCurrentAccountId(accountId).subscribe(
    (response: ApiResponse<CollateralDTO>) => {
      if (response && response.data) {
        this.collaterals = Array.isArray(response.data) ? response.data : [];
      } else {
        console.error('No collateral data received.');
      }
    },
    (error) => {
      console.error('Error fetching collateral:', error);
    }
  );
}
// Component class
selectedCollaterals = new Set<any>();
selectedCollateralIds:number[]=[];
get totalSelectedCollateral(): number {
  return Array.from(this.selectedCollaterals).reduce(
    (sum, item) => sum + item.remainingValue, 0
  );
}

isSelected(item: any): boolean {
  return this.selectedCollaterals.has(item);
}

toggleSelection(item: any): void {
  if (item.remainingValue <= 0) return;
  
  if (this.selectedCollaterals.has(item)) {
    this.selectedCollaterals.delete(item);
  } else {
    this.selectedCollaterals.add(item);
  }
  if (this.selectedCollateralIds.includes(item.id)) {
    this.selectedCollateralIds = this.selectedCollateralIds.filter(id => id !== item.id); // Deselect
  } else {
    this.selectedCollateralIds.push(item.id); // Select
  }
}
logFormValue(): void {
  this.smeLoanForm.get('serviceCharge')?.setValue(this.serviceChargeValue);
  console.log("Selected Collateral Ids : "+this.selectedCollateralIds);
  
  console.log("Selected Collateral : "+this.selectedCollaterals);
  console.log('Form Value:', this.smeLoanForm.value);
  console.log('Form Valid:', this.smeLoanForm.valid);
}

}