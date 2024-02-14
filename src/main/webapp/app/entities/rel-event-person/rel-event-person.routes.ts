import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { RelEventPersonComponent } from './list/rel-event-person.component';
import { RelEventPersonDetailComponent } from './detail/rel-event-person-detail.component';
import { RelEventPersonUpdateComponent } from './create/rel-event-person-update.component';
import RelEventPersonResolve from './route/rel-event-person-routing-resolve.service';

const relEventPersonRoute: Routes = [
  {
    path: '',
    component: RelEventPersonComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RelEventPersonDetailComponent,
    resolve: {
      relEventPerson: RelEventPersonResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RelEventPersonUpdateComponent,
    resolve: {
      relEventPerson: RelEventPersonResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RelEventPersonUpdateComponent,
    resolve: {
      relEventPerson: RelEventPersonResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default relEventPersonRoute;
