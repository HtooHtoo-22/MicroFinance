import { Component, OnInit } from '@angular/core';
import { DealerService } from '../../../service/dealer.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Dealer } from '../../../model/Dealer';
import { CifService } from '../../../service/cif.service';

@Component({
  selector: 'app-dealer-detailview',
  standalone: false,
  templateUrl: './dealer-detailview.component.html',
  styleUrl: './dealer-detailview.component.css'
})
export class DealerDetailviewComponent implements OnInit {
  dealer?: Dealer;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dealerService: DealerService,
    private cifService: CifService
  ) { }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.dealerService.getDealerById(+id).subscribe({
        next: (dealer) => {
          this.dealer = dealer;
          this.loadUserPhoto(dealer.id);
        },
        error: () => this.router.navigate(['/manager-dashboard/dealer-detail'])
      });
    }
  }

  loadUserPhoto(dealerId: number) {
    this.cifService.getCifById(dealerId).subscribe({
      next: (cif) => {
        if (this.dealer) {
          this.dealer.userPhotoURL = cif.userPhotoURL;
        }
      },
      error: () => {
        console.error(`Error loading photo for dealer ID ${dealerId}`);
      }
    });
  }

  goBack() {
    this.router.navigate(['/manager-dashboard/dealer-detail']);
  }
}