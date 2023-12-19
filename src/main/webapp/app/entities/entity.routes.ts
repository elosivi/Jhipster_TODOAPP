import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'main-task',
    data: { pageTitle: 'todoApp.mainTask.home.title' },
    loadChildren: () => import('./main-task/main-task.routes'),
  },
  {
    path: 'sub-task',
    data: { pageTitle: 'todoApp.subTask.home.title' },
    loadChildren: () => import('./sub-task/sub-task.routes'),
  },
  {
    path: 'status',
    data: { pageTitle: 'todoApp.status.home.title' },
    loadChildren: () => import('./status/status.routes'),
  },
  {
    path: 'person',
    data: { pageTitle: 'todoApp.person.home.title' },
    loadChildren: () => import('./person/person.routes'),
  },
  {
    path: 'category',
    data: { pageTitle: 'todoApp.category.home.title' },
    loadChildren: () => import('./category/category.routes'),
  },
  {
    path: 'hierarchy',
    data: { pageTitle: 'todoApp.hierarchy.home.title' },
    loadChildren: () => import('./hierarchy/hierarchy.routes'),
  },
  {
    path: 'event',
    data: { pageTitle: 'todoApp.event.home.title' },
    loadChildren: () => import('./event/event.routes'),
  },
  {
    path: 'event-type',
    data: { pageTitle: 'todoApp.eventType.home.title' },
    loadChildren: () => import('./event-type/event-type.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
