import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DealerTransactionComponent } from './dealer-transaction.component';

describe('DealerTransactionComponent', () => {
  let component: DealerTransactionComponent;
  let fixture: ComponentFixture<DealerTransactionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DealerTransactionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DealerTransactionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
