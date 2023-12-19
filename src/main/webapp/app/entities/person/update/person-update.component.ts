import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IHierarchy } from 'app/entities/hierarchy/hierarchy.model';
import { HierarchyService } from 'app/entities/hierarchy/service/hierarchy.service';
import { PersonService } from '../service/person.service';
import { IPerson } from '../person.model';
import { PersonFormService, PersonFormGroup } from './person-form.service';

@Component({
  standalone: true,
  selector: 'jhi-person-update',
  templateUrl: './person-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PersonUpdateComponent implements OnInit {
  isSaving = false;
  person: IPerson | null = null;

  usersSharedCollection: IUser[] = [];
  hierarchiesCollection: IHierarchy[] = [];

  editForm: PersonFormGroup = this.personFormService.createPersonFormGroup();

  constructor(
    protected personService: PersonService,
    protected personFormService: PersonFormService,
    protected userService: UserService,
    protected hierarchyService: HierarchyService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareHierarchy = (o1: IHierarchy | null, o2: IHierarchy | null): boolean => this.hierarchyService.compareHierarchy(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ person }) => {
      this.person = person;
      if (person) {
        this.updateForm(person);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const person = this.personFormService.getPerson(this.editForm);
    if (person.id !== null) {
      this.subscribeToSaveResponse(this.personService.update(person));
    } else {
      this.subscribeToSaveResponse(this.personService.create(person));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPerson>>): void {
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

  protected updateForm(person: IPerson): void {
    this.person = person;
    this.personFormService.resetForm(this.editForm, person);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, person.user);
    this.hierarchiesCollection = this.hierarchyService.addHierarchyToCollectionIfMissing<IHierarchy>(
      this.hierarchiesCollection,
      person.hierarchy,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.person?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.hierarchyService
      .query({ filter: 'person-is-null' })
      .pipe(map((res: HttpResponse<IHierarchy[]>) => res.body ?? []))
      .pipe(
        map((hierarchies: IHierarchy[]) =>
          this.hierarchyService.addHierarchyToCollectionIfMissing<IHierarchy>(hierarchies, this.person?.hierarchy),
        ),
      )
      .subscribe((hierarchies: IHierarchy[]) => (this.hierarchiesCollection = hierarchies));
  }
}
