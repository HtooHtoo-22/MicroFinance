import { Component, OnInit } from '@angular/core';
import { RoleService } from '../../../service/role.service';
import { Role } from '../../../model/Role';

@Component({
  selector: 'app-list-role',
  standalone: false,
  templateUrl: './role-list.component.html',
  styleUrls: ['./role-list.component.css']
})
export class ListRoleComponent implements OnInit {
  Math: any = Math;
  roles: Role[] = [];
  pagedRoles: Role[] = [];
  
  currentPage: number = 1;
  pageSize: number = 10; // Adjust as needed
  totalRoles: number = 0;
  totalPages: number = 0;
  pages: number[] = [];

  constructor(private roleService: RoleService) { }

  ngOnInit(): void {
    this.roleService.getAllRoles().subscribe(data => {
      this.roles = data;
      this.totalRoles = this.roles.length;
      this.totalPages = Math.ceil(this.totalRoles / this.pageSize);
      this.updatePagination();
    });
  }

  updatePagination() {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = Math.min(startIndex + this.pageSize, this.totalRoles);
    this.pagedRoles = this.roles.slice(startIndex, endIndex);
    this.pages = Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.updatePagination();
    }
  }

  previousPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.updatePagination();
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.updatePagination();
    }
  }
}