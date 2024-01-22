import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../sub-task.test-samples';

import { SubTaskFormService } from './sub-task-form.service';

describe('SubTask Form Service', () => {
  let service: SubTaskFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SubTaskFormService);
  });

  describe('Service methods', () => {
    describe('createSubTaskFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSubTaskFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            description: expect.any(Object),
            deadline: expect.any(Object),
            creation: expect.any(Object),
            cost: expect.any(Object),
            mainTask: expect.any(Object),
            personDoer: expect.any(Object),
            status: expect.any(Object),
          }),
        );
      });

      it('passing ISubTask should create a new form with FormGroup', () => {
        const formGroup = service.createSubTaskFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            description: expect.any(Object),
            deadline: expect.any(Object),
            creation: expect.any(Object),
            cost: expect.any(Object),
            mainTask: expect.any(Object),
            personDoer: expect.any(Object),
            status: expect.any(Object),
          }),
        );
      });
    });

    describe('getSubTask', () => {
      it('should return NewSubTask for default SubTask initial value', () => {
        const formGroup = service.createSubTaskFormGroup(sampleWithNewData);

        const subTask = service.getSubTask(formGroup) as any;

        expect(subTask).toMatchObject(sampleWithNewData);
      });

      it('should return NewSubTask for empty SubTask initial value', () => {
        const formGroup = service.createSubTaskFormGroup();

        const subTask = service.getSubTask(formGroup) as any;

        expect(subTask).toMatchObject({});
      });

      it('should return ISubTask', () => {
        const formGroup = service.createSubTaskFormGroup(sampleWithRequiredData);

        const subTask = service.getSubTask(formGroup) as any;

        expect(subTask).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISubTask should not enable id FormControl', () => {
        const formGroup = service.createSubTaskFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSubTask should disable id FormControl', () => {
        const formGroup = service.createSubTaskFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
