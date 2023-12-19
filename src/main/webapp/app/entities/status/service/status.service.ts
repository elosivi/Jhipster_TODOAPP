import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IStatus, NewStatus } from '../status.model';

export type PartialUpdateStatus = Partial<IStatus> & Pick<IStatus, 'id'>;

export type EntityResponseType = HttpResponse<IStatus>;
export type EntityArrayResponseType = HttpResponse<IStatus[]>;

@Injectable({ providedIn: 'root' })
export class StatusService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/statuses');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/statuses/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(status: NewStatus): Observable<EntityResponseType> {
    return this.http.post<IStatus>(this.resourceUrl, status, { observe: 'response' });
  }

  update(status: IStatus): Observable<EntityResponseType> {
    return this.http.put<IStatus>(`${this.resourceUrl}/${this.getStatusIdentifier(status)}`, status, { observe: 'response' });
  }

  partialUpdate(status: PartialUpdateStatus): Observable<EntityResponseType> {
    return this.http.patch<IStatus>(`${this.resourceUrl}/${this.getStatusIdentifier(status)}`, status, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IStatus>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IStatus[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IStatus[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IStatus[]>()], asapScheduler)));
  }

  getStatusIdentifier(status: Pick<IStatus, 'id'>): number {
    return status.id;
  }

  compareStatus(o1: Pick<IStatus, 'id'> | null, o2: Pick<IStatus, 'id'> | null): boolean {
    return o1 && o2 ? this.getStatusIdentifier(o1) === this.getStatusIdentifier(o2) : o1 === o2;
  }

  addStatusToCollectionIfMissing<Type extends Pick<IStatus, 'id'>>(
    statusCollection: Type[],
    ...statusesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const statuses: Type[] = statusesToCheck.filter(isPresent);
    if (statuses.length > 0) {
      const statusCollectionIdentifiers = statusCollection.map(statusItem => this.getStatusIdentifier(statusItem)!);
      const statusesToAdd = statuses.filter(statusItem => {
        const statusIdentifier = this.getStatusIdentifier(statusItem);
        if (statusCollectionIdentifiers.includes(statusIdentifier)) {
          return false;
        }
        statusCollectionIdentifiers.push(statusIdentifier);
        return true;
      });
      return [...statusesToAdd, ...statusCollection];
    }
    return statusCollection;
  }
}
