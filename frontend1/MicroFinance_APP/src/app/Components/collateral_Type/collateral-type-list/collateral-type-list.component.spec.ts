import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollateralTypeListComponent } from './collateral-type-list.component';

describe('CollateralTypeListComponent', () => {
  let component: CollateralTypeListComponent;
  let fixture: ComponentFixture<CollateralTypeListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CollateralTypeListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CollateralTypeListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
