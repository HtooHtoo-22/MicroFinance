import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BranchUserCountComponent } from './branch-user-count.component';

describe('BranchUserCountComponent', () => {
  let component: BranchUserCountComponent;
  let fixture: ComponentFixture<BranchUserCountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BranchUserCountComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BranchUserCountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
