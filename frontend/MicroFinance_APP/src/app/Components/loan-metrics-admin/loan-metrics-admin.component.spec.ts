import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoanMetricsAdminComponent } from './loan-metrics-admin.component';

describe('LoanMetricsAdminComponent', () => {
  let component: LoanMetricsAdminComponent;
  let fixture: ComponentFixture<LoanMetricsAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoanMetricsAdminComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoanMetricsAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
