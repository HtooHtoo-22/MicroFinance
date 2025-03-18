import { TestBed } from '@angular/core/testing';

import { CurrentAccService } from './current-acc.service';

describe('CurrentAccService', () => {
  let service: CurrentAccService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CurrentAccService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
