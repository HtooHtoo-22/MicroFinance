import { TestBed } from '@angular/core/testing';

import { HpLoanSchduleService } from './hp-loan-schdule.service';

describe('HpLoanSchduleService', () => {
  let service: HpLoanSchduleService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HpLoanSchduleService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
