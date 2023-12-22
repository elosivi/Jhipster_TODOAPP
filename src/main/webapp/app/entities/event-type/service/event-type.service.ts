import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IEventType, NewEventType } from '../event-type.model';

export type PartialUpdateEventType = Partial<IEventType> & Pick<IEventType, 'id'>;

export type EntityResponseType = HttpResponse<IEventType>;
export type EntityArrayResponseType = HttpResponse<IEventType[]>;

@Injectable({ providedIn: 'root' })
export class EventTypeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/event-types');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/event-types/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(eventType: NewEventType): Observable<EntityResponseType> {
    return this.http.post<IEventType>(this.resourceUrl, eventType, { observe: 'response' });
  }

  update(eventType: IEventType): Observable<EntityResponseType> {
    return this.http.put<IEventType>(`${this.resourceUrl}/${this.getEventTypeIdentifier(eventType)}`, eventType, { observe: 'response' });
  }

  partialUpdate(eventType: PartialUpdateEventType): Observable<EntityResponseType> {
    return this.http.patch<IEventType>(`${this.resourceUrl}/${this.getEventTypeIdentifier(eventType)}`, eventType, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEventType>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEventType[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IEventType[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IEventType[]>()], asapScheduler)));
  }

  getEventTypeIdentifier(eventType: Pick<IEventType, 'id'>): number {
    return eventType.id;
  }

  compareEventType(o1: Pick<IEventType, 'id'> | null, o2: Pick<IEventType, 'id'> | null): boolean {
    return o1 && o2 ? this.getEventTypeIdentifier(o1) === this.getEventTypeIdentifier(o2) : o1 === o2;
  }

  addEventTypeToCollectionIfMissing<Type extends Pick<IEventType, 'id'>>(
    eventTypeCollection: Type[],
    ...eventTypesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const eventTypes: Type[] = eventTypesToCheck.filter(isPresent);
    if (eventTypes.length > 0) {
      const eventTypeCollectionIdentifiers = eventTypeCollection.map(eventTypeItem => this.getEventTypeIdentifier(eventTypeItem)!);
      const eventTypesToAdd = eventTypes.filter(eventTypeItem => {
        const eventTypeIdentifier = this.getEventTypeIdentifier(eventTypeItem);
        if (eventTypeCollectionIdentifiers.includes(eventTypeIdentifier)) {
          return false;
        }
        eventTypeCollectionIdentifiers.push(eventTypeIdentifier);
        return true;
      });
      return [...eventTypesToAdd, ...eventTypeCollection];
    }
    return eventTypeCollection;
  }
}
