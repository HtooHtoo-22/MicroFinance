import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HpLoanListFinishedComponent } from './hp-loan-list-finished.component';

describe('HpLoanListFinishedComponent', () => {
  let component: HpLoanListFinishedComponent;
  let fixture: ComponentFixture<HpLoanListFinishedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HpLoanListFinishedComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HpLoanListFinishedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
