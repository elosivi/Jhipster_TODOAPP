import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../status.test-samples';

import { StatusFormService } from './status-form.service';

describe('Status Form Service', () => {
  let service: StatusFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StatusFormService);
  });

  describe('Service methods', () => {
    describe('createStatusFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createStatusFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });

      it('passing IStatus should create a new form with FormGroup', () => {
        const formGroup = service.createStatusFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });
    });

    describe('getStatus', () => {
      it('should return NewStatus for default Status initial value', () => {
        const formGroup = service.createStatusFormGroup(sampleWithNewData);

        const status = service.getStatus(formGroup) as any;

        expect(status).toMatchObject(sampleWithNewData);
      });

      it('should return NewStatus for empty Status initial value', () => {
        const formGroup = service.createStatusFormGroup();

        const status = service.getStatus(formGroup) as any;

        expect(status).toMatchObject({});
      });

      it('should return IStatus', () => {
        const formGroup = service.createStatusFormGroup(sampleWithRequiredData);

        const status = service.getStatus(formGroup) as any;

        expect(status).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IStatus should not enable id FormControl', () => {
        const formGroup = service.createStatusFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewStatus should disable id FormControl', () => {
        const formGroup = service.createStatusFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
