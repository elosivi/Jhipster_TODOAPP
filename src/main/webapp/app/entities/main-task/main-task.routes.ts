import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { MainTaskComponent } from './list/main-task.component';
import { MainTaskDetailComponent } from './detail/main-task-detail.component';
import { MainTaskUpdateComponent } from './update/main-task-update.component';
import MainTaskResolve from './route/main-task-routing-resolve.service';

const mainTaskRoute: Routes = [
  {
    path: '',
    component: MainTaskComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MainTaskDetailComponent,
    resolve: {
      mainTask: MainTaskResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MainTaskUpdateComponent,
    resolve: {
      mainTask: MainTaskResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MainTaskUpdateComponent,
    resolve: {
      mainTask: MainTaskResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default mainTaskRoute;
