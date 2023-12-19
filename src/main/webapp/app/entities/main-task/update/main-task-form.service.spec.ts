import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../main-task.test-samples';

import { MainTaskFormService } from './main-task-form.service';

describe('MainTask Form Service', () => {
  let service: MainTaskFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MainTaskFormService);
  });

  describe('Service methods', () => {
    describe('createMainTaskFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMainTaskFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            description: expect.any(Object),
            deadline: expect.any(Object),
            creation: expect.any(Object),
            cost: expect.any(Object),
            status: expect.any(Object),
            category: expect.any(Object),
            personOwner: expect.any(Object),
          }),
        );
      });

      it('passing IMainTask should create a new form with FormGroup', () => {
        const formGroup = service.createMainTaskFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            description: expect.any(Object),
            deadline: expect.any(Object),
            creation: expect.any(Object),
            cost: expect.any(Object),
            status: expect.any(Object),
            category: expect.any(Object),
            personOwner: expect.any(Object),
          }),
        );
      });
    });

    describe('getMainTask', () => {
      it('should return NewMainTask for default MainTask initial value', () => {
        const formGroup = service.createMainTaskFormGroup(sampleWithNewData);

        const mainTask = service.getMainTask(formGroup) as any;

        expect(mainTask).toMatchObject(sampleWithNewData);
      });

      it('should return NewMainTask for empty MainTask initial value', () => {
        const formGroup = service.createMainTaskFormGroup();

        const mainTask = service.getMainTask(formGroup) as any;

        expect(mainTask).toMatchObject({});
      });

      it('should return IMainTask', () => {
        const formGroup = service.createMainTaskFormGroup(sampleWithRequiredData);

        const mainTask = service.getMainTask(formGroup) as any;

        expect(mainTask).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMainTask should not enable id FormControl', () => {
        const formGroup = service.createMainTaskFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMainTask should disable id FormControl', () => {
        const formGroup = service.createMainTaskFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
