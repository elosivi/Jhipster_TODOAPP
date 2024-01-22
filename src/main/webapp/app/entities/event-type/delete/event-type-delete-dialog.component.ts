import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IEventType } from '../event-type.model';
import { EventTypeService } from '../service/event-type.service';

@Component({
  standalone: true,
  templateUrl: './event-type-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class EventTypeDeleteDialogComponent {
  eventType?: IEventType;

  constructor(
    protected eventTypeService: EventTypeService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.eventTypeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
