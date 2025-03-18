import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollateralDetailComponent } from './collateral-detail.component';

describe('CollateralDetailComponent', () => {
  let component: CollateralDetailComponent;
  let fixture: ComponentFixture<CollateralDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CollateralDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CollateralDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
