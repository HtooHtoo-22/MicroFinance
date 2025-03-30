import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HploancountComponent } from './hploancount.component';

describe('HploancountComponent', () => {
  let component: HploancountComponent;
  let fixture: ComponentFixture<HploancountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HploancountComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HploancountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
