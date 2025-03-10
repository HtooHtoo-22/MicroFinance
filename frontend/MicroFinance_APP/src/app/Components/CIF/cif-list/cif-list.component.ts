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

  constructor(private cifService: CifService) {}

  ngOnInit(): void {
    this.getCifs();
  }

  getCifs() {
    this.cifService.listCif().subscribe({
      next: (response) => {
        if (response && response.data) {
          this.cifs = response.data;
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
