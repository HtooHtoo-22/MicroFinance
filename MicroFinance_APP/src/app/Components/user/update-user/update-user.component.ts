import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../../service/user.service';
import { finalize } from 'rxjs';
import { Branch, Role, UserDTO, UserResponseDTO } from '../../../model/user';
import { MatDialog } from '@angular/material/dialog';
import { ModelComponent } from '../../model/model.component';

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
    private userService: UserService,
    private dialog: MatDialog,


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
  
   

    this.loadBranchesAndRoles().then(() => {
      this.loadUser();
    });
  }


  async loadBranchesAndRoles() {
    await Promise.all([
      new Promise((resolve) => {
        this.userService.getBranches().subscribe((branches) => {
          this.branches = branches;
          console.log('Branches:', this.branches);
          resolve(true);
        });
      }),
      new Promise((resolve) => {
        this.userService.getRoles().subscribe((roles) => {
          this.roles = roles;
          console.log('Roles:', this.roles);
          resolve(true);
        });
      })
    ]);
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
       



      const matchingBranch = this.branches.find(branch => branch.name === response.branchName);
      const branchId = matchingBranch ? matchingBranch.id : null;

      // Find the matching roleId based on roleName
      const matchingRole = this.roles.find(role => role.roleName === response.roleName);
      const roleId = matchingRole ? matchingRole.id : null;
  
        this.userForm.patchValue({
          name: response.name ?? '',
          branchId: branchId, // Assign matched branchId
        roleId: roleId , // Ensure it's a number
          
        });
  
      
        this.loading = false;
      },
      (error) => {
        console.error('Error loading user:', error);
        this.showModal('Failed to load user data.', false);
        this.errorMessage = 'Failed to load user data';
        this.loading = false;
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
        this.showModal('User updated successfully!', true);
        this.router.navigate(['/dashboard/list-users']);
      },
      (error) => {
        console.error('Error updating user:', error);
        this.showModal('Failed to update user.', false);
        this.loading = false;
      }
    );
  }

   showModal(message: string, success: boolean): void {
      this.dialog.open(ModelComponent, {
        width: '300px',
        data: { message, success, }
      });
    }
}