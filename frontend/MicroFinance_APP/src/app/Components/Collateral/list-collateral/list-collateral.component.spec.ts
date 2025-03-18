import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListCollateralComponent } from './list-collateral.component';

describe('ListCollateralComponent', () => {
  let component: ListCollateralComponent;
  let fixture: ComponentFixture<ListCollateralComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListCollateralComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListCollateralComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
