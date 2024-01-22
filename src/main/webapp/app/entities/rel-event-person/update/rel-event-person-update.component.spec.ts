import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { IPerson } from 'app/entities/person/person.model';
import { PersonService } from 'app/entities/person/service/person.service';
import { IHierarchy } from 'app/entities/hierarchy/hierarchy.model';
import { HierarchyService } from 'app/entities/hierarchy/service/hierarchy.service';
import { IRelEventPerson } from '../rel-event-person.model';
import { RelEventPersonService } from '../service/rel-event-person.service';
import { RelEventPersonFormService } from './rel-event-person-form.service';

import { RelEventPersonUpdateComponent } from './rel-event-person-update.component';

describe('RelEventPerson Management Update Component', () => {
  let comp: RelEventPersonUpdateComponent;
  let fixture: ComponentFixture<RelEventPersonUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let relEventPersonFormService: RelEventPersonFormService;
  let relEventPersonService: RelEventPersonService;
  let eventService: EventService;
  let personService: PersonService;
  let hierarchyService: HierarchyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), RelEventPersonUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(RelEventPersonUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RelEventPersonUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    relEventPersonFormService = TestBed.inject(RelEventPersonFormService);
    relEventPersonService = TestBed.inject(RelEventPersonService);
    eventService = TestBed.inject(EventService);
    personService = TestBed.inject(PersonService);
    hierarchyService = TestBed.inject(HierarchyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Event query and add missing value', () => {
      const relEventPerson: IRelEventPerson = { id: 456 };
      const events: IEvent[] = [{ id: 847 }];
      relEventPerson.events = events;

      const eventCollection: IEvent[] = [{ id: 3770 }];
      jest.spyOn(eventService, 'query').mockReturnValue(of(new HttpResponse({ body: eventCollection })));
      const additionalEvents = [...events];
      const expectedCollection: IEvent[] = [...additionalEvents, ...eventCollection];
      jest.spyOn(eventService, 'addEventToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ relEventPerson });
      comp.ngOnInit();

      expect(eventService.query).toHaveBeenCalled();
      expect(eventService.addEventToCollectionIfMissing).toHaveBeenCalledWith(
        eventCollection,
        ...additionalEvents.map(expect.objectContaining),
      );
      expect(comp.eventsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Person query and add missing value', () => {
      const relEventPerson: IRelEventPerson = { id: 456 };
      const people: IPerson[] = [{ id: 5709 }];
      relEventPerson.people = people;

      const personCollection: IPerson[] = [{ id: 27722 }];
      jest.spyOn(personService, 'query').mockReturnValue(of(new HttpResponse({ body: personCollection })));
      const additionalPeople = [...people];
      const expectedCollection: IPerson[] = [...additionalPeople, ...personCollection];
      jest.spyOn(personService, 'addPersonToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ relEventPerson });
      comp.ngOnInit();

      expect(personService.query).toHaveBeenCalled();
      expect(personService.addPersonToCollectionIfMissing).toHaveBeenCalledWith(
        personCollection,
        ...additionalPeople.map(expect.objectContaining),
      );
      expect(comp.peopleSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Hierarchy query and add missing value', () => {
      const relEventPerson: IRelEventPerson = { id: 456 };
      const hierarchies: IHierarchy[] = [{ id: 6309 }];
      relEventPerson.hierarchies = hierarchies;

      const hierarchyCollection: IHierarchy[] = [{ id: 22079 }];
      jest.spyOn(hierarchyService, 'query').mockReturnValue(of(new HttpResponse({ body: hierarchyCollection })));
      const additionalHierarchies = [...hierarchies];
      const expectedCollection: IHierarchy[] = [...additionalHierarchies, ...hierarchyCollection];
      jest.spyOn(hierarchyService, 'addHierarchyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ relEventPerson });
      comp.ngOnInit();

      expect(hierarchyService.query).toHaveBeenCalled();
      expect(hierarchyService.addHierarchyToCollectionIfMissing).toHaveBeenCalledWith(
        hierarchyCollection,
        ...additionalHierarchies.map(expect.objectContaining),
      );
      expect(comp.hierarchiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const relEventPerson: IRelEventPerson = { id: 456 };
      const event: IEvent = { id: 24850 };
      relEventPerson.events = [event];
      const person: IPerson = { id: 13787 };
      relEventPerson.people = [person];
      const hierarchy: IHierarchy = { id: 271 };
      relEventPerson.hierarchies = [hierarchy];

      activatedRoute.data = of({ relEventPerson });
      comp.ngOnInit();

      expect(comp.eventsSharedCollection).toContain(event);
      expect(comp.peopleSharedCollection).toContain(person);
      expect(comp.hierarchiesSharedCollection).toContain(hierarchy);
      expect(comp.relEventPerson).toEqual(relEventPerson);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelEventPerson>>();
      const relEventPerson = { id: 123 };
      jest.spyOn(relEventPersonFormService, 'getRelEventPerson').mockReturnValue(relEventPerson);
      jest.spyOn(relEventPersonService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relEventPerson });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: relEventPerson }));
      saveSubject.complete();

      // THEN
      expect(relEventPersonFormService.getRelEventPerson).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(relEventPersonService.update).toHaveBeenCalledWith(expect.objectContaining(relEventPerson));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelEventPerson>>();
      const relEventPerson = { id: 123 };
      jest.spyOn(relEventPersonFormService, 'getRelEventPerson').mockReturnValue({ id: null });
      jest.spyOn(relEventPersonService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relEventPerson: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: relEventPerson }));
      saveSubject.complete();

      // THEN
      expect(relEventPersonFormService.getRelEventPerson).toHaveBeenCalled();
      expect(relEventPersonService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelEventPerson>>();
      const relEventPerson = { id: 123 };
      jest.spyOn(relEventPersonService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relEventPerson });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(relEventPersonService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareEvent', () => {
      it('Should forward to eventService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(eventService, 'compareEvent');
        comp.compareEvent(entity, entity2);
        expect(eventService.compareEvent).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePerson', () => {
      it('Should forward to personService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(personService, 'comparePerson');
        comp.comparePerson(entity, entity2);
        expect(personService.comparePerson).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareHierarchy', () => {
      it('Should forward to hierarchyService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(hierarchyService, 'compareHierarchy');
        comp.compareHierarchy(entity, entity2);
        expect(hierarchyService.compareHierarchy).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
