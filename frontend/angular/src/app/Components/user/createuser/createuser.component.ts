import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Branch, Role, UserDTO } from '../../../model/user';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-createuser',
  standalone: false,
  templateUrl: './createuser.component.html',
  styleUrls: ['./createuser.component.css']
})
export class CreateuserComponent implements OnInit {
  userForm!: FormGroup;
  roles: Role[] = [];
  branches: Branch[] = [];
  loading = false;
  errorMessage = '';
  submitted = false;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit() {
    this.initForm();
    this.loadRolesAndBranches();
  }

  private initForm() {
    this.userForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      roleId: [null, [Validators.required]],
      branchId: [null, [Validators.required]]
    });
  }

  private loadRolesAndBranches() {
    this.loading = true;
    this.errorMessage = '';

    // Load branches
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

    // Load roles
    this.userService.getRoles().subscribe({
      next: (roles) => {
        this.roles = roles;
        console.log('Loaded roles:', roles);
      },
      error: (error) => {
        this.errorMessage = 'Failed to load roles: ' + error;
        console.error('Role loading error:', error);
      },
      complete: () => this.loading = false
    });
  }

  createUser() {
    this.submitted = true;
    
    if (this.userForm.valid) {
      this.loading = true;
      this.errorMessage = '';

      const userData: UserDTO = {
        name: this.userForm.get('name')?.value.trim(),
        branchId: parseInt(this.userForm.get('branchId')?.value),
        roleId: parseInt(this.userForm.get('roleId')?.value),
        active: true
    };
  
      console.log('Submitting user data:', userData);
  
      this.userService.createUser(userData).subscribe({
        next: (response) => {
          console.log('User created successfully:', response);
          alert('User created successfully!');
          this.router.navigate(['/dashboard/list-users']);
        },
        error: (error) => {
          this.errorMessage = error;
          console.error('Error creating user:', error);
          
          // If it's a 403 error, we might want to handle it specially
          if (error.includes('permission')) {
            // Optionally redirect to an access denied page
            // this.router.navigate(['/access-denied']);
            
            // Or show a more specific error message
            this.errorMessage = 'Access Denied: You do not have sufficient permissions to create users. Please contact your administrator.';
          }
          
          this.loading = false;
        },
        complete: () => {
          this.loading = false;
        }
      });
    } else {
      this.errorMessage = 'Please fill in all required fields correctly.';
    }
  }
  

  // Helper methods for template
  get nameControl() { return this.userForm.get('name'); }
  get roleIdControl() { return this.userForm.get('roleId'); }
  get branchIdControl() { return this.userForm.get('branchId'); }
}