import { Component, OnInit } from '@angular/core';
import { CifService } from '../../../services/cif.service';
import { Cif } from '../../../models/cif';

@Component({
  selector: 'app-list-cif',
  standalone: false,
  templateUrl: './list-cif.component.html',
  styleUrls: ['./list-cif.component.css']
})
export class ListCifComponent implements OnInit {
  cifs: Cif[] = [];

  constructor(private cifService: CifService) {}

  ngOnInit(): void {
    this.getCifs();
  }

  getCifs() {
    this.cifService.listCif().subscribe({
      next: (response) => {
        if (response && response.data) {
          this.cifs = response.data; // ✅ Extracting the array from response
        } else {
          this.cifs = [];
        }
      },
      error: (error) => {
        console.error('Error fetching CIFs:', error);
      }
    });
  }
}