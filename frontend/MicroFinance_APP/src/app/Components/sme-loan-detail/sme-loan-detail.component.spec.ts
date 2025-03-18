import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SmeLoanDetailComponent } from './sme-loan-detail.component';

describe('SmeLoanDetailComponent', () => {
  let component: SmeLoanDetailComponent;
  let fixture: ComponentFixture<SmeLoanDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SmeLoanDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SmeLoanDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
