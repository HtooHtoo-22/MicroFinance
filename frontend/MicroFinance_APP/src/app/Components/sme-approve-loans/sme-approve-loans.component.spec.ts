import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SmeApproveLoansComponent } from './sme-approve-loans.component';

describe('SmeApproveLoansComponent', () => {
  let component: SmeApproveLoansComponent;
  let fixture: ComponentFixture<SmeApproveLoansComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SmeApproveLoansComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SmeApproveLoansComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
