import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMainTask } from '../main-task.model';
import { MainTaskService } from '../service/main-task.service';

@Component({
  standalone: true,
  templateUrl: './main-task-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MainTaskDeleteDialogComponent {
  mainTask?: IMainTask;

  constructor(
    protected mainTaskService: MainTaskService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.mainTaskService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
