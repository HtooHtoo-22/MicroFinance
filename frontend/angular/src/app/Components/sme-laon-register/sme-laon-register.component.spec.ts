import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SmeLaonRegisterComponent } from './sme-laon-register.component';

describe('SmeLaonRegisterComponent', () => {
  let component: SmeLaonRegisterComponent;
  let fixture: ComponentFixture<SmeLaonRegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SmeLaonRegisterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SmeLaonRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
