import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IEvent, NewEvent } from '../event.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEvent for edit and NewEventFormGroupInput for create.
 */
type EventFormGroupInput = IEvent | PartialWithRequiredKeyOf<NewEvent>;

type EventFormDefaults = Pick<NewEvent, 'id' | 'people'>;

type EventFormGroupContent = {
  id: FormControl<IEvent['id'] | NewEvent['id']>;
  label: FormControl<IEvent['label']>;
  description: FormControl<IEvent['description']>;
  theme: FormControl<IEvent['theme']>;
  dateStart: FormControl<IEvent['dateStart']>;
  dateEnd: FormControl<IEvent['dateEnd']>;
  place: FormControl<IEvent['place']>;
  placeDetails: FormControl<IEvent['placeDetails']>;
  adress: FormControl<IEvent['adress']>;
  note: FormControl<IEvent['note']>;
  eventType: FormControl<IEvent['eventType']>;
  people: FormControl<IEvent['people']>;
};

export type EventFormGroup = FormGroup<EventFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EventFormService {
  createEventFormGroup(event: EventFormGroupInput = { id: null }): EventFormGroup {
    const eventRawValue = {
      ...this.getFormDefaults(),
      ...event,
    };
    return new FormGroup<EventFormGroupContent>({
      id: new FormControl(
        { value: eventRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      label: new FormControl(eventRawValue.label, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(50)],
      }),
      description: new FormControl(eventRawValue.description, {
        validators: [Validators.minLength(3), Validators.maxLength(300)],
      }),
      theme: new FormControl(eventRawValue.theme, {
        validators: [Validators.minLength(3), Validators.maxLength(300)],
      }),
      dateStart: new FormControl(eventRawValue.dateStart, {
        validators: [Validators.required],
      }),
      dateEnd: new FormControl(eventRawValue.dateEnd, {
        validators: [Validators.required],
      }),
      place: new FormControl(eventRawValue.place, {
        validators: [Validators.required],
      }),
      placeDetails: new FormControl(eventRawValue.placeDetails),
      adress: new FormControl(eventRawValue.adress),
      note: new FormControl(eventRawValue.note, {
        validators: [Validators.minLength(3), Validators.maxLength(300)],
      }),
      eventType: new FormControl(eventRawValue.eventType),
      people: new FormControl(eventRawValue.people ?? []),
    });
  }

  getEvent(form: EventFormGroup): IEvent | NewEvent {
    return form.getRawValue() as IEvent | NewEvent;
  }

  resetForm(form: EventFormGroup, event: EventFormGroupInput): void {
    const eventRawValue = { ...this.getFormDefaults(), ...event };
    form.reset(
      {
        ...eventRawValue,
        id: { value: eventRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): EventFormDefaults {
    return {
      id: null,
      people: [],
    };
  }
}
