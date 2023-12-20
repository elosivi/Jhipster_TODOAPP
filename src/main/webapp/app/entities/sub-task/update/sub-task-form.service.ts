import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISubTask, NewSubTask } from '../sub-task.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISubTask for edit and NewSubTaskFormGroupInput for create.
 */
type SubTaskFormGroupInput = ISubTask | PartialWithRequiredKeyOf<NewSubTask>;

type SubTaskFormDefaults = Pick<NewSubTask, 'id'>;

type SubTaskFormGroupContent = {
  id: FormControl<ISubTask['id'] | NewSubTask['id']>;
  description: FormControl<ISubTask['description']>;
  deadline: FormControl<ISubTask['deadline']>;
  creation: FormControl<ISubTask['creation']>;
  cost: FormControl<ISubTask['cost']>;
  mainTask: FormControl<ISubTask['mainTask']>;
  personDoer: FormControl<ISubTask['personDoer']>;
  status: FormControl<ISubTask['status']>;
};

export type SubTaskFormGroup = FormGroup<SubTaskFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SubTaskFormService {
  createSubTaskFormGroup(subTask: SubTaskFormGroupInput = { id: null }): SubTaskFormGroup {
    const subTaskRawValue = {
      ...this.getFormDefaults(),
      ...subTask,
    };
    return new FormGroup<SubTaskFormGroupContent>({
      id: new FormControl(
        { value: subTaskRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      description: new FormControl(subTaskRawValue.description, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(300)],
      }),
      deadline: new FormControl(subTaskRawValue.deadline, {
        validators: [Validators.required],
      }),
      creation: new FormControl(subTaskRawValue.creation),
      cost: new FormControl(subTaskRawValue.cost),
      mainTask: new FormControl(subTaskRawValue.mainTask),
      personDoer: new FormControl(subTaskRawValue.personDoer),
      status: new FormControl(subTaskRawValue.status),
    });
  }

  getSubTask(form: SubTaskFormGroup): ISubTask | NewSubTask {
    return form.getRawValue() as ISubTask | NewSubTask;
  }

  resetForm(form: SubTaskFormGroup, subTask: SubTaskFormGroupInput): void {
    const subTaskRawValue = { ...this.getFormDefaults(), ...subTask };
    form.reset(
      {
        ...subTaskRawValue,
        id: { value: subTaskRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SubTaskFormDefaults {
    return {
      id: null,
    };
  }
}
