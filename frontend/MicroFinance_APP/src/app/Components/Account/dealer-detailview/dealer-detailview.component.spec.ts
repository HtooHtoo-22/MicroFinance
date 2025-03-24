import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DealerDetailviewComponent } from './dealer-detailview.component';

describe('DealerDetailviewComponent', () => {
  let component: DealerDetailviewComponent;
  let fixture: ComponentFixture<DealerDetailviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DealerDetailviewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DealerDetailviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
