import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { MainTaskDetailComponent } from './main-task-detail.component';

describe('MainTask Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MainTaskDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: MainTaskDetailComponent,
              resolve: { mainTask: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(MainTaskDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load mainTask on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MainTaskDetailComponent);

      // THEN
      expect(instance.mainTask).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
