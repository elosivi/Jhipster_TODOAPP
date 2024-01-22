import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { StatusDetailComponent } from './status-detail.component';

describe('Status Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatusDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: StatusDetailComponent,
              resolve: { status: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(StatusDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load status on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', StatusDetailComponent);

      // THEN
      expect(instance.status).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
