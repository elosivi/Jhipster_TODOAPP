import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IMainTask, NewMainTask } from '../main-task.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMainTask for edit and NewMainTaskFormGroupInput for create.
 */
type MainTaskFormGroupInput = IMainTask | PartialWithRequiredKeyOf<NewMainTask>;

type MainTaskFormDefaults = Pick<NewMainTask, 'id'>;

type MainTaskFormGroupContent = {
  id: FormControl<IMainTask['id'] | NewMainTask['id']>;
  description: FormControl<IMainTask['description']>;
  deadline: FormControl<IMainTask['deadline']>;
  creation: FormControl<IMainTask['creation']>;
  cost: FormControl<IMainTask['cost']>;
  status: FormControl<IMainTask['status']>;
  category: FormControl<IMainTask['category']>;
  personOwner: FormControl<IMainTask['personOwner']>;
};

export type MainTaskFormGroup = FormGroup<MainTaskFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MainTaskFormService {
  createMainTaskFormGroup(mainTask: MainTaskFormGroupInput = { id: null }): MainTaskFormGroup {
    const mainTaskRawValue = {
      ...this.getFormDefaults(),
      ...mainTask,
    };
    return new FormGroup<MainTaskFormGroupContent>({
      id: new FormControl(
        { value: mainTaskRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      description: new FormControl(mainTaskRawValue.description, {
        validators: [Validators.minLength(3), Validators.maxLength(100)],
      }),
      deadline: new FormControl(mainTaskRawValue.deadline, {
        validators: [Validators.required],
      }),
      creation: new FormControl(mainTaskRawValue.creation),
      cost: new FormControl(mainTaskRawValue.cost),
      status: new FormControl(mainTaskRawValue.status),
      category: new FormControl(mainTaskRawValue.category),
      personOwner: new FormControl(mainTaskRawValue.personOwner),
    });
  }

  getMainTask(form: MainTaskFormGroup): IMainTask | NewMainTask {
    return form.getRawValue() as IMainTask | NewMainTask;
  }

  resetForm(form: MainTaskFormGroup, mainTask: MainTaskFormGroupInput): void {
    const mainTaskRawValue = { ...this.getFormDefaults(), ...mainTask };
    form.reset(
      {
        ...mainTaskRawValue,
        id: { value: mainTaskRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MainTaskFormDefaults {
    return {
      id: null,
    };
  }
}
