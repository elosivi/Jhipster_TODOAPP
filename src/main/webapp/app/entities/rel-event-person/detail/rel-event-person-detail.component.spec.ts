import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { RelEventPersonDetailComponent } from './rel-event-person-detail.component';

describe('RelEventPerson Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RelEventPersonDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: RelEventPersonDetailComponent,
              resolve: { relEventPerson: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(RelEventPersonDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load relEventPerson on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', RelEventPersonDetailComponent);

      // THEN
      expect(instance.relEventPerson).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
