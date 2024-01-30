import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IEventType } from '../event-type.model';
import { EventTypeService } from '../service/event-type.service';
import { EventTypeFormService, EventTypeFormGroup } from './event-type-form.service';

@Component({
  standalone: true,
  selector: 'jhi-event-type-update',
  templateUrl: './event-type-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class EventTypeUpdateComponent implements OnInit {
  isSaving = false;
  eventType: IEventType | null = null;

  editForm: EventTypeFormGroup = this.eventTypeFormService.createEventTypeFormGroup();

  durationInSeconds: number | undefined;
  constructor(
    protected eventTypeService: EventTypeService,
    protected eventTypeFormService: EventTypeFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ eventType }) => {
      this.eventType = eventType;
      if (eventType) {
        this.durationInSeconds = eventType.duration;
        this.updateForm(eventType);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    this.convertDaysInSeconds();
    const eventType = this.eventTypeFormService.getEventType(this.editForm);
    if (eventType.id !== null) {
      this.subscribeToSaveResponse(this.eventTypeService.update(eventType));
    } else {
      this.subscribeToSaveResponse(this.eventTypeService.create(eventType));
    }
  }
  convertDaysInSeconds(): void {
    const nbDays = this.editForm.controls['duration'].value;

    if (nbDays !== null && nbDays !== undefined) {
      const nbSeconds = nbDays * (60 * 60 * 24);
      // Vous pouvez afficher ou utiliser dureeEnSecondes comme nécessaire
      console.log('Durée en secondes :', nbSeconds);
      this.editForm.controls['duration'].setValue(nbSeconds);
    }
  }

  convertSecondsInDays() {
    return this.durationInSeconds !== undefined ? this.durationInSeconds / (60 * 60 * 24) : undefined;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEventType>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(eventType: IEventType): void {
    this.eventType = eventType;
    this.eventTypeFormService.resetForm(this.editForm, eventType);
  }
}
