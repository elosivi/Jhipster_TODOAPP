import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IRelEventPerson } from '../rel-event-person.model';

@Component({
  standalone: true,
  selector: 'jhi-rel-event-person-detail',
  templateUrl: './rel-event-person-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class RelEventPersonDetailComponent {
  @Input() relEventPerson: IRelEventPerson | null = null;
  @Input() relEventPersonsList: IRelEventPerson[] = [];

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}
