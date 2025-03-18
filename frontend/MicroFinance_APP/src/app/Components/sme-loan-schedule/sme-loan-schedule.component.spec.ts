import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SmeLoanScheduleComponent } from './sme-loan-schedule.component';

describe('SmeLoanScheduleComponent', () => {
  let component: SmeLoanScheduleComponent;
  let fixture: ComponentFixture<SmeLoanScheduleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SmeLoanScheduleComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SmeLoanScheduleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
