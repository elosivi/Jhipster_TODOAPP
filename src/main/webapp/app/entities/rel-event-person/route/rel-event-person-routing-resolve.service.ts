import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRelEventPerson } from '../rel-event-person.model';
import { RelEventPersonService } from '../service/rel-event-person.service';

export const relEventPersonResolve = (route: ActivatedRouteSnapshot): Observable<null | IRelEventPerson> => {
  const id = route.params['id'];
  if (id) {
    return inject(RelEventPersonService)
      .find(id)
      .pipe(
        mergeMap((relEventPerson: HttpResponse<IRelEventPerson>) => {
          if (relEventPerson.body) {
            return of(relEventPerson.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default relEventPersonResolve;
