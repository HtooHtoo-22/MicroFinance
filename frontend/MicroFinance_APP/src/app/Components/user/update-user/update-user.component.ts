import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../../service/user.service';
import { finalize } from 'rxjs';
import { Branch, Role, UserDTO, UserResponseDTO } from '../../../model/user';

@Component({
  selector: 'app-update-user',
  standalone: false,
  templateUrl: './update-user.component.html',
  styleUrl: './update-user.component.css'
})
export class UpdateUserComponent implements OnInit {
  userForm!: FormGroup;
  userId!: number;
  loading = false;
  submitted: boolean = false;
  errorMessage: string | null = null;
  roles: Role[]=[];
  branches: Branch[] = [];

  get nameControl() {
    return this.userForm.get('name');
  }
  get branchIdControl() {
    return this.userForm.get('branchId');
  }
  get roleIdControl() {
    return this.userForm.get('roleId');
  }

  constructor(
    private route: ActivatedRoute,
    private router: Router,  
    private fb: FormBuilder,
    private userService: UserService


  ){
    this.userForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      branchId: ['', Validators.required],
      roleId: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.userId = Number(this.route.snapshot.paramMap.get('id'));
  
    if (!this.userId) {
      console.error('Invalid userId:', this.userId);
      this.errorMessage = 'Invalid User ID';
      return;
    }
  
    this.loadUser();
    this.loadBranches();
    this.loadRoles();
  }
  

  loadUser() {
    this.loading = true;

    this.userService.getUser(this.userId).subscribe(
      (response: UserResponseDTO) => {
        console.log('User API Response:', response); // Log the full API response
  
        if (!response) {
          this.errorMessage = 'User data is empty';
          this.loading = false;
          return;
        }
        console.log('Branch ID:', response.branchId, 'Type:', typeof response.branchId);
        console.log('Role ID:', response.roleId, 'Type:', typeof response.roleId);
  
        this.userForm.patchValue({
          name: response.name ?? '',
          branchId: response.branchId ? response.branchId.toString() : '', // Ensure value exists
          roleId: response.roleId ? response.roleId.toString() : ''
        });
  
      
        this.loading = false;
      },
      (error) => {
        console.error('Error loading user:', error);
        this.errorMessage = 'Failed to load user data';
        this.loading = false;
      }
    );
  }
  
  loadBranches() {
    this.userService.getBranches().subscribe(
      (branches) => {
        console.log('Branches List:', branches); // Debug log
        this.branches = branches;

       
      },
      (error) => {
        console.error('Error loading branches:', error);
        this.errorMessage = 'Failed to load branches';
      }
    );
  }
  

  loadRoles() {
    this.userService.getRoles().subscribe(
      (roles) => {
        console.log('Roles List:', roles); // Debug log
        this.roles = roles;

        
      },
      (error) => {
        console.error('Error loading roles:', error);
        this.errorMessage = 'Failed to load roles';
      }
    );
  }
  




  updateUser() {
    this.submitted = true;
    if (this.userForm.invalid) return;

    this.loading = true;
    this.userService.updateUser(this.userId, this.userForm.value).subscribe(
      () => {
        this.loading = false;
        alert('User updated successfully!');
        this.router.navigate(['/dashboard/list-users']);
      },
      (error) => {
        console.error('Error updating user:', error);
        this.errorMessage = 'Failed to update user';
        this.loading = false;
      }
    );
  }
}