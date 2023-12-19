import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { EventTypeDetailComponent } from './event-type-detail.component';

describe('EventType Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventTypeDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: EventTypeDetailComponent,
              resolve: { eventType: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(EventTypeDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load eventType on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', EventTypeDetailComponent);

      // THEN
      expect(instance.eventType).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
