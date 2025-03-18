import { Component, OnInit } from '@angular/core';
import { CifService } from '../../../service/cif.service';
import { Cif } from '../../../model/CIF';

@Component({
  selector: 'app-cif-list',
  standalone: false,
  templateUrl: './cif-list.component.html',
  styleUrl: './cif-list.component.css'
})
export class CifListComponent implements OnInit {
  cifs: Cif[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 7;
  totalUsers: number = 0;
  totalPages: number = 0;

  constructor(private cifService: CifService) {}

  ngOnInit(): void {
    this.getCifs();
  }

  getCifs() {
    this.cifService.listCif().subscribe({
      next: (response) => {
        if (response && response.data) {
          // Sort CIFs with missing accounts first
          this.cifs = response.data.sort((a, b) => {
            // Show CIFs without accounts first
            if (!a.hasCurrentAccount && b.hasCurrentAccount) return -1;
            if (a.hasCurrentAccount && !b.hasCurrentAccount) return 1;
            return 0;
          });
          
          this.totalUsers = this.cifs.length;
          this.totalPages = Math.ceil(this.totalUsers / this.itemsPerPage);
        } else {
          this.cifs = [];
          this.totalUsers = 0;
          this.totalPages = 0;
        }
      },
      error: (error) => {
        console.error('Error fetching CIFs:', error);
      }
    });
  }


  get paginatedUsers(): Cif[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    return this.cifs.slice(start, start + this.itemsPerPage);
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