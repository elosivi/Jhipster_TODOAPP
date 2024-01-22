import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IHierarchy } from '../hierarchy.model';
import { HierarchyService } from '../service/hierarchy.service';

export const hierarchyResolve = (route: ActivatedRouteSnapshot): Observable<null | IHierarchy> => {
  const id = route.params['id'];
  if (id) {
    return inject(HierarchyService)
      .find(id)
      .pipe(
        mergeMap((hierarchy: HttpResponse<IHierarchy>) => {
          if (hierarchy.body) {
            return of(hierarchy.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default hierarchyResolve;
