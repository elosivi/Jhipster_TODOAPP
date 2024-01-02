import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IHierarchy, NewHierarchy } from '../hierarchy.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IHierarchy for edit and NewHierarchyFormGroupInput for create.
 */
type HierarchyFormGroupInput = IHierarchy | PartialWithRequiredKeyOf<NewHierarchy>;

type HierarchyFormDefaults = Pick<NewHierarchy, 'id'>;

type HierarchyFormGroupContent = {
  id: FormControl<IHierarchy['id'] | NewHierarchy['id']>;
  description: FormControl<IHierarchy['description']>;
};

export type HierarchyFormGroup = FormGroup<HierarchyFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class HierarchyFormService {
  createHierarchyFormGroup(hierarchy: HierarchyFormGroupInput = { id: null }): HierarchyFormGroup {
    const hierarchyRawValue = {
      ...this.getFormDefaults(),
      ...hierarchy,
    };
    return new FormGroup<HierarchyFormGroupContent>({
      id: new FormControl(
        { value: hierarchyRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      description: new FormControl(hierarchyRawValue.description, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(50)],
      }),
    });
  }

  getHierarchy(form: HierarchyFormGroup): IHierarchy | NewHierarchy {
    return form.getRawValue() as IHierarchy | NewHierarchy;
  }

  resetForm(form: HierarchyFormGroup, hierarchy: HierarchyFormGroupInput): void {
    const hierarchyRawValue = { ...this.getFormDefaults(), ...hierarchy };
    form.reset(
      {
        ...hierarchyRawValue,
        id: { value: hierarchyRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): HierarchyFormDefaults {
    return {
      id: null,
    };
  }
}
