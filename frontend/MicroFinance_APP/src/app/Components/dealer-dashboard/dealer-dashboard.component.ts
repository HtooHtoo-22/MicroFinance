import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dealer-dashboard',
  standalone: false,
  templateUrl: './dealer-dashboard.component.html',
  styleUrl: './dealer-dashboard.component.css'
})
export class DealerDashboardComponent {
  imagePath: string = "image/richcon-logo.png";
currentAccountId: any|string;

constructor(private router: Router) {}

navigateToProductList() {
  console.log('Navigating to product list');
  this.router.navigate(['/dealer-dashboard/product-list']).then(success => {
    console.log('Navigation success:', success);
  }).catch(err => {
    console.error('Navigation error:', err);
  });
}
}
