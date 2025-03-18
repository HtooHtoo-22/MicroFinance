import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Branch, Role, UserDTO } from '../../../model/user';
import { UserService } from '../../../service/user.service';

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
  submitted = false;
  showSuccessModal = false;
  showAlert = false;
  alertType = '';
  alertMessage = '';
nameControl: any;
branchIdControl: any;
roleIdControl: any;

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
    
    this.userService.getBranches().subscribe({
      next: (branches) => this.branches = branches,
      error: () => this.showError('Failed to load branches')
    });

    this.userService.getRoles().subscribe({
      next: (roles) => this.roles = roles,
      error: () => this.showError('Failed to load roles')
    });
  }

  createUser() {
    this.submitted = true;
    
    if (this.userForm.valid) {
      this.loading = true;
      const userData: UserDTO = {
        name: this.userForm.get('name')?.value.trim(),
        branchId: parseInt(this.userForm.get('branchId')?.value),
        roleId: parseInt(this.userForm.get('roleId')?.value),
        active: true,
        
      };
  
      this.userService.createUser(userData).subscribe({
        next: () => {
          this.showSuccessModal = true;
          setTimeout(() => this.closeModal(), 3000);
        },
        error: () => this.showError('Error creating user')
      });
    } else {
      this.showError('Please fill in all required fields correctly.');
    }
  }

  showError(message: string) {
    this.alertType = 'error';
    this.alertMessage = message;
    this.showAlert = true;
    this.loading = false;
    setTimeout(() => this.showAlert = false, 3000);
  }

  closeModal(): void {
    this.showSuccessModal = false;
  }
}
