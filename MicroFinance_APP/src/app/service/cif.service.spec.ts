import { TestBed } from '@angular/core/testing';

import { CifService } from './cif.service';

describe('CifService', () => {
  let service: CifService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CifService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
