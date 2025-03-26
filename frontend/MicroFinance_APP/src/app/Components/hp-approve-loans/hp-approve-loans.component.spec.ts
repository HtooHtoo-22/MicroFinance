import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HpApproveLoansComponent } from './hp-approve-loans.component';

describe('HpApproveLoansComponent', () => {
  let component: HpApproveLoansComponent;
  let fixture: ComponentFixture<HpApproveLoansComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HpApproveLoansComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HpApproveLoansComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
