import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { PersonService } from '../service/person.service';
import { IPerson } from '../person.model';

import { PersonFormService } from './person-form.service';

import { PersonUpdateComponent } from './person-update.component';

describe('Person Management Update Component', () => {
  let comp: PersonUpdateComponent;
  let fixture: ComponentFixture<PersonUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let personFormService: PersonFormService;
  let personService: PersonService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), PersonUpdateComponent],
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
      .overrideTemplate(PersonUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PersonUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    personFormService = TestBed.inject(PersonFormService);
    personService = TestBed.inject(PersonService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const person: IPerson = { id: 456 };
      const user: IUser = { id: 2461 };
      person.user = user;

      const userCollection: IUser[] = [{ id: 23350 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ person });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const person: IPerson = { id: 456 };
      const user: IUser = { id: 31326 };
      person.user = user;

      activatedRoute.data = of({ person });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.person).toEqual(person);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPerson>>();
      const person = { id: 123 };
      jest.spyOn(personFormService, 'getPerson').mockReturnValue(person);
      jest.spyOn(personService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ person });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: person }));
      saveSubject.complete();

      // THEN
      expect(personFormService.getPerson).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(personService.update).toHaveBeenCalledWith(expect.objectContaining(person));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPerson>>();
      const person = { id: 123 };
      jest.spyOn(personFormService, 'getPerson').mockReturnValue({ id: null });
      jest.spyOn(personService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ person: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: person }));
      saveSubject.complete();

      // THEN
      expect(personFormService.getPerson).toHaveBeenCalled();
      expect(personService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPerson>>();
      const person = { id: 123 };
      jest.spyOn(personService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ person });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(personService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
