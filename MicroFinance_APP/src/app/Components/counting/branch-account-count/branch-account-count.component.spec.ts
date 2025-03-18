import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BranchAccountCountComponent } from './branch-account-count.component';

describe('BranchAccountCountComponent', () => {
  let component: BranchAccountCountComponent;
  let fixture: ComponentFixture<BranchAccountCountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BranchAccountCountComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BranchAccountCountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
