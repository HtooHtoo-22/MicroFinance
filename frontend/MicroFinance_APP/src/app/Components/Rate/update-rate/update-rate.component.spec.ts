import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateRateComponent } from './update-rate.component';

describe('UpdateRateComponent', () => {
  let component: UpdateRateComponent;
  let fixture: ComponentFixture<UpdateRateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UpdateRateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpdateRateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
