import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CifRegisterComponent } from './cif-register.component';

describe('CifRegisterComponent', () => {
  let component: CifRegisterComponent;
  let fixture: ComponentFixture<CifRegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CifRegisterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CifRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
