import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CurrentAccRegisterComponent } from './current-acc-register.component';

describe('CurrentAccRegisterComponent', () => {
  let component: CurrentAccRegisterComponent;
  let fixture: ComponentFixture<CurrentAccRegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CurrentAccRegisterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CurrentAccRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
