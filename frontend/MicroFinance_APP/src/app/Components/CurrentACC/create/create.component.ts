import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CurrentAccService } from '../../../service/current-acc.service';
import { CurrentAccount } from '../../../model/CurrentAcc';
import { Cif } from '../../../model/CIF';
import { CifService } from '../../../service/cif.service';

@Component({
  selector: 'app-current-account-register',
  standalone: false,
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.css']
})
export class CreateComponent implements OnInit {
  currentAccountForm!: FormGroup;
  cifs: Cif[] = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private currentAccService: CurrentAccService,
    private cifService: CifService
  ) {}

  ngOnInit(): void {
    this.cifService.listCif().subscribe({
      next: (response) => {
        console.log('CIFs loaded:', response); // Add logging
        this.cifs = response.data || [];
      },
      error: (error) => {
        console.error('Error fetching CIFs:', error);
        alert('Failed to load CIFs');
      }
    });

    this.currentAccountForm = this.fb.group({
      cifId: [null, Validators.required],
      maxAmount: ['', [Validators.required, Validators.min(0)]],
      minAmount: ['', [Validators.required, Validators.min(0)]],
      totalBalance: [0],
      freezeStatus: [false],
      accountId: ['']
    });

    this.route.params.subscribe(params => {
      const cifId = params['cifId'];
      if (cifId) {
        this.currentAccountForm.patchValue({ cifId: cifId });
      }
    });
  }

  onSubmit() {
    if (this.currentAccountForm.valid) {
      const formValue = this.currentAccountForm.value;
      const cifId = Number(formValue.cifId);
      if (isNaN(cifId)) {
        console.error('CIF ID is not a valid number');
        alert('Invalid CIF ID');
        return;
      }

      const accountData: CurrentAccount = {
        accountId: '',
        maxAmount: Number(formValue.maxAmount),
        minAmount: Number(formValue.minAmount),
        cifId: cifId,
        totalBalance: 0,
        freezeStatus: false,
        createDate: '',
        userName: ''
      };

      this.currentAccService.createCurrentAccount(accountData).subscribe({
        next: (response) => {
          console.log('Current Account created successfully:', response);
          this.router.navigate(['/entry-dashboard/current-acc-list']);
        },
        error: (error) => {
          console.error('Error creating Current Account:', error);
          alert('Error creating current account. Please try again.');
        }
      });
    } else {
      console.log('Form is invalid');
    }
  }
}