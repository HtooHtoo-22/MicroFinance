import { TestBed } from '@angular/core/testing';

import { CollateralService } from './collateral.service';

describe('CollateralService', () => {
  let service: CollateralService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CollateralService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
