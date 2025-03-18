import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollateralTypeUpdateComponent } from './collateral-type-update.component';

describe('CollateralTypeUpdateComponent', () => {
  let component: CollateralTypeUpdateComponent;
  let fixture: ComponentFixture<CollateralTypeUpdateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CollateralTypeUpdateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CollateralTypeUpdateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
