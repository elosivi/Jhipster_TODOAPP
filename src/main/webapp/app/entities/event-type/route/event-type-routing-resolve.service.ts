import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEventType } from '../event-type.model';
import { EventTypeService } from '../service/event-type.service';

export const eventTypeResolve = (route: ActivatedRouteSnapshot): Observable<null | IEventType> => {
  const id = route.params['id'];
  if (id) {
    return inject(EventTypeService)
      .find(id)
      .pipe(
        mergeMap((eventType: HttpResponse<IEventType>) => {
          if (eventType.body) {
            return of(eventType.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default eventTypeResolve;
