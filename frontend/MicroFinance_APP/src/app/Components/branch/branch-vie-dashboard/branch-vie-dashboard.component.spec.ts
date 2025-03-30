import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BranchVieDashboardComponent } from './branch-vie-dashboard.component';

describe('BranchVieDashboardComponent', () => {
  let component: BranchVieDashboardComponent;
  let fixture: ComponentFixture<BranchVieDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BranchVieDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BranchVieDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
