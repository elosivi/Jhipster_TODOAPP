import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IHierarchy } from '../hierarchy.model';
import { HierarchyService } from '../service/hierarchy.service';

@Component({
  standalone: true,
  templateUrl: './hierarchy-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class HierarchyDeleteDialogComponent {
  hierarchy?: IHierarchy;

  constructor(
    protected hierarchyService: HierarchyService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.hierarchyService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
