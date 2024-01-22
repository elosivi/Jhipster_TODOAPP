import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IStatus } from '../status.model';
import { StatusService } from '../service/status.service';

@Component({
  standalone: true,
  templateUrl: './status-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class StatusDeleteDialogComponent {
  status?: IStatus;

  constructor(
    protected statusService: StatusService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.statusService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
