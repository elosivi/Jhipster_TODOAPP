import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { UserComponent } from './list/user.component';
//import { UserDetailComponent } from './detail/user-detail.component';
//import { UserUpdateComponent } from './update/user-update.component';
//import UserResolve from './route/user-routing-resolve.service';

const userRoute: Routes = [
  {
    path: '',
    component: UserComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  /*{
    path: ':id/view',
    component: UserDetailComponent,
    resolve: {
      user: UserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UserUpdateComponent,
    resolve: {
      user: UserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UserUpdateComponent,
    resolve: {
      user: UserResolve,
    },
    canActivate: [UserRouteAccessService],
  },*/
];

export default userRoute;
