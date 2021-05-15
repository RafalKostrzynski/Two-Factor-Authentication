import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SecondFactorComponent } from './second-factor.component';

describe('SecondFactorComponent', () => {
  let component: SecondFactorComponent;
  let fixture: ComponentFixture<SecondFactorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SecondFactorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SecondFactorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
