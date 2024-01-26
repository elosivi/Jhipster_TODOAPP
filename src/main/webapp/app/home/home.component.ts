import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Observable, Subject, of } from 'rxjs';
import { catchError, map, takeUntil } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { IUser } from 'app/admin/user-management/user-management.model';
import { IPerson } from 'app/entities/person/person.model';
import { EntityResponseType, PersonService } from 'app/entities/person/service/person.service';
import { UserService } from 'app/entities/user/service/user.service';
import { RelEventPersonComponent } from '../entities/rel-event-person/listBy/rel-event-person-listBy.component';

@Component({
  standalone: true,
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  imports: [SharedModule, RouterModule, RelEventPersonComponent],
})
export default class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  userConnected: IUser | null = null;
  personConnected: IPerson | null = null;
  userConnectedHasAPersonLinked: boolean | undefined;

  private readonly destroy$ = new Subject<void>();
  private readonly destroyUser$ = new Subject<void>();

  constructor(
    private accountService: AccountService,
    protected personService: PersonService,
    protected userService: UserService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));

    if (this.account == null) {
      this.loadAccountService();
    } else {
      this.loadPersonLinkedToTheConnectedUser();
    }
  }

  loadAccountService() {
    if (this.account == null) {
      this.accountService.getAuthenticationState().subscribe(account => {
        // `account` contient le compte actuellement connecté (ou null si non connecté)
        this.account = account;
        console.log('Compte connecté :', account);
        this.loadPersonLinkedToTheConnectedUser();
      });
    }
  }

  /**
   * We need to know if the connected account is llinked to a person
   * If yes : load table of events to which the person is linked.
   */

  loadPersonLinkedToTheConnectedUser(): void {
    this.getUserId()
      .pipe(takeUntil(this.destroyUser$))
      .subscribe((user: IUser | null) => {
        if (user != null && user.id != null) {
          this.personService
            .findByUserAssociated(user.id)
            .pipe(takeUntil(this.destroyUser$))
            .subscribe(
              (person: EntityResponseType) => {
                this.personConnected = person.body;
                this.userConnectedHasAPersonLinked = true;
              },
              () => {
                console.log("Impossible de charger le participant lié à l'utilisateur connecté");
                this.userConnectedHasAPersonLinked = false;
              },
            );
        }
      });
  }

  getUserId(): Observable<IUser | null> {
    if (this.account != null && this.account.login !== '') {
      return this.userService.findByUserByLogin(this.account.login).pipe(
        takeUntil(this.destroy$),
        map((user: any) => {
          if (user && typeof user === 'object') {
            this.userConnected = user.body as IUser;
            return this.userConnected;
          } else {
            return null;
          }
        }),
        catchError(() => {
          console.log("Impossible de charger le participant lié à l'utilisateur connecté");
          return of(null);
        }),
      );
    }
    return of(null);
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.destroyUser$.next();
    this.destroyUser$.complete();
  }
}
