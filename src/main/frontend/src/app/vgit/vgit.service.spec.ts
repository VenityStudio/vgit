import { TestBed } from '@angular/core/testing';

import { VgitService } from './vgit.service';

describe('VgitService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: VgitService = TestBed.get(VgitService);
    expect(service).toBeTruthy();
  });
});
