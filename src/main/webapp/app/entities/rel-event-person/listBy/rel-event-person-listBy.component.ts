import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { combineLatest, filter, forkJoin, Observable, of, Subject, switchMap, takeUntil, tap } from 'rxjs';
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
import { UserService } from '../../user/service/user.service';
import { Account } from '../../../core/auth/account.model';
import { catchError, map } from 'rxjs/operators';

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

  account: Account | null = null;
  userConnected: IUser | null = null;
  personConnected: IPerson | null = null;
  userConnectedHasAPersonLinked: boolean | undefined;

  relEventPersonList?: IRelEventPerson[];
  isLoading = false;

  private readonly destroy$ = new Subject<void>();
  private readonly destroyUser$ = new Subject<void>();

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
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    // call from home page => return events participation for the person connected
    this.loadRelEventPersonForHomePage();
    this.filters.filterChanges.subscribe(filterOptions => this.handleNavigation(1, this.predicate, this.ascending, filterOptions));
  }

  /*************************************************
   LOAD LIST BY PERSON
  /*************************************************/
  loadAccountService() {
    if (this.account == null) {
      this.accountService.getAuthenticationState().subscribe(account => {
        this.account = account;
      });
    }
  }

  /**
   * To return only the events that concern the connected user we need to know to which "person" the user is linked.
   */
  loadPersonLinkedToTheConnectedUser(): void {
    if (this.account?.id != null) {
      this.personService
        .findByUserAssociated(this.account.id)
        .pipe(takeUntil(this.destroyUser$))
        .subscribe(
          (person: EntityResponseType) => {
            this.personConnected = person.body;
            this.userConnectedHasAPersonLinked = true;
            this.cdr.detectChanges();
            this.load();
          },
          error => {
            console.log("Impossible de charger le participant lié à l'utilisateur connecté =>", error);
            this.userConnectedHasAPersonLinked = false;
          },
        );
    }
  }
  /**
   * load data about user/person connected for home page wich return only events concerned by the account logged
   */
  loadRelEventPersonForHomePage() {
    if (this.account == null) {
      this.loadAccountService();
    }
    this.loadPersonLinkedToTheConnectedUser();
    if (this.personConnected) {
      this.userConnectedHasAPersonLinked = true;
    } else {
      this.userConnectedHasAPersonLinked = false;
    }
  }

  /*************************************************
   LOAD OTHERS RELEVENTPERSON BY EVENT
   /*************************************************/

  /**
   * For all event in relEventPersonList, load the other relEventPerson concerned bythe same event.
   * In order to have the person's participation
   */
  loadAllRelEventPersonForAllEvent() {
    if (this.relEventPersonList != null && this.relEventPersonList.length > 0) {
      this.relEventPersonList.forEach(relEventPerson => {
        this.loadRelEventPersonListByEvent(relEventPerson?.event?.id, relEventPerson.id);
        console.log('relEventPersonListNEW => ', this.relEventPersonList);
      });
    }
  }

  /**
   * To display the others person's participation and hierarchy we need to load all releventperson for each event
   * @param EventId
   */
  loadRelEventPersonListByEvent(EventId: number | undefined, relEventPersonId: number | undefined): void {
    // load the other releventperson for the event in param
    if (EventId != null) {
      this.relEventPersonService.findByEventWithRelationData(EventId).subscribe(
        response => {
          const relEventPersonListbyEvent = response.body;
          console.log(EventId, ' / IREPByEvent => ', relEventPersonListbyEvent);

          const indexToUpdate = this.relEventPersonList?.findIndex(relEventPerson => relEventPerson.id === relEventPersonId);

          if (indexToUpdate != null && indexToUpdate !== -1 && this.relEventPersonList != null) {
            // Mettre à jour relEventPersonLinkedByEvent avec les données chargées
            this.relEventPersonList[indexToUpdate].relEventPersonLinkedByEvent = relEventPersonListbyEvent;
            console.log(relEventPersonId, ' / this.relEventPersonList[indexToUpdate] => ', this.relEventPersonList[indexToUpdate]);
          } else {
            console.error(`RelEventPerson avec l'ID ${relEventPersonId} non trouvé dans this.relEventPersonList.`);
          }
        },
        error => {
          console.error("Erreur lors du chargement des relEventPerson pour l'événement", error);
        },
      );
    }
  }

  /*************************************************
   LOAD LIST OF EVENTS BY PERSON / FOR HOME PAGE
   /*************************************************/

  /**
   * Load the rel event person concerned by the person connected
   */
  load(): void {
    this.loadFromBackendWithRouteInformations().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
        // console.log("relEventPersonList => ", JSON.stringify(this.relEventPersonList, null, 2));
        // load other releventperson by event to have all the person's participation
        this.loadAllRelEventPersonForAllEvent();
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
      tap(([params, data]) => {
        this.fillComponentAttributeFromRoute(params, data);
      }),
      switchMap(() => {
        return this.queryBackend(this.page, this.predicate, this.ascending, this.filters.filterOptions, this.currentSearch);
      }),
    );
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);

    const sortParam = params.get(SORT) ?? data[DEFAULT_SORT_DATA];
    const sort = sortParam ? sortParam.split(',') : [];

    if (sort.length >= 2) {
      this.predicate = sort[0];
      this.ascending = sort[1] === ASC;
    } else {
      // if no sort or invalid sort      this.predicate = '';
      this.ascending = true;
    }

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
    this.relEventPersonList = dataFromBody;
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
