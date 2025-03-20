import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HpLoanLatefeeComponent } from './hp-loan-latefee.component';

describe('HpLoanLatefeeComponent', () => {
  let component: HpLoanLatefeeComponent;
  let fixture: ComponentFixture<HpLoanLatefeeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HpLoanLatefeeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HpLoanLatefeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
