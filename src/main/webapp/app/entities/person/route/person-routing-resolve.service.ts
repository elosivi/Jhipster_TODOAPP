import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPerson } from '../person.model';
import { PersonService } from '../service/person.service';

export const personResolve = (route: ActivatedRouteSnapshot): Observable<null | IPerson> => {
  const id = route.params['id'];
  if (id) {
    return inject(PersonService)
      .find(id)
      .pipe(
        mergeMap((person: HttpResponse<IPerson>) => {
          if (person.body) {
            return of(person.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default personResolve;
