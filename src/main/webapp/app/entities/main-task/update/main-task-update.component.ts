import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { IPerson } from 'app/entities/person/person.model';
import { PersonService } from 'app/entities/person/service/person.service';
import { IStatus } from 'app/entities/status/status.model';
import { StatusService } from 'app/entities/status/service/status.service';
import { MainTaskService } from '../service/main-task.service';
import { IMainTask } from '../main-task.model';
import { MainTaskFormService, MainTaskFormGroup } from './main-task-form.service';

@Component({
  standalone: true,
  selector: 'jhi-main-task-update',
  templateUrl: './main-task-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class MainTaskUpdateComponent implements OnInit {
  isSaving = false;
  mainTask: IMainTask | null = null;

  categoriesSharedCollection: ICategory[] = [];
  peopleSharedCollection: IPerson[] = [];
  statusesSharedCollection: IStatus[] = [];

  editForm: MainTaskFormGroup = this.mainTaskFormService.createMainTaskFormGroup();

  constructor(
    protected mainTaskService: MainTaskService,
    protected mainTaskFormService: MainTaskFormService,
    protected categoryService: CategoryService,
    protected personService: PersonService,
    protected statusService: StatusService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareCategory = (o1: ICategory | null, o2: ICategory | null): boolean => this.categoryService.compareCategory(o1, o2);

  comparePerson = (o1: IPerson | null, o2: IPerson | null): boolean => this.personService.comparePerson(o1, o2);

  compareStatus = (o1: IStatus | null, o2: IStatus | null): boolean => this.statusService.compareStatus(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ mainTask }) => {
      this.mainTask = mainTask;
      if (mainTask) {
        this.updateForm(mainTask);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const mainTask = this.mainTaskFormService.getMainTask(this.editForm);
    if (mainTask.id !== null) {
      this.subscribeToSaveResponse(this.mainTaskService.update(mainTask));
    } else {
      this.subscribeToSaveResponse(this.mainTaskService.create(mainTask));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMainTask>>): void {
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

  protected updateForm(mainTask: IMainTask): void {
    this.mainTask = mainTask;
    this.mainTaskFormService.resetForm(this.editForm, mainTask);

    this.categoriesSharedCollection = this.categoryService.addCategoryToCollectionIfMissing<ICategory>(
      this.categoriesSharedCollection,
      mainTask.category,
    );
    this.peopleSharedCollection = this.personService.addPersonToCollectionIfMissing<IPerson>(
      this.peopleSharedCollection,
      mainTask.personOwner,
    );
    this.statusesSharedCollection = this.statusService.addStatusToCollectionIfMissing<IStatus>(
      this.statusesSharedCollection,
      mainTask.status,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.categoryService
      .query()
      .pipe(map((res: HttpResponse<ICategory[]>) => res.body ?? []))
      .pipe(
        map((categories: ICategory[]) =>
          this.categoryService.addCategoryToCollectionIfMissing<ICategory>(categories, this.mainTask?.category),
        ),
      )
      .subscribe((categories: ICategory[]) => (this.categoriesSharedCollection = categories));

    this.personService
      .query()
      .pipe(map((res: HttpResponse<IPerson[]>) => res.body ?? []))
      .pipe(map((people: IPerson[]) => this.personService.addPersonToCollectionIfMissing<IPerson>(people, this.mainTask?.personOwner)))
      .subscribe((people: IPerson[]) => (this.peopleSharedCollection = people));

    this.statusService
      .query()
      .pipe(map((res: HttpResponse<IStatus[]>) => res.body ?? []))
      .pipe(map((statuses: IStatus[]) => this.statusService.addStatusToCollectionIfMissing<IStatus>(statuses, this.mainTask?.status)))
      .subscribe((statuses: IStatus[]) => (this.statusesSharedCollection = statuses));
  }
}
