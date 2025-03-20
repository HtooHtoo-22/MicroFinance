import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-hp-loan-latefee',
  standalone: false,
  templateUrl: './hp-loan-latefee.component.html',
  styleUrl: './hp-loan-latefee.component.css'
})
export class HpLoanLatefeeComponent {
     @Input() loanId: number | undefined; // Accept loanId as input
}
