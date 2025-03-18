import { TestBed } from '@angular/core/testing';

import { WebsocketsubjectService } from './websocketsubject.service';

describe('WebsocketsubjectService', () => {
  let service: WebsocketsubjectService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WebsocketsubjectService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
