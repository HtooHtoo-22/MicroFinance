import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoanMetricsComponent } from './loan-metrics.component';

describe('LoanMetricsComponent', () => {
  let component: LoanMetricsComponent;
  let fixture: ComponentFixture<LoanMetricsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoanMetricsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoanMetricsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
