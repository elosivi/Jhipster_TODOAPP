import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IStatus } from '../status.model';
import { StatusService } from '../service/status.service';

export const statusResolve = (route: ActivatedRouteSnapshot): Observable<null | IStatus> => {
  const id = route.params['id'];
  if (id) {
    return inject(StatusService)
      .find(id)
      .pipe(
        mergeMap((status: HttpResponse<IStatus>) => {
          if (status.body) {
            return of(status.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default statusResolve;
