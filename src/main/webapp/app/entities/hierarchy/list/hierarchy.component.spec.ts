import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { HierarchyService } from '../service/hierarchy.service';

import { HierarchyComponent } from './hierarchy.component';

describe('Hierarchy Management Component', () => {
  let comp: HierarchyComponent;
  let fixture: ComponentFixture<HierarchyComponent>;
  let service: HierarchyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'hierarchy', component: HierarchyComponent }]),
        HttpClientTestingModule,
        HierarchyComponent,
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
      .overrideTemplate(HierarchyComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(HierarchyComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(HierarchyService);

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
    expect(comp.hierarchies?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to hierarchyService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getHierarchyIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getHierarchyIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
