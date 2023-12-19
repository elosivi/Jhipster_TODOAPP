import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { HierarchyService } from '../service/hierarchy.service';
import { IHierarchy } from '../hierarchy.model';
import { HierarchyFormService } from './hierarchy-form.service';

import { HierarchyUpdateComponent } from './hierarchy-update.component';

describe('Hierarchy Management Update Component', () => {
  let comp: HierarchyUpdateComponent;
  let fixture: ComponentFixture<HierarchyUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let hierarchyFormService: HierarchyFormService;
  let hierarchyService: HierarchyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), HierarchyUpdateComponent],
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
      .overrideTemplate(HierarchyUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(HierarchyUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    hierarchyFormService = TestBed.inject(HierarchyFormService);
    hierarchyService = TestBed.inject(HierarchyService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const hierarchy: IHierarchy = { id: 456 };

      activatedRoute.data = of({ hierarchy });
      comp.ngOnInit();

      expect(comp.hierarchy).toEqual(hierarchy);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHierarchy>>();
      const hierarchy = { id: 123 };
      jest.spyOn(hierarchyFormService, 'getHierarchy').mockReturnValue(hierarchy);
      jest.spyOn(hierarchyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ hierarchy });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: hierarchy }));
      saveSubject.complete();

      // THEN
      expect(hierarchyFormService.getHierarchy).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(hierarchyService.update).toHaveBeenCalledWith(expect.objectContaining(hierarchy));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHierarchy>>();
      const hierarchy = { id: 123 };
      jest.spyOn(hierarchyFormService, 'getHierarchy').mockReturnValue({ id: null });
      jest.spyOn(hierarchyService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ hierarchy: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: hierarchy }));
      saveSubject.complete();

      // THEN
      expect(hierarchyFormService.getHierarchy).toHaveBeenCalled();
      expect(hierarchyService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IHierarchy>>();
      const hierarchy = { id: 123 };
      jest.spyOn(hierarchyService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ hierarchy });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(hierarchyService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
