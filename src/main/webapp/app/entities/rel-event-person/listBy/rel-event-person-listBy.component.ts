import { Component, OnInit } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { combineLatest, filter, Observable, of, Subject, switchMap, takeUntil, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortDirective, SortByDirective } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { ItemCountComponent } from 'app/shared/pagination';
import { FormsModule } from '@angular/forms';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { ASC, DESC, SORT, ITEM_DELETED_EVENT, DEFAULT_SORT_DATA } from 'app/config/navigation.constants';
import { FilterComponent, FilterOptions, IFilterOptions, IFilterOption } from 'app/shared/filter';
import { IRelEventPerson } from '../rel-event-person.model';

import { EntityArrayResponseType, RelEventPersonService } from '../service/rel-event-person.service';
import { RelEventPersonDeleteDialogComponent } from '../delete/rel-event-person-delete-dialog.component';
import { AccountService } from 'app/core/auth/account.service';
import { IUser } from 'app/admin/user-management/user-management.model';
import { IPerson } from 'app/entities/person/person.model';
import { EntityResponseType, PersonService } from 'app/entities/person/service/person.service';

@Component({
  standalone: true,
  selector: 'jhi-rel-event-person-listBy',
  templateUrl: './rel-event-person-listBy.component.html',
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    FilterComponent,
    ItemCountComponent,
  ],
})
export class RelEventPersonComponent implements OnInit {
  private static readonly NOT_SORTABLE_FIELDS_AFTER_SEARCH = ['participation'];

  userConnected: IUser | null = null;
  personConnected: IPerson | null = null;
  private readonly destroy$ = new Subject<void>();
  userConnectedHasAPersonLinked: boolean | undefined;

  relEventPeople?: IRelEventPerson[];
  isLoading = false;

  predicate = 'id';
  ascending = true;
  currentSearch = '';
  filters: IFilterOptions = new FilterOptions();

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  constructor(
    private accountService: AccountService,
    protected relEventPersonService: RelEventPersonService,
    protected personService: PersonService,
    protected activatedRoute: ActivatedRoute,
    public router: Router,
    protected modalService: NgbModal,
  ) {}

  getUserId(): void {
    this.accountService
      .getUserConnected()
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => (this.userConnected = user));
  }

  /**
   * To return only the events that concern the connected user we need to know to which "person" the user is linked.
   */
  loadPersonLinkedToTheConnectedUser(): void {
    if (this.userConnected != null && this.userConnected?.id != null) {
      this.personService
        .findByUserAssociated(this.userConnected.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe(
          (person: EntityResponseType) => {
            this.personConnected = person.body;
            this.userConnectedHasAPersonLinked = true;
          },
          () => {
            console.log("impossible de charger le participant lié à l'utilisateur connecté");
            this.userConnectedHasAPersonLinked = false;
          },
        );
    }
  }

  trackId = (_index: number, item: IRelEventPerson): number => this.relEventPersonService.getRelEventPersonIdentifier(item);

  search(query: string): void {
    if (query && RelEventPersonComponent.NOT_SORTABLE_FIELDS_AFTER_SEARCH.includes(this.predicate)) {
      this.predicate = 'id';
      this.ascending = true;
    }
    this.page = 1;
    this.currentSearch = query;
    this.navigateToWithComponentValues();
  }

  ngOnInit(): void {
    this.getUserId();
    this.loadPersonLinkedToTheConnectedUser();
    if (this.personConnected) {
      this.userConnectedHasAPersonLinked = true;
      this.load();
    } else {
      this.userConnectedHasAPersonLinked = false;
    }

    this.filters.filterChanges.subscribe(filterOptions => this.handleNavigation(1, this.predicate, this.ascending, filterOptions));
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  delete(relEventPerson: IRelEventPerson): void {
    const modalRef = this.modalService.open(RelEventPersonDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.relEventPerson = relEventPerson;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        switchMap(() => this.loadFromBackendWithRouteInformations()),
      )
      .subscribe({
        next: (res: EntityArrayResponseType) => {
          this.onResponseSuccess(res);
        },
      });
  }

  load(): void {
    this.loadFromBackendWithRouteInformations().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(): void {
    this.handleNavigation(this.page, this.predicate, this.ascending, this.filters.filterOptions, this.currentSearch);
  }

  navigateToPage(page = this.page): void {
    this.handleNavigation(page, this.predicate, this.ascending, this.filters.filterOptions, this.currentSearch);
  }

  protected loadFromBackendWithRouteInformations(): Observable<EntityArrayResponseType> {
    return combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
      switchMap(() => this.queryBackend(this.page, this.predicate, this.ascending, this.filters.filterOptions, this.currentSearch)),
    );
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);
    const sort = (params.get(SORT) ?? data[DEFAULT_SORT_DATA]).split(',');
    this.predicate = sort[0];
    this.ascending = sort[1] === ASC;
    this.filters.initializeFromParams(params);
    if (params.has('search') && params.get('search') !== '') {
      this.currentSearch = params.get('search') as string;
      if (RelEventPersonComponent.NOT_SORTABLE_FIELDS_AFTER_SEARCH.includes(this.predicate)) {
        this.predicate = '';
      }
    }
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.relEventPeople = dataFromBody;
  }

  protected fillComponentAttributesFromResponseBody(data: IRelEventPerson[] | null): IRelEventPerson[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected queryBackend(
    page?: number,
    predicate?: string,
    ascending?: boolean,
    filterOptions?: IFilterOption[],
    currentSearch?: string,
  ): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const pageToLoad: number = page ?? 1;
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      eagerload: true,
      query: currentSearch,
      sort: this.getSortQueryParam(predicate, ascending),
    };
    filterOptions?.forEach(filterOption => {
      queryObject[filterOption.name] = filterOption.values;
    });
    if (this.currentSearch && this.currentSearch !== '') {
      return this.relEventPersonService.search(queryObject).pipe(tap(() => (this.isLoading = false)));
    } else if (this.personConnected?.id != null) {
      return this.relEventPersonService
        .findByPersonWithRelationData(this.personConnected?.id, queryObject)
        .pipe(tap(() => (this.isLoading = false)));
    } else {
      this.isLoading = false;
      return of([]) as unknown as Observable<EntityArrayResponseType>;
    }
  }

  protected handleNavigation(
    page = this.page,
    predicate?: string,
    ascending?: boolean,
    filterOptions?: IFilterOption[],
    currentSearch?: string,
  ): void {
    const queryParamsObj: any = {
      search: currentSearch,
      page,
      size: this.itemsPerPage,
      sort: this.getSortQueryParam(predicate, ascending),
    };

    filterOptions?.forEach(filterOption => {
      queryParamsObj[filterOption.nameAsQueryParam()] = filterOption.values;
    });

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }

  protected getSortQueryParam(predicate = this.predicate, ascending = this.ascending): string[] {
    const ascendingQueryParam = ascending ? ASC : DESC;
    if (predicate === '') {
      return [];
    } else {
      return [predicate + ',' + ascendingQueryParam];
    }
  }
}
