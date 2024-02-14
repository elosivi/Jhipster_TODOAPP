import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IRelEventPerson, NewRelEventPerson } from '../rel-event-person.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRelEventPerson for edit and NewRelEventPersonFormGroupInput for create.
 */
type RelEventPersonFormGroupInput = IRelEventPerson | PartialWithRequiredKeyOf<NewRelEventPerson>;

type RelEventPersonFormDefaults = Pick<NewRelEventPerson, 'id' | 'event' | 'person' | 'hierarchy'>;

type RelEventPersonFormGroupContent = {
  id: FormControl<IRelEventPerson['id'] | NewRelEventPerson['id']>;
  participation: FormControl<IRelEventPerson['participation']>;
  event: FormControl<IRelEventPerson['event']>;
  person: FormControl<IRelEventPerson['person']>;
  hierarchy: FormControl<IRelEventPerson['hierarchy']>;
};

export type RelEventPersonFormGroup = FormGroup<RelEventPersonFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RelEventPersonFormService {
  createRelEventPersonFormGroup(relEventPerson: RelEventPersonFormGroupInput = { id: null }): RelEventPersonFormGroup {
    const relEventPersonRawValue = {
      ...this.getFormDefaults(),
      ...relEventPerson,
    };
    return new FormGroup<RelEventPersonFormGroupContent>({
      id: new FormControl(
        { value: relEventPersonRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      participation: new FormControl(relEventPersonRawValue.participation),
      event: new FormControl(relEventPersonRawValue.event),
      person: new FormControl(relEventPersonRawValue.person),
      hierarchy: new FormControl(relEventPersonRawValue.hierarchy),
    });
  }

  getRelEventPerson(form: RelEventPersonFormGroup): IRelEventPerson | NewRelEventPerson {
    return form.getRawValue() as IRelEventPerson | NewRelEventPerson;
  }

  resetForm(form: RelEventPersonFormGroup, relEventPerson: RelEventPersonFormGroupInput): void {
    const relEventPersonRawValue = { ...this.getFormDefaults(), ...relEventPerson };
    form.reset(
      {
        ...relEventPersonRawValue,
        id: { value: relEventPersonRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): RelEventPersonFormDefaults {
    return {
      id: null,
      event: null,
      person: undefined,
      hierarchy: null,
    };
  }
}
