import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IRelEventPerson, NewRelEventPerson } from '../rel-event-person.model';

export type PartialUpdateRelEventPerson = Partial<IRelEventPerson> & Pick<IRelEventPerson, 'id'>;

export type EntityResponseType = HttpResponse<IRelEventPerson>;
export type EntityArrayResponseType = HttpResponse<IRelEventPerson[]>;

@Injectable({ providedIn: 'root' })
export class RelEventPersonService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/rel-event-people');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/rel-event-people/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(relEventPerson: NewRelEventPerson): Observable<EntityResponseType> {
    return this.http.post<IRelEventPerson>(this.resourceUrl, relEventPerson, { observe: 'response' });
  }

  update(relEventPerson: IRelEventPerson): Observable<EntityResponseType> {
    return this.http.put<IRelEventPerson>(`${this.resourceUrl}/${this.getRelEventPersonIdentifier(relEventPerson)}`, relEventPerson, {
      observe: 'response',
    });
  }

  partialUpdate(relEventPerson: PartialUpdateRelEventPerson): Observable<EntityResponseType> {
    return this.http.patch<IRelEventPerson>(`${this.resourceUrl}/${this.getRelEventPersonIdentifier(relEventPerson)}`, relEventPerson, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRelEventPerson>(`${this.resourceUrl}/management/${id}`, { observe: 'response' });
  }

  /** load all couple event/person in database with data about event, person, user, hierarchy and participation */
  findAllWithRelationData(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRelEventPerson[]>(`${this.resourceUrl}/management`, { params: options, observe: 'response' });
  }

  /** load all couple event/person in database with data about event, person, user, hierarchy and participation */
  findByEventWithRelationData(id: number, req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRelEventPerson[]>(`${this.resourceUrl}/management/byEvent/${id}`, { params: options, observe: 'response' });
  }

  /** load all couple event/person in database with data about event, person, user, hierarchy and participation */
  findByPersonWithRelationData(id: number, req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRelEventPerson[]>(`${this.resourceUrl}/management/byPerson/${id}`, { params: options, observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRelEventPerson[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IRelEventPerson[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IRelEventPerson[]>()], asapScheduler)));
  }

  getRelEventPersonIdentifier(relEventPerson: Pick<IRelEventPerson, 'id'>): number {
    return relEventPerson.id;
  }

  compareRelEventPerson(o1: Pick<IRelEventPerson, 'id'> | null, o2: Pick<IRelEventPerson, 'id'> | null): boolean {
    return o1 && o2 ? this.getRelEventPersonIdentifier(o1) === this.getRelEventPersonIdentifier(o2) : o1 === o2;
  }

  addRelEventPersonToCollectionIfMissing<Type extends Pick<IRelEventPerson, 'id'>>(
    relEventPersonCollection: Type[],
    ...relEventPeopleToCheck: (Type | null | undefined)[]
  ): Type[] {
    const relEventPeople: Type[] = relEventPeopleToCheck.filter(isPresent);
    if (relEventPeople.length > 0) {
      const relEventPersonCollectionIdentifiers = relEventPersonCollection.map(
        relEventPersonItem => this.getRelEventPersonIdentifier(relEventPersonItem)!,
      );
      const relEventPeopleToAdd = relEventPeople.filter(relEventPersonItem => {
        const relEventPersonIdentifier = this.getRelEventPersonIdentifier(relEventPersonItem);
        if (relEventPersonCollectionIdentifiers.includes(relEventPersonIdentifier)) {
          return false;
        }
        relEventPersonCollectionIdentifiers.push(relEventPersonIdentifier);
        return true;
      });
      return [...relEventPeopleToAdd, ...relEventPersonCollection];
    }
    return relEventPersonCollection;
  }
}
