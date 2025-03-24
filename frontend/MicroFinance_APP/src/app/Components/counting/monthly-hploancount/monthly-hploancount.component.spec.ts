import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MonthlyHploancountComponent } from './monthly-hploancount.component';

describe('MonthlyHploancountComponent', () => {
  let component: MonthlyHploancountComponent;
  let fixture: ComponentFixture<MonthlyHploancountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MonthlyHploancountComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MonthlyHploancountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
