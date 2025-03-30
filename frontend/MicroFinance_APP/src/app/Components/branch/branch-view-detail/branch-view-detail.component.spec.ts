import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BranchViewDetailComponent } from './branch-view-detail.component';

describe('BranchViewDetailComponent', () => {
  let component: BranchViewDetailComponent;
  let fixture: ComponentFixture<BranchViewDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BranchViewDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BranchViewDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
