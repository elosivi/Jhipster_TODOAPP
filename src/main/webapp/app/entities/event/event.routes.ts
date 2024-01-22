import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { EventComponent } from './list/event.component';
import { EventDetailComponent } from './detail/event-detail.component';
import { EventUpdateComponent } from './update/event-update.component';
import EventResolve from './route/event-routing-resolve.service';

const eventRoute: Routes = [
  {
    path: '',
    component: EventComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EventDetailComponent,
    resolve: {
      event: EventResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EventUpdateComponent,
    resolve: {
      event: EventResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EventUpdateComponent,
    resolve: {
      event: EventResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default eventRoute;
