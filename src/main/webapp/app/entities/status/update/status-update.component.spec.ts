import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { StatusService } from '../service/status.service';
import { IStatus } from '../status.model';
import { StatusFormService } from './status-form.service';

import { StatusUpdateComponent } from './status-update.component';

describe('Status Management Update Component', () => {
  let comp: StatusUpdateComponent;
  let fixture: ComponentFixture<StatusUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let statusFormService: StatusFormService;
  let statusService: StatusService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), StatusUpdateComponent],
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
      .overrideTemplate(StatusUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StatusUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    statusFormService = TestBed.inject(StatusFormService);
    statusService = TestBed.inject(StatusService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const status: IStatus = { id: 456 };

      activatedRoute.data = of({ status });
      comp.ngOnInit();

      expect(comp.status).toEqual(status);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStatus>>();
      const status = { id: 123 };
      jest.spyOn(statusFormService, 'getStatus').mockReturnValue(status);
      jest.spyOn(statusService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ status });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: status }));
      saveSubject.complete();

      // THEN
      expect(statusFormService.getStatus).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(statusService.update).toHaveBeenCalledWith(expect.objectContaining(status));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStatus>>();
      const status = { id: 123 };
      jest.spyOn(statusFormService, 'getStatus').mockReturnValue({ id: null });
      jest.spyOn(statusService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ status: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: status }));
      saveSubject.complete();

      // THEN
      expect(statusFormService.getStatus).toHaveBeenCalled();
      expect(statusService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStatus>>();
      const status = { id: 123 };
      jest.spyOn(statusService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ status });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(statusService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
