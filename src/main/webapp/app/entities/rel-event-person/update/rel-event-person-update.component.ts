import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { IPerson } from 'app/entities/person/person.model';
import { PersonService } from 'app/entities/person/service/person.service';
import { IHierarchy } from 'app/entities/hierarchy/hierarchy.model';
import { HierarchyService } from 'app/entities/hierarchy/service/hierarchy.service';
import { RelEventPersonService } from '../service/rel-event-person.service';
import { IRelEventPerson } from '../rel-event-person.model';
import { RelEventPersonFormService, RelEventPersonFormGroup } from './rel-event-person-form.service';

@Component({
  standalone: true,
  selector: 'jhi-rel-event-person-update',
  templateUrl: './rel-event-person-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class RelEventPersonUpdateComponent implements OnInit {
  isSaving = false;
  relEventPerson: IRelEventPerson | null = null;

  eventsSharedCollection: IEvent[] = [];
  peopleSharedCollection: IPerson[] = [];
  hierarchiesSharedCollection: IHierarchy[] = [];

  editForm: RelEventPersonFormGroup = this.relEventPersonFormService.createRelEventPersonFormGroup();

  constructor(
    protected relEventPersonService: RelEventPersonService,
    protected relEventPersonFormService: RelEventPersonFormService,
    protected eventService: EventService,
    protected personService: PersonService,
    protected hierarchyService: HierarchyService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareEvent = (o1: IEvent | null, o2: IEvent | null): boolean => this.eventService.compareEvent(o1, o2);

  comparePerson = (o1: IPerson | null, o2: IPerson | null): boolean => this.personService.comparePerson(o1, o2);

  compareHierarchy = (o1: IHierarchy | null, o2: IHierarchy | null): boolean => this.hierarchyService.compareHierarchy(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ relEventPerson }) => {
      this.relEventPerson = relEventPerson;
      if (relEventPerson) {
        this.updateForm(relEventPerson);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const relEventPerson = this.relEventPersonFormService.getRelEventPerson(this.editForm);
    if (relEventPerson.id !== null) {
      this.subscribeToSaveResponse(this.relEventPersonService.update(relEventPerson));
    } else {
      this.subscribeToSaveResponse(this.relEventPersonService.create(relEventPerson));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRelEventPerson>>): void {
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

  protected updateForm(relEventPerson: IRelEventPerson): void {
    this.relEventPerson = relEventPerson;
    this.relEventPersonFormService.resetForm(this.editForm, relEventPerson);

    this.eventsSharedCollection = this.eventService.addEventToCollectionIfMissing<IEvent>(
      this.eventsSharedCollection,
      relEventPerson.event,
    );
    this.peopleSharedCollection = this.personService.addPersonToCollectionIfMissing<IPerson>(
      this.peopleSharedCollection,
      relEventPerson.person,
    );
    this.hierarchiesSharedCollection = this.hierarchyService.addHierarchyToCollectionIfMissing<IHierarchy>(
      this.hierarchiesSharedCollection,
      relEventPerson.hierarchy,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.eventService
      .query()
      .pipe(map((res: HttpResponse<IEvent[]>) => res.body ?? []))
      .pipe(map((events: IEvent[]) => this.eventService.addEventToCollectionIfMissing<IEvent>(events, this.relEventPerson?.event)))
      .subscribe((events: IEvent[]) => (this.eventsSharedCollection = events));

    this.personService
      .query()
      .pipe(map((res: HttpResponse<IPerson[]>) => res.body ?? []))
      .pipe(map((people: IPerson[]) => this.personService.addPersonToCollectionIfMissing<IPerson>(people, this.relEventPerson?.person)))
      .subscribe((people: IPerson[]) => (this.peopleSharedCollection = people));

    this.hierarchyService
      .query()
      .pipe(map((res: HttpResponse<IHierarchy[]>) => res.body ?? []))
      .pipe(
        map((hierarchies: IHierarchy[]) =>
          this.hierarchyService.addHierarchyToCollectionIfMissing<IHierarchy>(hierarchies, this.relEventPerson?.hierarchy),
        ),
      )
      .subscribe((hierarchies: IHierarchy[]) => (this.hierarchiesSharedCollection = hierarchies));
  }
}
