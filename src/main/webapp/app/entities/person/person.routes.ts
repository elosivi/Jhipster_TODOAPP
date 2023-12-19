import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { PersonComponent } from './list/person.component';
import { PersonDetailComponent } from './detail/person-detail.component';
import { PersonUpdateComponent } from './update/person-update.component';
import PersonResolve from './route/person-routing-resolve.service';

const personRoute: Routes = [
  {
    path: '',
    component: PersonComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PersonDetailComponent,
    resolve: {
      person: PersonResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PersonUpdateComponent,
    resolve: {
      person: PersonResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PersonUpdateComponent,
    resolve: {
      person: PersonResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default personRoute;
