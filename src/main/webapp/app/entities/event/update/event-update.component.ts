import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IEventType } from 'app/entities/event-type/event-type.model';
import { EventTypeService } from 'app/entities/event-type/service/event-type.service';
import { IPerson } from 'app/entities/person/person.model';
import { PersonService } from 'app/entities/person/service/person.service';
import { EventService } from '../service/event.service';
import { IEvent } from '../event.model';
import { EventFormService, EventFormGroup } from './event-form.service';

@Component({
  standalone: true,
  selector: 'jhi-event-update',
  templateUrl: './event-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule, RouterLink],
})
export class EventUpdateComponent implements OnInit {
  isSaving = false;
  event: IEvent | null = null;

  eventTypesSharedCollection: IEventType[] = [];
  peopleSharedCollection: IPerson[] = [];

  editForm: EventFormGroup = this.eventFormService.createEventFormGroup();

  constructor(
    protected eventService: EventService,
    protected eventFormService: EventFormService,
    protected eventTypeService: EventTypeService,
    protected personService: PersonService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareEventType = (o1: IEventType | null, o2: IEventType | null): boolean => this.eventTypeService.compareEventType(o1, o2);

  comparePerson = (o1: IPerson | null, o2: IPerson | null): boolean => this.personService.comparePerson(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ event }) => {
      this.event = event;
      if (event) {
        this.updateForm(event);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const event = this.eventFormService.getEvent(this.editForm);
    if (event.id !== null) {
      this.subscribeToSaveResponse(this.eventService.update(event));
    } else {
      this.subscribeToSaveResponse(this.eventService.create(event));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEvent>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(event: IEvent): void {
    this.event = event;
    this.eventFormService.resetForm(this.editForm, event);

    this.eventTypesSharedCollection = this.eventTypeService.addEventTypeToCollectionIfMissing<IEventType>(
      this.eventTypesSharedCollection,
      event.eventType,
    );
    this.peopleSharedCollection = this.personService.addPersonToCollectionIfMissing<IPerson>(
      this.peopleSharedCollection,
      ...(event.people ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.eventTypeService
      .query()
      .pipe(map((res: HttpResponse<IEventType[]>) => res.body ?? []))
      .pipe(
        map((eventTypes: IEventType[]) =>
          this.eventTypeService.addEventTypeToCollectionIfMissing<IEventType>(eventTypes, this.event?.eventType),
        ),
      )
      .subscribe((eventTypes: IEventType[]) => (this.eventTypesSharedCollection = eventTypes));

    this.personService
      .query()
      .pipe(map((res: HttpResponse<IPerson[]>) => res.body ?? []))
      .pipe(map((people: IPerson[]) => this.personService.addPersonToCollectionIfMissing<IPerson>(people, ...(this.event?.people ?? []))))
      .subscribe((people: IPerson[]) => (this.peopleSharedCollection = people));
  }
}
