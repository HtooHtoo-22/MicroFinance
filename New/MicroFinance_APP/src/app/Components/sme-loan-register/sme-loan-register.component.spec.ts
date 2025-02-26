import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SmeLoanRegisterComponent } from './sme-loan-register.component';

describe('SmeLoanRegisterComponent', () => {
  let component: SmeLoanRegisterComponent;
  let fixture: ComponentFixture<SmeLoanRegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SmeLoanRegisterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SmeLoanRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
