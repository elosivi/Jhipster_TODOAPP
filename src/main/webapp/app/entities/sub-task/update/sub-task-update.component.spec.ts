import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IMainTask } from 'app/entities/main-task/main-task.model';
import { MainTaskService } from 'app/entities/main-task/service/main-task.service';
import { IPerson } from 'app/entities/person/person.model';
import { PersonService } from 'app/entities/person/service/person.service';
import { IStatus } from 'app/entities/status/status.model';
import { StatusService } from 'app/entities/status/service/status.service';
import { ISubTask } from '../sub-task.model';
import { SubTaskService } from '../service/sub-task.service';
import { SubTaskFormService } from './sub-task-form.service';

import { SubTaskUpdateComponent } from './sub-task-update.component';

describe('SubTask Management Update Component', () => {
  let comp: SubTaskUpdateComponent;
  let fixture: ComponentFixture<SubTaskUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let subTaskFormService: SubTaskFormService;
  let subTaskService: SubTaskService;
  let mainTaskService: MainTaskService;
  let personService: PersonService;
  let statusService: StatusService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), SubTaskUpdateComponent],
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
      .overrideTemplate(SubTaskUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SubTaskUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    subTaskFormService = TestBed.inject(SubTaskFormService);
    subTaskService = TestBed.inject(SubTaskService);
    mainTaskService = TestBed.inject(MainTaskService);
    personService = TestBed.inject(PersonService);
    statusService = TestBed.inject(StatusService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call MainTask query and add missing value', () => {
      const subTask: ISubTask = { id: 456 };
      const mainTask: IMainTask = { id: 32585 };
      subTask.mainTask = mainTask;

      const mainTaskCollection: IMainTask[] = [{ id: 19278 }];
      jest.spyOn(mainTaskService, 'query').mockReturnValue(of(new HttpResponse({ body: mainTaskCollection })));
      const additionalMainTasks = [mainTask];
      const expectedCollection: IMainTask[] = [...additionalMainTasks, ...mainTaskCollection];
      jest.spyOn(mainTaskService, 'addMainTaskToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ subTask });
      comp.ngOnInit();

      expect(mainTaskService.query).toHaveBeenCalled();
      expect(mainTaskService.addMainTaskToCollectionIfMissing).toHaveBeenCalledWith(
        mainTaskCollection,
        ...additionalMainTasks.map(expect.objectContaining),
      );
      expect(comp.mainTasksSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Person query and add missing value', () => {
      const subTask: ISubTask = { id: 456 };
      const personDoer: IPerson = { id: 5184 };
      subTask.personDoer = personDoer;

      const personCollection: IPerson[] = [{ id: 32047 }];
      jest.spyOn(personService, 'query').mockReturnValue(of(new HttpResponse({ body: personCollection })));
      const additionalPeople = [personDoer];
      const expectedCollection: IPerson[] = [...additionalPeople, ...personCollection];
      jest.spyOn(personService, 'addPersonToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ subTask });
      comp.ngOnInit();

      expect(personService.query).toHaveBeenCalled();
      expect(personService.addPersonToCollectionIfMissing).toHaveBeenCalledWith(
        personCollection,
        ...additionalPeople.map(expect.objectContaining),
      );
      expect(comp.peopleSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Status query and add missing value', () => {
      const subTask: ISubTask = { id: 456 };
      const status: IStatus = { id: 11623 };
      subTask.status = status;

      const statusCollection: IStatus[] = [{ id: 5094 }];
      jest.spyOn(statusService, 'query').mockReturnValue(of(new HttpResponse({ body: statusCollection })));
      const additionalStatuses = [status];
      const expectedCollection: IStatus[] = [...additionalStatuses, ...statusCollection];
      jest.spyOn(statusService, 'addStatusToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ subTask });
      comp.ngOnInit();

      expect(statusService.query).toHaveBeenCalled();
      expect(statusService.addStatusToCollectionIfMissing).toHaveBeenCalledWith(
        statusCollection,
        ...additionalStatuses.map(expect.objectContaining),
      );
      expect(comp.statusesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const subTask: ISubTask = { id: 456 };
      const mainTask: IMainTask = { id: 3659 };
      subTask.mainTask = mainTask;
      const personDoer: IPerson = { id: 30654 };
      subTask.personDoer = personDoer;
      const status: IStatus = { id: 27202 };
      subTask.status = status;

      activatedRoute.data = of({ subTask });
      comp.ngOnInit();

      expect(comp.mainTasksSharedCollection).toContain(mainTask);
      expect(comp.peopleSharedCollection).toContain(personDoer);
      expect(comp.statusesSharedCollection).toContain(status);
      expect(comp.subTask).toEqual(subTask);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISubTask>>();
      const subTask = { id: 123 };
      jest.spyOn(subTaskFormService, 'getSubTask').mockReturnValue(subTask);
      jest.spyOn(subTaskService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ subTask });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: subTask }));
      saveSubject.complete();

      // THEN
      expect(subTaskFormService.getSubTask).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(subTaskService.update).toHaveBeenCalledWith(expect.objectContaining(subTask));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISubTask>>();
      const subTask = { id: 123 };
      jest.spyOn(subTaskFormService, 'getSubTask').mockReturnValue({ id: null });
      jest.spyOn(subTaskService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ subTask: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: subTask }));
      saveSubject.complete();

      // THEN
      expect(subTaskFormService.getSubTask).toHaveBeenCalled();
      expect(subTaskService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISubTask>>();
      const subTask = { id: 123 };
      jest.spyOn(subTaskService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ subTask });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(subTaskService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareMainTask', () => {
      it('Should forward to mainTaskService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(mainTaskService, 'compareMainTask');
        comp.compareMainTask(entity, entity2);
        expect(mainTaskService.compareMainTask).toHaveBeenCalledWith(entity, entity2);
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

    describe('compareStatus', () => {
      it('Should forward to statusService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(statusService, 'compareStatus');
        comp.compareStatus(entity, entity2);
        expect(statusService.compareStatus).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
