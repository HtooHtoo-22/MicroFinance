import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllproductListComponent } from './allproduct-list.component';

describe('AllproductListComponent', () => {
  let component: AllproductListComponent;
  let fixture: ComponentFixture<AllproductListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AllproductListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllproductListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
