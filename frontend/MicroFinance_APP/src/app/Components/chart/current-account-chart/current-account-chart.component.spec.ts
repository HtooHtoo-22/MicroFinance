import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CurrentAccountChartComponent } from './current-account-chart.component';

describe('CurrentAccountChartComponent', () => {
  let component: CurrentAccountChartComponent;
  let fixture: ComponentFixture<CurrentAccountChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CurrentAccountChartComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CurrentAccountChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
