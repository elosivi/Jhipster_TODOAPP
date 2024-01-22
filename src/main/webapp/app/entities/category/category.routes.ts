import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { CategoryComponent } from './list/category.component';
import { CategoryDetailComponent } from './detail/category-detail.component';
import { CategoryUpdateComponent } from './update/category-update.component';
import CategoryResolve from './route/category-routing-resolve.service';

const categoryRoute: Routes = [
  {
    path: '',
    component: CategoryComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CategoryDetailComponent,
    resolve: {
      category: CategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CategoryUpdateComponent,
    resolve: {
      category: CategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CategoryUpdateComponent,
    resolve: {
      category: CategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default categoryRoute;
