import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IStatus } from '../status.model';
import { StatusService } from '../service/status.service';
import { StatusFormService, StatusFormGroup } from './status-form.service';

@Component({
  standalone: true,
  selector: 'jhi-status-update',
  templateUrl: './status-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class StatusUpdateComponent implements OnInit {
  isSaving = false;
  status: IStatus | null = null;

  editForm: StatusFormGroup = this.statusFormService.createStatusFormGroup();

  constructor(
    protected statusService: StatusService,
    protected statusFormService: StatusFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ status }) => {
      this.status = status;
      if (status) {
        this.updateForm(status);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const status = this.statusFormService.getStatus(this.editForm);
    if (status.id !== null) {
      this.subscribeToSaveResponse(this.statusService.update(status));
    } else {
      this.subscribeToSaveResponse(this.statusService.create(status));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStatus>>): void {
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

  protected updateForm(status: IStatus): void {
    this.status = status;
    this.statusFormService.resetForm(this.editForm, status);
  }
}
