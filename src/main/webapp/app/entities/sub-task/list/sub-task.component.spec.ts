import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SubTaskService } from '../service/sub-task.service';

import { SubTaskComponent } from './sub-task.component';

describe('SubTask Management Component', () => {
  let comp: SubTaskComponent;
  let fixture: ComponentFixture<SubTaskComponent>;
  let service: SubTaskService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'sub-task', component: SubTaskComponent }]),
        HttpClientTestingModule,
        SubTaskComponent,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              }),
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(SubTaskComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SubTaskComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SubTaskService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        }),
      ),
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.subTasks?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to subTaskService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getSubTaskIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getSubTaskIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
