import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IMainTask, NewMainTask } from '../main-task.model';

export type PartialUpdateMainTask = Partial<IMainTask> & Pick<IMainTask, 'id'>;

type RestOf<T extends IMainTask | NewMainTask> = Omit<T, 'deadline' | 'creation'> & {
  deadline?: string | null;
  creation?: string | null;
};

export type RestMainTask = RestOf<IMainTask>;

export type NewRestMainTask = RestOf<NewMainTask>;

export type PartialUpdateRestMainTask = RestOf<PartialUpdateMainTask>;

export type EntityResponseType = HttpResponse<IMainTask>;
export type EntityArrayResponseType = HttpResponse<IMainTask[]>;

@Injectable({ providedIn: 'root' })
export class MainTaskService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/main-tasks');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/main-tasks/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(mainTask: NewMainTask): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(mainTask);
    return this.http
      .post<RestMainTask>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(mainTask: IMainTask): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(mainTask);
    return this.http
      .put<RestMainTask>(`${this.resourceUrl}/${this.getMainTaskIdentifier(mainTask)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(mainTask: PartialUpdateMainTask): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(mainTask);
    return this.http
      .patch<RestMainTask>(`${this.resourceUrl}/${this.getMainTaskIdentifier(mainTask)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestMainTask>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestMainTask[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestMainTask[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([new HttpResponse<IMainTask[]>()], asapScheduler)),
    );
  }

  getMainTaskIdentifier(mainTask: Pick<IMainTask, 'id'>): number {
    return mainTask.id;
  }

  compareMainTask(o1: Pick<IMainTask, 'id'> | null, o2: Pick<IMainTask, 'id'> | null): boolean {
    return o1 && o2 ? this.getMainTaskIdentifier(o1) === this.getMainTaskIdentifier(o2) : o1 === o2;
  }

  addMainTaskToCollectionIfMissing<Type extends Pick<IMainTask, 'id'>>(
    mainTaskCollection: Type[],
    ...mainTasksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const mainTasks: Type[] = mainTasksToCheck.filter(isPresent);
    if (mainTasks.length > 0) {
      const mainTaskCollectionIdentifiers = mainTaskCollection.map(mainTaskItem => this.getMainTaskIdentifier(mainTaskItem)!);
      const mainTasksToAdd = mainTasks.filter(mainTaskItem => {
        const mainTaskIdentifier = this.getMainTaskIdentifier(mainTaskItem);
        if (mainTaskCollectionIdentifiers.includes(mainTaskIdentifier)) {
          return false;
        }
        mainTaskCollectionIdentifiers.push(mainTaskIdentifier);
        return true;
      });
      return [...mainTasksToAdd, ...mainTaskCollection];
    }
    return mainTaskCollection;
  }

  protected convertDateFromClient<T extends IMainTask | NewMainTask | PartialUpdateMainTask>(mainTask: T): RestOf<T> {
    return {
      ...mainTask,
      deadline: mainTask.deadline?.format(DATE_FORMAT) ?? null,
      creation: mainTask.creation?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restMainTask: RestMainTask): IMainTask {
    return {
      ...restMainTask,
      deadline: restMainTask.deadline ? dayjs(restMainTask.deadline) : undefined,
      creation: restMainTask.creation ? dayjs(restMainTask.creation) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestMainTask>): HttpResponse<IMainTask> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestMainTask[]>): HttpResponse<IMainTask[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
