import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { combineLatest, filter, Observable, switchMap, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { SortDirective, SortByDirective } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { FormsModule } from '@angular/forms';
import { ASC, DESC, SORT, ITEM_DELETED_EVENT, DEFAULT_SORT_DATA } from 'app/config/navigation.constants';
import { SortService } from 'app/shared/sort/sort.service';
import { IEventType } from '../event-type.model';
import { EntityArrayResponseType, EventTypeService } from '../service/event-type.service';
import { EventTypeDeleteDialogComponent } from '../delete/event-type-delete-dialog.component';

@Component({
  standalone: true,
  selector: 'jhi-event-type',
  templateUrl: './event-type.component.html',
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
  ],
})
export class EventTypeComponent implements OnInit {
  private static readonly NOT_SORTABLE_FIELDS_AFTER_SEARCH = ['label', 'description', 'duration'];

  eventTypes?: IEventType[];
  isLoading = false;

  predicate = 'id';
  ascending = true;
  currentSearch = '';

  constructor(
    protected eventTypeService: EventTypeService,
    protected activatedRoute: ActivatedRoute,
    public router: Router,
    protected sortService: SortService,
    protected modalService: NgbModal,
  ) {}

  trackId = (_index: number, item: IEventType): number => this.eventTypeService.getEventTypeIdentifier(item);

  search(query: string): void {
    if (query && EventTypeComponent.NOT_SORTABLE_FIELDS_AFTER_SEARCH.includes(this.predicate)) {
      this.predicate = 'id';
      this.ascending = true;
    }
    this.currentSearch = query;
    this.navigateToWithComponentValues();
  }

  ngOnInit(): void {
    this.load();
  }

  delete(eventType: IEventType): void {
    const modalRef = this.modalService.open(EventTypeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.eventType = eventType;
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
    this.handleNavigation(this.predicate, this.ascending, this.currentSearch);
  }

  protected loadFromBackendWithRouteInformations(): Observable<EntityArrayResponseType> {
    return combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data]).pipe(
      tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
      switchMap(() => this.queryBackend(this.predicate, this.ascending, this.currentSearch)),
    );
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const sort = (params.get(SORT) ?? data[DEFAULT_SORT_DATA]).split(',');
    this.predicate = sort[0];
    this.ascending = sort[1] === ASC;
    if (params.has('search') && params.get('search') !== '') {
      this.currentSearch = params.get('search') as string;
      if (EventTypeComponent.NOT_SORTABLE_FIELDS_AFTER_SEARCH.includes(this.predicate)) {
        this.predicate = '';
      }
    }
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.eventTypes = this.refineData(dataFromBody);
  }

  protected refineData(data: IEventType[]): IEventType[] {
    return data.sort(this.sortService.startSort(this.predicate, this.ascending ? 1 : -1));
  }

  protected fillComponentAttributesFromResponseBody(data: IEventType[] | null): IEventType[] {
    return data ?? [];
  }

  protected queryBackend(predicate?: string, ascending?: boolean, currentSearch?: string): Observable<EntityArrayResponseType> {
    this.isLoading = true;
    const queryObject: any = {
      query: currentSearch,
      sort: this.getSortQueryParam(predicate, ascending),
    };
    if (this.currentSearch && this.currentSearch !== '') {
      return this.eventTypeService.search(queryObject).pipe(tap(() => (this.isLoading = false)));
    } else {
      return this.eventTypeService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
    }
  }

  protected handleNavigation(predicate?: string, ascending?: boolean, currentSearch?: string): void {
    const queryParamsObj = {
      search: currentSearch,
      sort: this.getSortQueryParam(predicate, ascending),
    };

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
