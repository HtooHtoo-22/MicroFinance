import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HpLoanDetailComponent } from './hp-loan-detail.component';

describe('HpLoanDetailComponent', () => {
  let component: HpLoanDetailComponent;
  let fixture: ComponentFixture<HpLoanDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HpLoanDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HpLoanDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
