import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CurrentAccCountComponent } from './current-acc-count.component';

describe('CurrentAccCountComponent', () => {
  let component: CurrentAccCountComponent;
  let fixture: ComponentFixture<CurrentAccCountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CurrentAccCountComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CurrentAccCountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
