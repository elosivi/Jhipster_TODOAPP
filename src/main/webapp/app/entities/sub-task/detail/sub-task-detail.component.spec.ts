import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SubTaskDetailComponent } from './sub-task-detail.component';

describe('SubTask Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubTaskDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: SubTaskDetailComponent,
              resolve: { subTask: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(SubTaskDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load subTask on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SubTaskDetailComponent);

      // THEN
      expect(instance.subTask).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
