import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { EventTypeComponent } from './list/event-type.component';
import { EventTypeDetailComponent } from './detail/event-type-detail.component';
import { EventTypeUpdateComponent } from './update/event-type-update.component';
import EventTypeResolve from './route/event-type-routing-resolve.service';

const eventTypeRoute: Routes = [
  {
    path: '',
    component: EventTypeComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EventTypeDetailComponent,
    resolve: {
      eventType: EventTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EventTypeUpdateComponent,
    resolve: {
      eventType: EventTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EventTypeUpdateComponent,
    resolve: {
      eventType: EventTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default eventTypeRoute;
