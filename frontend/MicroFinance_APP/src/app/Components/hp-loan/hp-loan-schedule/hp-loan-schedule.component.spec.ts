import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HpLoanScheduleComponent } from './hp-loan-schedule.component';

describe('HpLoanScheduleComponent', () => {
  let component: HpLoanScheduleComponent;
  let fixture: ComponentFixture<HpLoanScheduleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HpLoanScheduleComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HpLoanScheduleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
