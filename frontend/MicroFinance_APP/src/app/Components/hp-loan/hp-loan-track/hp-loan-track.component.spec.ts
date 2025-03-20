import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HpLoanTrackComponent } from './hp-loan-track.component';

describe('HpLoanTrackComponent', () => {
  let component: HpLoanTrackComponent;
  let fixture: ComponentFixture<HpLoanTrackComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HpLoanTrackComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HpLoanTrackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
