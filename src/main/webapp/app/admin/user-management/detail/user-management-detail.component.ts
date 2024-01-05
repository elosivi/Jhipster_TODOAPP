import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import SharedModule from 'app/shared/shared.module';
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { LOCALE_ID } from '@angular/core';
registerLocaleData(localeFr);
import { User } from '../user-management.model';
import { IPerson } from '../../../entities/person/person.model';
import { HttpResponse } from '@angular/common/http';
import { PersonService } from '../../../entities/person/service/person.service';

@Component({
  standalone: true,
  selector: 'jhi-user-mgmt-detail',
  templateUrl: './user-management-detail.component.html',
  imports: [SharedModule],
})
export default class UserManagementDetailComponent implements OnInit {
  user: User | null = null;
  associatedPerson: IPerson | null = null;

  constructor(
    private route: ActivatedRoute,
    protected personService: PersonService,
  ) {}

  ngOnInit(): void {
    this.route.data.subscribe(({ user }) => {
      this.user = user;
    });
    if (this.user != null) this.loadAssociatedPerson(this.user.id);
  }

  private loadAssociatedPerson(userId: number | null) {
    if (userId != null) {
      this.personService.findByUserAssociated(userId).subscribe((response: HttpResponse<IPerson>) => {
        const content = response.body as IPerson;
        if (content) {
          this.associatedPerson = content;
        } else {
          console.log("ce user n'est associé à aucun participant");
        }
      });
    }
  }
}
