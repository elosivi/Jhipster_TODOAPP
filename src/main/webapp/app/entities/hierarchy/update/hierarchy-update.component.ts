import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IHierarchy } from '../hierarchy.model';
import { HierarchyService } from '../service/hierarchy.service';
import { HierarchyFormService, HierarchyFormGroup } from './hierarchy-form.service';

@Component({
  standalone: true,
  selector: 'jhi-hierarchy-update',
  templateUrl: './hierarchy-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class HierarchyUpdateComponent implements OnInit {
  isSaving = false;
  hierarchy: IHierarchy | null = null;

  editForm: HierarchyFormGroup = this.hierarchyFormService.createHierarchyFormGroup();

  constructor(
    protected hierarchyService: HierarchyService,
    protected hierarchyFormService: HierarchyFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ hierarchy }) => {
      this.hierarchy = hierarchy;
      if (hierarchy) {
        this.updateForm(hierarchy);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const hierarchy = this.hierarchyFormService.getHierarchy(this.editForm);
    if (hierarchy.id !== null) {
      this.subscribeToSaveResponse(this.hierarchyService.update(hierarchy));
    } else {
      this.subscribeToSaveResponse(this.hierarchyService.create(hierarchy));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IHierarchy>>): void {
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

  protected updateForm(hierarchy: IHierarchy): void {
    this.hierarchy = hierarchy;
    this.hierarchyFormService.resetForm(this.editForm, hierarchy);
  }
}
