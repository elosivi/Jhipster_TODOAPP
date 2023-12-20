import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { IPerson } from 'app/entities/person/person.model';
import { PersonService } from 'app/entities/person/service/person.service';
import { IStatus } from 'app/entities/status/status.model';
import { StatusService } from 'app/entities/status/service/status.service';
import { IMainTask } from '../main-task.model';
import { MainTaskService } from '../service/main-task.service';
import { MainTaskFormService } from './main-task-form.service';

import { MainTaskUpdateComponent } from './main-task-update.component';

describe('MainTask Management Update Component', () => {
  let comp: MainTaskUpdateComponent;
  let fixture: ComponentFixture<MainTaskUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let mainTaskFormService: MainTaskFormService;
  let mainTaskService: MainTaskService;
  let categoryService: CategoryService;
  let personService: PersonService;
  let statusService: StatusService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), MainTaskUpdateComponent],
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
      .overrideTemplate(MainTaskUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MainTaskUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    mainTaskFormService = TestBed.inject(MainTaskFormService);
    mainTaskService = TestBed.inject(MainTaskService);
    categoryService = TestBed.inject(CategoryService);
    personService = TestBed.inject(PersonService);
    statusService = TestBed.inject(StatusService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Category query and add missing value', () => {
      const mainTask: IMainTask = { id: 456 };
      const category: ICategory = { id: 7881 };
      mainTask.category = category;

      const categoryCollection: ICategory[] = [{ id: 19294 }];
      jest.spyOn(categoryService, 'query').mockReturnValue(of(new HttpResponse({ body: categoryCollection })));
      const additionalCategories = [category];
      const expectedCollection: ICategory[] = [...additionalCategories, ...categoryCollection];
      jest.spyOn(categoryService, 'addCategoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ mainTask });
      comp.ngOnInit();

      expect(categoryService.query).toHaveBeenCalled();
      expect(categoryService.addCategoryToCollectionIfMissing).toHaveBeenCalledWith(
        categoryCollection,
        ...additionalCategories.map(expect.objectContaining),
      );
      expect(comp.categoriesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Person query and add missing value', () => {
      const mainTask: IMainTask = { id: 456 };
      const personOwner: IPerson = { id: 10567 };
      mainTask.personOwner = personOwner;

      const personCollection: IPerson[] = [{ id: 24622 }];
      jest.spyOn(personService, 'query').mockReturnValue(of(new HttpResponse({ body: personCollection })));
      const additionalPeople = [personOwner];
      const expectedCollection: IPerson[] = [...additionalPeople, ...personCollection];
      jest.spyOn(personService, 'addPersonToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ mainTask });
      comp.ngOnInit();

      expect(personService.query).toHaveBeenCalled();
      expect(personService.addPersonToCollectionIfMissing).toHaveBeenCalledWith(
        personCollection,
        ...additionalPeople.map(expect.objectContaining),
      );
      expect(comp.peopleSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Status query and add missing value', () => {
      const mainTask: IMainTask = { id: 456 };
      const status: IStatus = { id: 17332 };
      mainTask.status = status;

      const statusCollection: IStatus[] = [{ id: 32105 }];
      jest.spyOn(statusService, 'query').mockReturnValue(of(new HttpResponse({ body: statusCollection })));
      const additionalStatuses = [status];
      const expectedCollection: IStatus[] = [...additionalStatuses, ...statusCollection];
      jest.spyOn(statusService, 'addStatusToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ mainTask });
      comp.ngOnInit();

      expect(statusService.query).toHaveBeenCalled();
      expect(statusService.addStatusToCollectionIfMissing).toHaveBeenCalledWith(
        statusCollection,
        ...additionalStatuses.map(expect.objectContaining),
      );
      expect(comp.statusesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const mainTask: IMainTask = { id: 456 };
      const category: ICategory = { id: 8579 };
      mainTask.category = category;
      const personOwner: IPerson = { id: 27685 };
      mainTask.personOwner = personOwner;
      const status: IStatus = { id: 4732 };
      mainTask.status = status;

      activatedRoute.data = of({ mainTask });
      comp.ngOnInit();

      expect(comp.categoriesSharedCollection).toContain(category);
      expect(comp.peopleSharedCollection).toContain(personOwner);
      expect(comp.statusesSharedCollection).toContain(status);
      expect(comp.mainTask).toEqual(mainTask);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMainTask>>();
      const mainTask = { id: 123 };
      jest.spyOn(mainTaskFormService, 'getMainTask').mockReturnValue(mainTask);
      jest.spyOn(mainTaskService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mainTask });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: mainTask }));
      saveSubject.complete();

      // THEN
      expect(mainTaskFormService.getMainTask).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(mainTaskService.update).toHaveBeenCalledWith(expect.objectContaining(mainTask));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMainTask>>();
      const mainTask = { id: 123 };
      jest.spyOn(mainTaskFormService, 'getMainTask').mockReturnValue({ id: null });
      jest.spyOn(mainTaskService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mainTask: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: mainTask }));
      saveSubject.complete();

      // THEN
      expect(mainTaskFormService.getMainTask).toHaveBeenCalled();
      expect(mainTaskService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMainTask>>();
      const mainTask = { id: 123 };
      jest.spyOn(mainTaskService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mainTask });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(mainTaskService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCategory', () => {
      it('Should forward to categoryService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(categoryService, 'compareCategory');
        comp.compareCategory(entity, entity2);
        expect(categoryService.compareCategory).toHaveBeenCalledWith(entity, entity2);
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
