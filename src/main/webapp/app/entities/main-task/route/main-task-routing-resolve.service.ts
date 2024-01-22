import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMainTask } from '../main-task.model';
import { MainTaskService } from '../service/main-task.service';

export const mainTaskResolve = (route: ActivatedRouteSnapshot): Observable<null | IMainTask> => {
  const id = route.params['id'];
  if (id) {
    return inject(MainTaskService)
      .find(id)
      .pipe(
        mergeMap((mainTask: HttpResponse<IMainTask>) => {
          if (mainTask.body) {
            return of(mainTask.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default mainTaskResolve;
