import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SmeRepayTrackComponent } from './sme-repay-track.component';

describe('SmeRepayTrackComponent', () => {
  let component: SmeRepayTrackComponent;
  let fixture: ComponentFixture<SmeRepayTrackComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SmeRepayTrackComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SmeRepayTrackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
