import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { MainTaskService } from '../service/main-task.service';

import { MainTaskComponent } from './main-task.component';

describe('MainTask Management Component', () => {
  let comp: MainTaskComponent;
  let fixture: ComponentFixture<MainTaskComponent>;
  let service: MainTaskService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'main-task', component: MainTaskComponent }]),
        HttpClientTestingModule,
        MainTaskComponent,
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
      .overrideTemplate(MainTaskComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MainTaskComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(MainTaskService);

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
    expect(comp.mainTasks?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to mainTaskService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getMainTaskIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getMainTaskIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
