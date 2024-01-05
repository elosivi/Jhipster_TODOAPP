import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IPerson, NewPerson } from '../person.model';

export type PartialUpdatePerson = Partial<IPerson> & Pick<IPerson, 'id'>;

export type EntityResponseType = HttpResponse<IPerson>;
export type EntityArrayResponseType = HttpResponse<IPerson[]>;

@Injectable({ providedIn: 'root' })
export class PersonService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/people');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/people/_search');
  protected resourcePersonsWithUsersUrl = this.applicationConfigService.getEndpointFor('api/people/persons-with-users');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(person: NewPerson): Observable<EntityResponseType> {
    return this.http.post<IPerson>(this.resourceUrl, person, { observe: 'response' });
  }

  update(person: IPerson): Observable<EntityResponseType> {
    return this.http.put<IPerson>(`${this.resourceUrl}/${this.getPersonIdentifier(person)}`, person, { observe: 'response' });
  }

  partialUpdate(person: PartialUpdatePerson): Observable<EntityResponseType> {
    return this.http.patch<IPerson>(`${this.resourceUrl}/${this.getPersonIdentifier(person)}`, person, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPerson>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPerson[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  /**
   * Get all Persons with, for each, user data attached
   * @param req
   */
  queryPersonsWithUsers(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPerson[]>(this.resourcePersonsWithUsersUrl, { params: options, observe: 'response' });
  }
  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPerson[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IPerson[]>()], asapScheduler)));
  }

  getPersonIdentifier(person: Pick<IPerson, 'id'>): number {
    return person.id;
  }

  comparePerson(o1: Pick<IPerson, 'id'> | null, o2: Pick<IPerson, 'id'> | null): boolean {
    return o1 && o2 ? this.getPersonIdentifier(o1) === this.getPersonIdentifier(o2) : o1 === o2;
  }

  addPersonToCollectionIfMissing<Type extends Pick<IPerson, 'id'>>(
    personCollection: Type[],
    ...peopleToCheck: (Type | null | undefined)[]
  ): Type[] {
    const people: Type[] = peopleToCheck.filter(isPresent);
    if (people.length > 0) {
      const personCollectionIdentifiers = personCollection.map(personItem => this.getPersonIdentifier(personItem)!);
      const peopleToAdd = people.filter(personItem => {
        const personIdentifier = this.getPersonIdentifier(personItem);
        if (personCollectionIdentifiers.includes(personIdentifier)) {
          return false;
        }
        personCollectionIdentifiers.push(personIdentifier);
        return true;
      });
      return [...peopleToAdd, ...personCollection];
    }
    return personCollection;
  }

  /**
   * Link or unlink an existing user with an existing person
   * @param userId user id
   * @param personId person ID
   */
  associateUserWithPerson(userId: number, personId: number): Observable<HttpResponse<{}>> | undefined {
    //link or unlink
    if (userId != null && personId != null) {
      const url = `${this.resourceUrl}/associate-user/${userId}/with-person/${personId}`;
      return this.http.post(url, {}, { observe: 'response' });
    }
    console.warn("impossible d'associer le user : {} avec le participant {}", userId, personId);
    return undefined;
  }

  /**
   * load the person associated to the user in param
   * @param userId
   */
  findByUserAssociated(userId: number): Observable<EntityResponseType> {
    return this.http.get<IPerson>(`${this.resourceUrl}/person?userId=${userId}`, { observe: 'response' });
  }
}
