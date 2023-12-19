import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { StatusComponent } from './list/status.component';
import { StatusDetailComponent } from './detail/status-detail.component';
import { StatusUpdateComponent } from './update/status-update.component';
import StatusResolve from './route/status-routing-resolve.service';

const statusRoute: Routes = [
  {
    path: '',
    component: StatusComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: StatusDetailComponent,
    resolve: {
      status: StatusResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: StatusUpdateComponent,
    resolve: {
      status: StatusResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: StatusUpdateComponent,
    resolve: {
      status: StatusResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default statusRoute;
