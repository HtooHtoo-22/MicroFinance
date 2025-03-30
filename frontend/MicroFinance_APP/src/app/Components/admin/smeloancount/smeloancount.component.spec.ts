import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SmeloancountComponent } from './smeloancount.component';

describe('SmeloancountComponent', () => {
  let component: SmeloancountComponent;
  let fixture: ComponentFixture<SmeloancountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SmeloancountComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SmeloancountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
