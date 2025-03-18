import { TestBed } from '@angular/core/testing';

import { HpLoanService } from './hp-loan.service';

describe('HpLoanService', () => {
  let service: HpLoanService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HpLoanService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
