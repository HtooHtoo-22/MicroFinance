import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-hp-loan-track',
  standalone: false,
  templateUrl: './hp-loan-track.component.html',
  styleUrl: './hp-loan-track.component.css'
})
export class HpLoanTrackComponent {
    @Input() loanId: number | undefined; // Accept loanId as input
}
