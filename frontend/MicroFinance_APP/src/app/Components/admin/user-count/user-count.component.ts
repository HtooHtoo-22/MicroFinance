import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../service/user.service';

@Component({
  selector: 'app-user-count',
  standalone: false,
  templateUrl: './user-count.component.html',
  styleUrls: ['./user-count.component.css']
})
export class UserCountComponent implements OnInit {
  activeUserCount: number | null = null;
  loading: boolean = true;
  errorMessage: string | null = null;
  imagePath2: string = "image/user_account_profile-512.webp";

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.fetchActiveUserCount();
  }

  fetchActiveUserCount(): void {
    this.userService.getTotalActiveUserCount().subscribe({
      next: (count) => {
        this.activeUserCount = count;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = error;
        this.loading = false;
      }
    });
  }
}