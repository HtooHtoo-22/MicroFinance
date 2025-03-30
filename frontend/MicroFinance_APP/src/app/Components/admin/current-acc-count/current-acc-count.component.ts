import { Component, OnInit } from '@angular/core';
import { CurrentAccService } from '../../../service/current-acc.service';

@Component({
  selector: 'app-current-acc-count',
  standalone: false,
  templateUrl: './current-acc-count.component.html',
  styleUrls: ['./current-acc-count.component.css']
})
export class CurrentAccCountComponent implements OnInit {
  activeAccountCount: number | null = null;
  loading: boolean = true;
  errorMessage: string | null = null;

  constructor(private currentAccService: CurrentAccService) {}

  ngOnInit(): void {
    this.fetchActiveAccountCount();
  }

  fetchActiveAccountCount(): void {
    this.currentAccService.getActiveAccountCount().subscribe({
      next: (count) => {
        this.activeAccountCount = count;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to fetch active account count.';
        this.loading = false;
      }
    });
  }
}
