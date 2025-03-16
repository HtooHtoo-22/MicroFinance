import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SmeOdLatefeeComponent } from './sme-od-latefee.component';

describe('SmeOdLatefeeComponent', () => {
  let component: SmeOdLatefeeComponent;
  let fixture: ComponentFixture<SmeOdLatefeeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SmeOdLatefeeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SmeOdLatefeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
