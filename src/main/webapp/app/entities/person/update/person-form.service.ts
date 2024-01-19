import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPerson, NewPerson } from '../person.model';
import { IUser } from '../../../admin/user-management/user-management.model';
import { isPresent } from '../../../core/util/operators';
import { getUserIdentifier } from '../../user/user.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPerson for edit and NewPersonFormGroupInput for create.
 */
type PersonFormGroupInput = IPerson | PartialWithRequiredKeyOf<NewPerson>;

type PersonFormDefaults = Pick<NewPerson, 'id'>;

type PersonFormGroupContent = {
  id: FormControl<IPerson['id'] | NewPerson['id']>;
  description: FormControl<IPerson['description']>;
  pseudo: FormControl<IPerson['pseudo']>;
  name: FormControl<IPerson['name']>;
  user: FormControl<IPerson['user']>;
};

export type PersonFormGroup = FormGroup<PersonFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PersonFormService {
  createPersonFormGroup(person: PersonFormGroupInput = { id: null }): PersonFormGroup {
    const personRawValue = {
      ...this.getFormDefaults(),
      ...person,
    };
    return new FormGroup<PersonFormGroupContent>({
      id: new FormControl(
        { value: personRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      description: new FormControl(personRawValue.description, {
        validators: [Validators.minLength(3), Validators.maxLength(300)],
      }),
      pseudo: new FormControl(personRawValue.pseudo, {
        validators: [Validators.minLength(3), Validators.maxLength(50)],
      }),
      name: new FormControl(personRawValue.name, {
        validators: [Validators.minLength(3), Validators.maxLength(250)],
      }),
      user: new FormControl(personRawValue.user),
    });
  }

  getPerson(form: PersonFormGroup): IPerson | NewPerson {
    return form.getRawValue() as IPerson | NewPerson;
  }

  resetForm(form: PersonFormGroup, person: PersonFormGroupInput): void {
    const personRawValue = { ...this.getFormDefaults(), ...person };
    form.reset(
      {
        ...personRawValue,
        id: { value: personRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PersonFormDefaults {
    return {
      id: null,
    };
  }
}
