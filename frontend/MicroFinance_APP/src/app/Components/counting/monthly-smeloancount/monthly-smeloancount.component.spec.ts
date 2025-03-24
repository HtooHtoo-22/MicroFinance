import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MonthlySMELoancountComponent } from './monthly-smeloancount.component';

describe('MonthlySMELoancountComponent', () => {
  let component: MonthlySMELoancountComponent;
  let fixture: ComponentFixture<MonthlySMELoancountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MonthlySMELoancountComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MonthlySMELoancountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
