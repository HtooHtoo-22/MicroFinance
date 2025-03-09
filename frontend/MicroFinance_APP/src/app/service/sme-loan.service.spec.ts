import { TestBed } from '@angular/core/testing';

import { SmeLoanService } from './sme-loan.service';

describe('SmeLoanService', () => {
  let service: SmeLoanService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SmeLoanService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
