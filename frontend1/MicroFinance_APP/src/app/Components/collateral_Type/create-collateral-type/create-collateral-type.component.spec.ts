import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateCollateralTypeComponent } from './create-collateral-type.component';

describe('CreateCollateralTypeComponent', () => {
  let component: CreateCollateralTypeComponent;
  let fixture: ComponentFixture<CreateCollateralTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateCollateralTypeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateCollateralTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
