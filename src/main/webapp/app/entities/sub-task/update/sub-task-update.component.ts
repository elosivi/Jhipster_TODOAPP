import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IMainTask } from 'app/entities/main-task/main-task.model';
import { MainTaskService } from 'app/entities/main-task/service/main-task.service';
import { IPerson } from 'app/entities/person/person.model';
import { PersonService } from 'app/entities/person/service/person.service';
import { IStatus } from 'app/entities/status/status.model';
import { StatusService } from 'app/entities/status/service/status.service';
import { SubTaskService } from '../service/sub-task.service';
import { ISubTask } from '../sub-task.model';
import { SubTaskFormService, SubTaskFormGroup } from './sub-task-form.service';

@Component({
  standalone: true,
  selector: 'jhi-sub-task-update',
  templateUrl: './sub-task-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SubTaskUpdateComponent implements OnInit {
  isSaving = false;
  subTask: ISubTask | null = null;

  mainTasksSharedCollection: IMainTask[] = [];
  peopleSharedCollection: IPerson[] = [];
  statusesSharedCollection: IStatus[] = [];

  editForm: SubTaskFormGroup = this.subTaskFormService.createSubTaskFormGroup();

  constructor(
    protected subTaskService: SubTaskService,
    protected subTaskFormService: SubTaskFormService,
    protected mainTaskService: MainTaskService,
    protected personService: PersonService,
    protected statusService: StatusService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareMainTask = (o1: IMainTask | null, o2: IMainTask | null): boolean => this.mainTaskService.compareMainTask(o1, o2);

  comparePerson = (o1: IPerson | null, o2: IPerson | null): boolean => this.personService.comparePerson(o1, o2);

  compareStatus = (o1: IStatus | null, o2: IStatus | null): boolean => this.statusService.compareStatus(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ subTask }) => {
      this.subTask = subTask;
      if (subTask) {
        this.updateForm(subTask);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const subTask = this.subTaskFormService.getSubTask(this.editForm);
    if (subTask.id !== null) {
      this.subscribeToSaveResponse(this.subTaskService.update(subTask));
    } else {
      this.subscribeToSaveResponse(this.subTaskService.create(subTask));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISubTask>>): void {
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

  protected updateForm(subTask: ISubTask): void {
    this.subTask = subTask;
    this.subTaskFormService.resetForm(this.editForm, subTask);

    this.mainTasksSharedCollection = this.mainTaskService.addMainTaskToCollectionIfMissing<IMainTask>(
      this.mainTasksSharedCollection,
      subTask.mainTask,
    );
    this.peopleSharedCollection = this.personService.addPersonToCollectionIfMissing<IPerson>(
      this.peopleSharedCollection,
      subTask.personDoer,
    );
    this.statusesSharedCollection = this.statusService.addStatusToCollectionIfMissing<IStatus>(
      this.statusesSharedCollection,
      subTask.status,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.mainTaskService
      .query()
      .pipe(map((res: HttpResponse<IMainTask[]>) => res.body ?? []))
      .pipe(
        map((mainTasks: IMainTask[]) =>
          this.mainTaskService.addMainTaskToCollectionIfMissing<IMainTask>(mainTasks, this.subTask?.mainTask),
        ),
      )
      .subscribe((mainTasks: IMainTask[]) => (this.mainTasksSharedCollection = mainTasks));

    this.personService
      .query()
      .pipe(map((res: HttpResponse<IPerson[]>) => res.body ?? []))
      .pipe(map((people: IPerson[]) => this.personService.addPersonToCollectionIfMissing<IPerson>(people, this.subTask?.personDoer)))
      .subscribe((people: IPerson[]) => (this.peopleSharedCollection = people));

    this.statusService
      .query()
      .pipe(map((res: HttpResponse<IStatus[]>) => res.body ?? []))
      .pipe(map((statuses: IStatus[]) => this.statusService.addStatusToCollectionIfMissing<IStatus>(statuses, this.subTask?.status)))
      .subscribe((statuses: IStatus[]) => (this.statusesSharedCollection = statuses));
  }
}
