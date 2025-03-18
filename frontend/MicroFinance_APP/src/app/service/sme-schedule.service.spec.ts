import { TestBed } from '@angular/core/testing';

import { SmeScheduleService } from './sme-schedule.service';

describe('SmeScheduleService', () => {
  let service: SmeScheduleService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SmeScheduleService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
