import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISubTask } from '../sub-task.model';
import { SubTaskService } from '../service/sub-task.service';

@Component({
  standalone: true,
  templateUrl: './sub-task-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SubTaskDeleteDialogComponent {
  subTask?: ISubTask;

  constructor(
    protected subTaskService: SubTaskService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.subTaskService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
