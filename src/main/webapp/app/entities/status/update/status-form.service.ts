import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IStatus, NewStatus } from '../status.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStatus for edit and NewStatusFormGroupInput for create.
 */
type StatusFormGroupInput = IStatus | PartialWithRequiredKeyOf<NewStatus>;

type StatusFormDefaults = Pick<NewStatus, 'id'>;

type StatusFormGroupContent = {
  id: FormControl<IStatus['id'] | NewStatus['id']>;
  description: FormControl<IStatus['description']>;
};

export type StatusFormGroup = FormGroup<StatusFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StatusFormService {
  createStatusFormGroup(status: StatusFormGroupInput = { id: null }): StatusFormGroup {
    const statusRawValue = {
      ...this.getFormDefaults(),
      ...status,
    };
    return new FormGroup<StatusFormGroupContent>({
      id: new FormControl(
        { value: statusRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      description: new FormControl(statusRawValue.description, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(100)],
      }),
    });
  }

  getStatus(form: StatusFormGroup): IStatus | NewStatus {
    return form.getRawValue() as IStatus | NewStatus;
  }

  resetForm(form: StatusFormGroup, status: StatusFormGroupInput): void {
    const statusRawValue = { ...this.getFormDefaults(), ...status };
    form.reset(
      {
        ...statusRawValue,
        id: { value: statusRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): StatusFormDefaults {
    return {
      id: null,
    };
  }
}
