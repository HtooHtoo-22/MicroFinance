import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SmeLaonHistoryComponent } from './sme-laon-history.component';

describe('SmeLaonHistoryComponent', () => {
  let component: SmeLaonHistoryComponent;
  let fixture: ComponentFixture<SmeLaonHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SmeLaonHistoryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SmeLaonHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
