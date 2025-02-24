import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollateralTypeComponent } from './collateral-type.component';

describe('CollateralTypeComponent', () => {
  let component: CollateralTypeComponent;
  let fixture: ComponentFixture<CollateralTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CollateralTypeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CollateralTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
