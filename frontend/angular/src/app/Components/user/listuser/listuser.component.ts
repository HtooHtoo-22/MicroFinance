import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../services/user.service';
import { UserResponseDTO } from '../../../model/user';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-listuser',
  standalone: false,
  templateUrl: './listuser.component.html',
  styleUrl: './listuser.component.css'
})
export class ListuserComponent implements OnInit {
  users: UserResponseDTO[] = [];
  errorMessage: string | null = null;
  loading: boolean = false;
  currentPage: number = 1;
  itemsPerPage: number = 7;
  totalUsers: number = 0;
  totalPages: number = 0;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.errorMessage = null;
    
    this.userService.getUsers()
      .pipe(
        finalize(() => this.loading = false)
      )
      .subscribe({
        next: (users) => {
          this.users = users;
          this.totalUsers = users.length;
          this.totalPages = Math.ceil(this.totalUsers / this.itemsPerPage);
        },
        error: (err) => {
          this.errorMessage = err.message || 'Failed to load users. Please try again later.';
        }
      });
  }

// Add these methods
get paginatedUsers(): any[] {
  const start = (this.currentPage - 1) * this.itemsPerPage;
  return this.users.slice(start, start + this.itemsPerPage);
}

get startIndex(): number {
  return (this.currentPage - 1) * this.itemsPerPage + 1;
}

get endIndex(): number {
  return Math.min(this.currentPage * this.itemsPerPage, this.totalUsers);
}

get pages(): number[] {
  return Array.from({length: this.totalPages}, (_, i) => i + 1);
}

previousPage(): void {
  if (this.currentPage > 1) this.currentPage--;
}

nextPage(): void {
  if (this.currentPage < this.totalPages) this.currentPage++;
}

goToPage(page: number): void {
  if (page >= 1 && page <= this.totalPages) this.currentPage = page;
}


}
