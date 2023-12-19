import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { SubTaskComponent } from './list/sub-task.component';
import { SubTaskDetailComponent } from './detail/sub-task-detail.component';
import { SubTaskUpdateComponent } from './update/sub-task-update.component';
import SubTaskResolve from './route/sub-task-routing-resolve.service';

const subTaskRoute: Routes = [
  {
    path: '',
    component: SubTaskComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SubTaskDetailComponent,
    resolve: {
      subTask: SubTaskResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SubTaskUpdateComponent,
    resolve: {
      subTask: SubTaskResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SubTaskUpdateComponent,
    resolve: {
      subTask: SubTaskResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default subTaskRoute;
