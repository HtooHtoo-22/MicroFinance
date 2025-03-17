import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CifDetailComponent } from './cif-detail.component';

describe('CifDetailComponent', () => {
  let component: CifDetailComponent;
  let fixture: ComponentFixture<CifDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CifDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CifDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
