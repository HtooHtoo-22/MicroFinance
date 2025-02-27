import { Component, OnInit } from '@angular/core';
import { Cif } from '../../../model/CIF';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CifService } from '../../../service/cif.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../service/auth.service';

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
  cifId?: number;
  isEditMode = false;
  loading = false;
  errorMessage = '';
  submitted = false;

  constructor(
    private fb: FormBuilder,
    private cifService: CifService,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
  ) {
    this.cifForm = this.fb.group({
      userName: ['', Validators.required],
      gender: ['', Validators.required],
      job: ['', Validators.required],
      incomeAmount: [null, [Validators.required, Validators.min(0)]],
      nrc: ['', Validators.required],
      phone: ['+95 ', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      state: ['', Validators.required],
      township: ['', Validators.required],
      address: ['', Validators.required],
    });
  }

  ngOnInit() {
    this.cifId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.cifId) {
      this.isEditMode = true;
      this.loadCifData(this.cifId);
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
          status: this.cifForm.value.status.toUpperCase(),
        });
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
          console.log('CIF created successfully', response);
          this.cifForm.reset();
          this.router.navigate(['/dashboard/cif-list']);
        },
        error: (err) => {
          console.error('Error creating CIF:', err);
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
      this.cifService.updateCif(this.cifId!, cifData).subscribe({
        next: (response) => {
          console.log('CIF updated successfully', response);
          this.cifForm.reset();
          this.router.navigate(['/dashboard/cif-list']);
        },
        error: (err) => {
          console.error('Error updating CIF', err);
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
}