import { Component } from '@angular/core';
import { Cif } from '../../../model/CIF';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CifService } from '../../../service/cif.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Branch } from '../../../model/user';
import { UserService } from '../../../service/user.service';
import { AuthService } from '../../../service/auth.service';

@Component({
  selector: 'app-create-cif',
  standalone: false,
  templateUrl: './create-cif.component.html',
  styleUrl: './create-cif.component.css'
})
export class CreateCifComponent {
  cifForm: FormGroup;
  branches: Branch[] = [];
  frontNRCFile?: File;
  backNRCFile?: File;
  userPhotoFile?: File;
  cifId?: number;
  isEditMode = false;
  loading = false;
  errorMessage = '';
  submitted = false;

  constructor(private fb: FormBuilder,
              private cifService: CifService,
              private route: ActivatedRoute,
              private router: Router,
              private userService: UserService,
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
      branchId: [null, Validators.required],
      code: [''],
      userId: [this.authService.getCurrentUserId(), Validators.required],
    });
  }
  ngOnInit() {
    this.loadBranches();
    // Check if we are in edit mode (via the route params)
    this.cifId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.cifId) {
      this.isEditMode = true;
      this.loadCifData(this.cifId); // Load the CIF data to pre-fill the form
    }
  }

  private loadBranches() {
    this.loading = true;
    this.errorMessage = '';

    this.userService.getBranches().subscribe({
      next: (branches) => {
        this.branches = branches;
        console.log('Loaded branches:', branches);
      },
      error: (error) => {
        this.errorMessage = 'Failed to load branches: ' + error;
        console.error('Branch loading error:', error);
      },
      complete: () => this.loading = false
    });
  }

  // Load CIF data for editing
  loadCifData(id: number) {
    this.cifService.getCifById(id).subscribe({
      next: (cif) => {
        console.log("Loaded CIF from API:", cif); // Debugging
        if (!cif) {
          console.warn("CIF data is undefined! Check API response.");
        }
        this.cifForm.patchValue(
          {
            cifId: cif.cifId,
            userName: cif.userName,
            gender :cif.gender,
            job:cif.job,
            incomeAmount:cif.incomeAmount,
            nrc:cif.nrc,
            phone:cif.phone,
            email:cif.email,
            state:cif.state,
            township:cif.township,
            address:cif.address,
            status: this.cifForm.value.status.toUpperCase(), // Convert to uppercase (e.g., "ACTIVE")
            branchId:cif.branchId,
       } );
      },
      error: (err) => {
        console.error("Error loading CIF data", err);
      }
    });
  }
  
  // Handle file selection
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

  // Method to create a new CIF
  createCif() {
    if (this.cifForm.valid && this.frontNRCFile && this.backNRCFile && this.userPhotoFile) {
      const userId = this.authService.getCurrentUserId();
      if (!userId) {
        console.error('No user ID found. Please log in again.');
        this.router.navigate(['/login']);
        return;
      }

      const cifData: Cif = {
        ...this.cifForm.value,
        userId: userId
      };

      this.cifService.createCif(cifData, this.frontNRCFile, this.backNRCFile, this.userPhotoFile).subscribe({
        next: (response) => {
          console.log('CIF created successfully', response);
          this.cifForm.reset();
          this.router.navigate(['/list-cif']);
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

  
  // Method to update an existing CIF
  updateCif() {
    if (this.cifForm.valid) {
      const cifData: Cif = this.cifForm.value;
      this.cifService.updateCif(this.cifId!, cifData).subscribe({
        next: (response) => {
          console.log('CIF updated successfully', response);
          this.cifForm.reset();
          this.router.navigate(['/list-cif']); // Navigate to the list of CIFs after successful update
        },
        error: (err) => {
          console.error('Error updating CIF', err);
        }
      });
    }
  }
  onSubmit() {
    if (this.isEditMode) {
      this.updateCif(); // Call update function if editing
    } else {
      this.createCif(); // Call create function if adding new CIF
    }
  }
  
  // Add getter for branchIdControl
  get branchIdControl() {
    return this.cifForm.get('branchId');
  }
}
