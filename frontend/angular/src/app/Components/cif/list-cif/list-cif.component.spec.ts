import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListCifComponent } from './list-cif.component';

describe('ListCifComponent', () => {
  let component: ListCifComponent;
  let fixture: ComponentFixture<ListCifComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListCifComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListCifComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
