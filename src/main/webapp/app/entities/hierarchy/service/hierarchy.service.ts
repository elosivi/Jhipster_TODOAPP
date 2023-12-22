import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IHierarchy, NewHierarchy } from '../hierarchy.model';

export type PartialUpdateHierarchy = Partial<IHierarchy> & Pick<IHierarchy, 'id'>;

export type EntityResponseType = HttpResponse<IHierarchy>;
export type EntityArrayResponseType = HttpResponse<IHierarchy[]>;

@Injectable({ providedIn: 'root' })
export class HierarchyService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/hierarchies');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/hierarchies/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(hierarchy: NewHierarchy): Observable<EntityResponseType> {
    return this.http.post<IHierarchy>(this.resourceUrl, hierarchy, { observe: 'response' });
  }

  update(hierarchy: IHierarchy): Observable<EntityResponseType> {
    return this.http.put<IHierarchy>(`${this.resourceUrl}/${this.getHierarchyIdentifier(hierarchy)}`, hierarchy, { observe: 'response' });
  }

  partialUpdate(hierarchy: PartialUpdateHierarchy): Observable<EntityResponseType> {
    return this.http.patch<IHierarchy>(`${this.resourceUrl}/${this.getHierarchyIdentifier(hierarchy)}`, hierarchy, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IHierarchy>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IHierarchy[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IHierarchy[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IHierarchy[]>()], asapScheduler)));
  }

  getHierarchyIdentifier(hierarchy: Pick<IHierarchy, 'id'>): number {
    return hierarchy.id;
  }

  compareHierarchy(o1: Pick<IHierarchy, 'id'> | null, o2: Pick<IHierarchy, 'id'> | null): boolean {
    return o1 && o2 ? this.getHierarchyIdentifier(o1) === this.getHierarchyIdentifier(o2) : o1 === o2;
  }

  addHierarchyToCollectionIfMissing<Type extends Pick<IHierarchy, 'id'>>(
    hierarchyCollection: Type[],
    ...hierarchiesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const hierarchies: Type[] = hierarchiesToCheck.filter(isPresent);
    if (hierarchies.length > 0) {
      const hierarchyCollectionIdentifiers = hierarchyCollection.map(hierarchyItem => this.getHierarchyIdentifier(hierarchyItem)!);
      const hierarchiesToAdd = hierarchies.filter(hierarchyItem => {
        const hierarchyIdentifier = this.getHierarchyIdentifier(hierarchyItem);
        if (hierarchyCollectionIdentifiers.includes(hierarchyIdentifier)) {
          return false;
        }
        hierarchyCollectionIdentifiers.push(hierarchyIdentifier);
        return true;
      });
      return [...hierarchiesToAdd, ...hierarchyCollection];
    }
    return hierarchyCollection;
  }
}
