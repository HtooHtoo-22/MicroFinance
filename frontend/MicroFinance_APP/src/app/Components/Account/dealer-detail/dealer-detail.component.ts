// dealer-detail.component.ts
import { Component, OnInit } from '@angular/core';
import { DealerService } from '../../../service/dealer.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Dealer } from '../../../model/Dealer';
import { CifService } from '../../../service/cif.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-dealer-detail',
  standalone: false,
  templateUrl: './dealer-detail.component.html',
  styleUrl: './dealer-detail.component.css'
})
export class DealerDetailComponent implements OnInit {
  dealers: Dealer[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 10;
  totalDealers: number = 0;
  totalPages: number = 0;
  loading: boolean = false;
  errorMessage: string | null = null;

  constructor(
    private dealerService: DealerService,
    private cifService: CifService,
    private snackBar: MatSnackBar,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit() {
    this.loadApprovedDealers();
  }

  loadApprovedDealers() {
    this.loading = true;
    this.errorMessage = null;

    this.dealerService.getApprovedDealers().subscribe({
      next: (dealers) => {
        this.dealers = dealers;
        this.totalDealers = dealers.length;
        this.totalPages = Math.ceil(this.totalDealers / this.itemsPerPage);
        this.loadUserPhotos();
      },
      error: (err) => {
        this.errorMessage = 'Error loading approved dealers. Please try again later.';
        this.snackBar.open(this.errorMessage, 'Close', { duration: 3000 });
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  loadUserPhotos() {
    this.dealers.forEach(dealer => {
      this.cifService.getCifById(dealer.id).subscribe({
        next: (cif) => {
          dealer.userPhotoURL = cif.userPhotoURL;
        },
        error: () => {
          this.snackBar.open(`Error loading photo for dealer ${dealer.businessName}`, 'Close', { duration: 3000 });
        }
      });
    });
  }
  viewDetails(dealerId: number) {
    this.router.navigate(['/manager-dashboard/dealer-detail', dealerId], { 
      relativeTo: this.route.parent 
    });
  }

  // Pagination methods
  get paginatedDealers(): Dealer[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.dealers.slice(start, start + this.itemsPerPage);
  }

  get startIndex(): number {
    return (this.currentPage - 1) * this.itemsPerPage + 1;
  }

  get endIndex(): number {
    return Math.min(this.currentPage * this.itemsPerPage, this.totalDealers);
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
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