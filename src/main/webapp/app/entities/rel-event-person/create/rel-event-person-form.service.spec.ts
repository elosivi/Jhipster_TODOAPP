import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../rel-event-person.test-samples';

import { RelEventPersonFormService } from './rel-event-person-form.service';

describe('RelEventPerson Form Service', () => {
  let service: RelEventPersonFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RelEventPersonFormService);
  });

  describe('Service methods', () => {
    describe('createRelEventPersonFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRelEventPersonFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            participation: expect.any(Object),
            events: expect.any(Object),
            people: expect.any(Object),
            hierarchies: expect.any(Object),
          }),
        );
      });

      it('passing IRelEventPerson should create a new form with FormGroup', () => {
        const formGroup = service.createRelEventPersonFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            participation: expect.any(Object),
            events: expect.any(Object),
            people: expect.any(Object),
            hierarchies: expect.any(Object),
          }),
        );
      });
    });

    describe('getRelEventPerson', () => {
      it('should return NewRelEventPerson for default RelEventPerson initial value', () => {
        const formGroup = service.createRelEventPersonFormGroup(sampleWithNewData);

        const relEventPerson = service.getRelEventPerson(formGroup) as any;

        expect(relEventPerson).toMatchObject(sampleWithNewData);
      });

      it('should return NewRelEventPerson for empty RelEventPerson initial value', () => {
        const formGroup = service.createRelEventPersonFormGroup();

        const relEventPerson = service.getRelEventPerson(formGroup) as any;

        expect(relEventPerson).toMatchObject({});
      });

      it('should return IRelEventPerson', () => {
        const formGroup = service.createRelEventPersonFormGroup(sampleWithRequiredData);

        const relEventPerson = service.getRelEventPerson(formGroup) as any;

        expect(relEventPerson).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRelEventPerson should not enable id FormControl', () => {
        const formGroup = service.createRelEventPersonFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRelEventPerson should disable id FormControl', () => {
        const formGroup = service.createRelEventPersonFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
