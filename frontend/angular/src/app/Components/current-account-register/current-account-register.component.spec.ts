import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CurrentAccountRegisterComponent } from './current-account-register.component';

describe('CurrentAccountRegisterComponent', () => {
  let component: CurrentAccountRegisterComponent;
  let fixture: ComponentFixture<CurrentAccountRegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CurrentAccountRegisterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CurrentAccountRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
