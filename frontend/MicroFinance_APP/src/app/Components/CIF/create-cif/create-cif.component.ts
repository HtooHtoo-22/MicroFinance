import { Component, OnInit } from '@angular/core';
import { Cif } from '../../../model/CIF';
import { AbstractControl, AsyncValidatorFn, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CifService } from '../../../service/cif.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../service/auth.service';
import { debounceTime, map, Observable, take } from 'rxjs';

import { ModelComponent } from '../../model/model.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-create-cif',
  standalone: false,
  templateUrl: './create-cif.component.html',
  styleUrl: './create-cif.component.css'
})
export class CreateCifComponent implements OnInit {
  cifForm: FormGroup;
  frontNRCFile?: File;
  backNRCFile?: File;
  userPhotoFile?: File;
  id?: number;
  cifId?: number;
  isEditMode = false;
  submitted = false;
showSuccessModal: any;
showAlert: any;
alertMessage: any;
alertType: any;

  constructor(
    private fb: FormBuilder,
    private cifService: CifService,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private dialog: MatDialog
  ) {
    this.cifForm = this.fb.group({
      userName: ['', Validators.required],
      gender: ['', Validators.required],
      job: ['', Validators.required],
      incomeAmount: [null, [Validators.required, Validators.min(0)]],
      nrc: ['', Validators.required, this.isEditMode ? [] : [this.nrcExistsValidator()]], // Only apply validator in create mode
      email: ['', [Validators.required, Validators.email], this.isEditMode ? [] : [this.emailExistsValidator()]],
      phone: ['+95 ', [Validators.required]],
      state: ['', Validators.required],
      township: ['', Validators.required],
      address: ['', Validators.required],
    });
  }

  nrcExistsValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<{ [key: string]: any } | null> => {
      return this.cifService.checkNRC(control.value).pipe(
        debounceTime(500), // Delay validation to prevent instant trigger
        take(1),
        map(exists => (exists ? { nrcExists: true } : null))
      );
    };
  }

  emailExistsValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<{ [key: string]: any } | null> => {
      return this.cifService.checkEmail(control.value).pipe(
        debounceTime(500), // Delay validation to prevent instant trigger
        take(1),
        map(exists => (exists ? { emailExists: true } : null))
      );
    };
  }

  ngOnInit() {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    if (this.id) {
      this.isEditMode = true;
      this.loadCifData(this.id);
    }
  }

  loadCifData(id: number) {
    this.cifService.getCifById(id).subscribe({
      next: (cif) => {
        console.log("Loaded CIF from API:", cif);
        this.cifForm.patchValue({
          cifId: cif.cifId,
          userName: cif.userName,
          gender: cif.gender,
          job: cif.job,
          incomeAmount: cif.incomeAmount,
          nrc: cif.nrc,
          phone: cif.phone,
          email: cif.email,
          state: cif.state,
          township: cif.township,
          address: cif.address,
          status: cif.status.toUpperCase(),
        });

        this.cifForm.get('nrc')?.clearAsyncValidators();
        this.cifForm.get('email')?.clearAsyncValidators();
        this.cifForm.get('nrc')?.updateValueAndValidity();
        this.cifForm.get('email')?.updateValueAndValidity();
      },
      error: (err) => {
        console.error("Error loading CIF data", err);
      }
    });
  }

  onFileSelected(event: any, fileType: string) {
    const file = event.target.files[0];
    if (fileType === 'frontNRC') {
      this.frontNRCFile = file;
    } else if (fileType === 'backNRC') {
      this.backNRCFile = file;
    } else if (fileType === 'userPhoto') {
      this.userPhotoFile = file;
    }
  }

  createCif() {
    if (this.cifForm.valid && this.frontNRCFile && this.backNRCFile && this.userPhotoFile) {
      const userId = this.authService.getCurrentUserId();
      const branchId = this.authService.getCurrentUserBranchId();

      if (!userId || !branchId) {
        console.error('No user ID or branch ID found. Please log in again.');
        this.router.navigate(['/login']);
        return;
      }

      const cifData: Cif = {
        ...this.cifForm.value,
        userId: userId,
        branchId: branchId
      };

      this.cifService.createCif(cifData, this.frontNRCFile, this.backNRCFile, this.userPhotoFile).subscribe({
        next: (response) => {
          this.showModal('CIF created successfully', true);
          console.log('CIF created successfully', response);
          this.cifForm.reset();
          this.router.navigate(['/dashboard/cif-list']);
        },
        error: (err) => {
          console.error('Error creating CIF:', err);
          this.showModal('Failed to create CIF', false);

          if (err.status) {
            console.error('HTTP Error Status:', err.status);
          }
          if (err.error) {
            console.error('Error Response:', err.error);
          }
        }
      });
    } else {
      console.warn('Please fill in all fields and select all required files.');
    }
  }

  updateCif() {
    if (this.cifForm.valid) {
      const cifData: Cif = this.cifForm.value;
      this.cifService.updateCif(this.id!, cifData).subscribe({
        next: (response) => {
          console.log('CIF updated successfully', response);
          this.showModal('CIF updated successfully', true);
          this.router.navigate(['/dashboard/cif-list']);
        },
        error: (err) => {
          console.error('Error updating CIF:', err);

          let errorMessage = 'Failed to update CIF';
          if (err.error && err.error.message) {
            errorMessage = err.error.message; // Show the actual backend error
          }

          this.showModal(errorMessage, false);
        }
      });
    }
  }

  onSubmit() {
    this.submitted = true;
    if (this.isEditMode) {
      this.updateCif();
    } else {
      this.createCif();
    }
  }

  showModal(message: string, success: boolean): void {
    this.dialog.open(ModelComponent, {
      width: '300px',
      data: { message, success }
    });
  }

  closeModal(): void {
    this.router.navigate(['/dashboard/cif-list']);
  }
}