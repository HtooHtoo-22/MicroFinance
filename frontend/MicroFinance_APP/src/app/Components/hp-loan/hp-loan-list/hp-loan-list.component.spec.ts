import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HpLoanListComponent } from './hp-loan-list.component';

describe('HpLoanListComponent', () => {
  let component: HpLoanListComponent;
  let fixture: ComponentFixture<HpLoanListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HpLoanListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HpLoanListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
