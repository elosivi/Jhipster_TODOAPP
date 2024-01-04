import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { LANGUAGES } from 'app/config/language.constants';
import { IUser } from '../user-management.model';
import { UserManagementService } from '../service/user-management.service';
import { IPerson } from '../../../entities/person/person.model';
import { PersonService } from '../../../entities/person/service/person.service';
import { PersonFormService } from '../../../entities/person/update/person-form.service';
import { IHierarchy } from '../../../entities/hierarchy/hierarchy.model';
import { map } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';
import { isPresent } from '../../../core/util/operators';
import { getUserIdentifier } from '../../../entities/user/user.model';
import { tap } from 'rxjs';
import { EntityArrayResponseType } from '../../../entities/event/service/event.service';

const userTemplate = {} as IUser;

const newUser: IUser = {
  langKey: 'fr',
  activated: true,
} as IUser;

@Component({
  standalone: true,
  selector: 'jhi-user-mgmt-update',
  templateUrl: './user-management-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export default class UserManagementUpdateComponent implements OnInit {
  languages = LANGUAGES;
  authorities: string[] = [];
  personsCollection: IPerson[] = [];
  selectedPerson: IPerson | null = null;
  isSaving = false;
  isLinked: boolean = false; // indicates if the user must be linked to a participant (Person)
  editForm = new FormGroup({
    id: new FormControl(userTemplate.id),
    login: new FormControl(userTemplate.login, {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(50),
        Validators.pattern('^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$'),
      ],
    }),
    firstName: new FormControl(userTemplate.firstName, { validators: [Validators.maxLength(50)] }),
    lastName: new FormControl(userTemplate.lastName, { validators: [Validators.maxLength(50)] }),
    email: new FormControl(userTemplate.email, {
      nonNullable: true,
      validators: [Validators.minLength(5), Validators.maxLength(254), Validators.email],
    }),
    activated: new FormControl(userTemplate.activated, { nonNullable: true }),
    langKey: new FormControl(userTemplate.langKey, { nonNullable: true }),
    authorities: new FormControl(userTemplate.authorities, { nonNullable: true }),
    person: new FormControl(null),
  });

  constructor(
    private userService: UserManagementService,
    private route: ActivatedRoute,
    private router: Router,
    protected personService: PersonService,
  ) {}

  comparePerson = (o1: IPerson | null, o2: IPerson | null): boolean => this.personService.comparePerson(o1, o2);

  ngOnInit(): void {
    this.route.data.subscribe(({ user }) => {
      if (user) {
        this.editForm.reset(user);
      } else {
        this.editForm.reset(newUser);
      }
    });
    this.userService.authorities().subscribe(authorities => (this.authorities = authorities));

    // load the persons list to associate with user and manage the link
    this.loadPersons();
    this.isAPersonIsSelected();
    this.loadThePersonAssociated(this.editForm?.get('id')?.value ?? null);
  }

  loadPersons(): void {
    this.personService.queryPersonsWithUsers().subscribe((response: HttpResponse<IPerson[]>) => {
      const content = (response.body as any)?.content as IPerson[];
      if (content) {
        this.personsCollection = content;
      } else {
        console.warn('Aucun participant récupéré via le service queryPersonsWithUsers()');
      }
    });
  }

  /**
   * Load the person allready associated with the current user /update function
   * @param userId
   */
  loadThePersonAssociated(userId: number | null) {
    if (userId != null) {
      this.personService.findByUserAssociated(userId).subscribe((response: HttpResponse<IPerson>) => {
        console.log('******body');
        console.log(response.body);
        const content = response.body as IPerson;
        if (content) {
          this.selectedPerson = content;
        } else {
          console.log("ce user n'est associé à aucun participant");
        }
      });
    }
  }
  previousState(): void {
    window.history.back();
  }

  /**
   * listen the select person
   */
  isAPersonIsSelected() {
    this.editForm.get('person')?.valueChanges.subscribe(selectedPerson => {
      console.log('Person selected:', selectedPerson);
      this.isLinked = selectedPerson !== null;
    });
  }

  /**
   * To associate the new user with a new person entity
   * this method save the user and redirects to person entity creation
   */
  associateWithNewPerson() {
    this.save('association');
  }

  /**
   * Associate the user with a person allready existing
   */
  associateWithExistingPerson() {
    const selectedPerson: unknown = this.editForm.get('person')?.value;
    if (selectedPerson !== null && selectedPerson !== undefined) {
      this.isLinked = true;
      this.selectedPerson = selectedPerson as IPerson;
      return this.selectedPerson.id;
    }
    return null;
  }

  /**
   * Save the new user with 3 options:
   * 1/ update the actually user with or not association with a person
   * 2/ create the user, then create the person and link the two
   * 3/ create the user with or not association with an existing person
   * @param association
   */
  save(association?: string): void {
    this.isSaving = true;
    const user = this.editForm.getRawValue();
    //load the person associated if there is one
    const selectedPersonId = this.associateWithExistingPerson();

    // update an user + link or not with a person
    if (user.id !== null && user.id > 0) {
      this.userService.update(user).subscribe(
        (updatedUser: IUser) => {
          //if api update user and a person is selected, link the user with it
          if (updatedUser && this.selectedPerson) {
            this.linkExistingPerson(updatedUser.id!, selectedPersonId!);
          } else if (this.selectedPerson == null) {
            console.warn('le user est bien enregistré, sans lien avec un participant de type Person ');
            this.onSaveSuccess();
          }
        },
        () => this.onSaveError(),
      );

      //creation of user + association with a new person
    } else if (association != null) {
      this.userService.create(user).subscribe({
        next: () => this.onSaveSuccess(association),
        error: () => this.onSaveError(),
      });

      // creation of user + link or not with a person
    } else {
      this.userService.create(user).subscribe(
        (createdUser: IUser) => {
          //if api create user and a person is selected, link the user with it
          if (createdUser && this.selectedPerson) {
            this.linkExistingPerson(createdUser.id!, selectedPersonId!);
          } else if (this.selectedPerson == null) {
            console.warn('le user est bien enregistré, sans lien avec un participant de type Person ');
            this.onSaveSuccess();
          }
        },
        () => this.onSaveError(),
      );
    }
  }

  private linkExistingPerson(userId: number, personId: number) {
    this.personService.associateUserWithPerson(userId, personId).subscribe(
      () => {
        // Le lien entre le user et la person a été établi avec succès
        console.log('Lien établi entre le user et la person');
        this.onSaveSuccess();
      },
      () => {
        // Gestion des erreurs lors de l'établissement du lien
        console.error("Erreur lors de l'établissement du lien entre le user et la person");
        () => this.onSaveError();
      },
    );
  }
  private onSaveSuccess(association?: string): void {
    this.isSaving = false;
    if (association == null) {
      this.previousState();
    } else {
      this.router.navigate(['/person/new']);
    }
  }

  private onSaveError(): void {
    this.isSaving = false;
  }
}
