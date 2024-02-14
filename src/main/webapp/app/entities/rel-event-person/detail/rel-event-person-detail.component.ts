import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IRelEventPerson } from '../rel-event-person.model';
import { RelEventPersonService } from '../service/rel-event-person.service';

@Component({
  standalone: true,
  selector: 'jhi-rel-event-person-detail',
  templateUrl: './rel-event-person-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class RelEventPersonDetailComponent {
  @Input() relEventPerson: IRelEventPerson | null = null;
  @Input() relEventPersonsList: IRelEventPerson[] = [];

  constructor(
    protected activatedRoute: ActivatedRoute,
    protected relEventPersonService: RelEventPersonService,
  ) {}

  ngOnInit() {
    if (this.relEventPerson && this.relEventPerson.event && this.relEventPerson.event.id != null) {
      this.relEventPerson.relEventPersonLinkedByEvent = this.relEventPerson.relEventPersonLinkedByEvent || [];
      this.loadAllRelEventPersonForAllEvent(this.relEventPerson?.event?.id);
      this.loadParticipationForCurrentUser();
    }
  }
  previousState(): void {
    window.history.back();
  }

  /**
   * For all event in relEventPersonList, load the other relEventPerson concerned by the same event.
   * In order to have the person's participation
   */
  loadAllRelEventPersonForAllEvent(EventId: number) {
    if (EventId != null || EventId != undefined) {
      this.relEventPersonService.findByEventWithRelationData(EventId).subscribe(
        response => {
          const relEventPersonListbyEvent = response.body;
          console.log(EventId, ' / IREPByEvent => ', relEventPersonListbyEvent);
          if (relEventPersonListbyEvent && Array.isArray(relEventPersonListbyEvent) && relEventPersonListbyEvent.length > 0) {
            for (const oneRelEventPerson of relEventPersonListbyEvent) {
              this.relEventPerson?.relEventPersonLinkedByEvent?.push(oneRelEventPerson);
              console.log(' this.relEventPerson ', this.relEventPerson);
            }
          }
        },
        error => {
          console.error("Erreur lors du chargement des relEventPerson pour l'événement", error);
        },
      );
    }
  }

  private loadParticipationForCurrentUser() {
    const currentUserID = 'currentUserID';
    /*
    if (this.relEventPerson?.relEventPersonLinkedByEvent) {
      const participation = this.relEventPerson.relEventPersonLinkedByEvent.find(
        person => person?.id === currentUserID
      );

      console.log('Participation for current user:', participation);
      // Do something with the participation data for the current user.
    }*/
  }
}
