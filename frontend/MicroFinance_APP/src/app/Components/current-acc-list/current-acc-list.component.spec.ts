import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CurrentAccListComponent } from './current-acc-list.component';

describe('CurrentAccListComponent', () => {
  let component: CurrentAccListComponent;
  let fixture: ComponentFixture<CurrentAccListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CurrentAccListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CurrentAccListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
