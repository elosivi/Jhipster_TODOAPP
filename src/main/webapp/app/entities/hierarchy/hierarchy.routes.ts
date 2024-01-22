import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { HierarchyComponent } from './list/hierarchy.component';
import { HierarchyDetailComponent } from './detail/hierarchy-detail.component';
import { HierarchyUpdateComponent } from './update/hierarchy-update.component';
import HierarchyResolve from './route/hierarchy-routing-resolve.service';

const hierarchyRoute: Routes = [
  {
    path: '',
    component: HierarchyComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: HierarchyDetailComponent,
    resolve: {
      hierarchy: HierarchyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: HierarchyUpdateComponent,
    resolve: {
      hierarchy: HierarchyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: HierarchyUpdateComponent,
    resolve: {
      hierarchy: HierarchyResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default hierarchyRoute;
