import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HpLoanFormComponent } from './hp-loan-form.component';

describe('HpLoanFormComponent', () => {
  let component: HpLoanFormComponent;
  let fixture: ComponentFixture<HpLoanFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HpLoanFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HpLoanFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
