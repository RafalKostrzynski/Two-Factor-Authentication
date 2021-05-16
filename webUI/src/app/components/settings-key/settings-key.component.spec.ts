import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingsKeyComponent } from './settings-key.component';

describe('SettingsKeyComponent', () => {
  let component: SettingsKeyComponent;
  let fixture: ComponentFixture<SettingsKeyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SettingsKeyComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SettingsKeyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
