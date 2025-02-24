import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SmeLoanListComponent } from './sme-loan-list.component';

describe('SmeLoanListComponent', () => {
  let component: SmeLoanListComponent;
  let fixture: ComponentFixture<SmeLoanListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SmeLoanListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SmeLoanListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
