import { TestBed } from '@angular/core/testing';

import { LoanDashboardServiceService } from './loan-dashboard-service.service';

describe('LoanDashboardServiceService', () => {
  let service: LoanDashboardServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoanDashboardServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
