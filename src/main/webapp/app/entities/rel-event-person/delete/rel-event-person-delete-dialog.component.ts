import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IRelEventPerson } from '../rel-event-person.model';
import { RelEventPersonService } from '../service/rel-event-person.service';

@Component({
  standalone: true,
  templateUrl: './rel-event-person-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class RelEventPersonDeleteDialogComponent {
  relEventPerson?: IRelEventPerson;

  constructor(
    protected relEventPersonService: RelEventPersonService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.relEventPersonService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
