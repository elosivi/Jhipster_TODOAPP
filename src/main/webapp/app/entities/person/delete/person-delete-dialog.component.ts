import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPerson } from '../person.model';
import { PersonService } from '../service/person.service';

@Component({
  standalone: true,
  templateUrl: './person-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PersonDeleteDialogComponent {
  person?: IPerson;

  constructor(
    protected personService: PersonService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.personService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
