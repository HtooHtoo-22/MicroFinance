import { Component } from '@angular/core';

@Component({
  selector: 'app-dealer-dashboard',
  standalone: false,
  templateUrl: './dealer-dashboard.component.html',
  styleUrl: './dealer-dashboard.component.css'
})
export class DealerDashboardComponent {
  imagePath: string = "image/richcon-logo.png";
currentAccountId: any|string;


}
