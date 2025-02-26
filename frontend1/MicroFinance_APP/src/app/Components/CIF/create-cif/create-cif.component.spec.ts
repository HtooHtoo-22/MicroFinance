import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateCifComponent } from './create-cif.component';

describe('CreateCifComponent', () => {
  let component: CreateCifComponent;
  let fixture: ComponentFixture<CreateCifComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateCifComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateCifComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
