import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthModuleComponent } from './auth-module.component';

describe('AuthModuleComponent', () => {
  let component: AuthModuleComponent;
  let fixture: ComponentFixture<AuthModuleComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AuthModuleComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AuthModuleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
