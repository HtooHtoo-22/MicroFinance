import { Component, Input, OnInit } from '@angular/core';
import { CurrentAccService } from '../../../service/current-acc.service';
import { AuthService } from '../../../service/auth.service';

@Component({
  selector: 'app-branch-account-count',
  standalone: false,
  templateUrl: './branch-account-count.component.html',
  styleUrl: './branch-account-count.component.css'
})
export class BranchAccountCountComponent {}