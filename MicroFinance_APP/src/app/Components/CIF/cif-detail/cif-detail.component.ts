import { Component, OnInit } from '@angular/core';
import { CifService } from '../../../service/cif.service';
import { Cif } from '../../../model/CIF';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-cif-detail',
  standalone: false,
  templateUrl: './cif-detail.component.html',
  styleUrl: './cif-detail.component.css'
})
export class CifDetailComponent implements OnInit {
  cif: Cif | null = null;
  loading = true;
  errorMessage = '';

  constructor(
    private cifService: CifService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = +params['id'];
      if (id) {
        this.getCif(id);
      } else {
        this.errorMessage = 'Invalid CIF ID';
        this.loading = false;
      }
    });
  }

  getCif(id: number) {
    this.cifService.getCifById(id).subscribe({
      next: (response) => {
        this.cif = response;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching CIF:', error);
        this.errorMessage = 'Failed to load CIF details';
        this.loading = false;
      }
    });
  }
}