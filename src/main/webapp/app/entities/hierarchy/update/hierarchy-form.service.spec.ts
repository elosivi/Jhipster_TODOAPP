import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../hierarchy.test-samples';

import { HierarchyFormService } from './hierarchy-form.service';

describe('Hierarchy Form Service', () => {
  let service: HierarchyFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HierarchyFormService);
  });

  describe('Service methods', () => {
    describe('createHierarchyFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createHierarchyFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });

      it('passing IHierarchy should create a new form with FormGroup', () => {
        const formGroup = service.createHierarchyFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });
    });

    describe('getHierarchy', () => {
      it('should return NewHierarchy for default Hierarchy initial value', () => {
        const formGroup = service.createHierarchyFormGroup(sampleWithNewData);

        const hierarchy = service.getHierarchy(formGroup) as any;

        expect(hierarchy).toMatchObject(sampleWithNewData);
      });

      it('should return NewHierarchy for empty Hierarchy initial value', () => {
        const formGroup = service.createHierarchyFormGroup();

        const hierarchy = service.getHierarchy(formGroup) as any;

        expect(hierarchy).toMatchObject({});
      });

      it('should return IHierarchy', () => {
        const formGroup = service.createHierarchyFormGroup(sampleWithRequiredData);

        const hierarchy = service.getHierarchy(formGroup) as any;

        expect(hierarchy).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IHierarchy should not enable id FormControl', () => {
        const formGroup = service.createHierarchyFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewHierarchy should disable id FormControl', () => {
        const formGroup = service.createHierarchyFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
