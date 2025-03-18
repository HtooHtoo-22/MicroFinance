import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CifListComponent } from './cif-list.component';

describe('CifListComponent', () => {
  let component: CifListComponent;
  let fixture: ComponentFixture<CifListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CifListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CifListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
