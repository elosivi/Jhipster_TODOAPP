import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { PersonDetailComponent } from './person-detail.component';

describe('Person Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PersonDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: PersonDetailComponent,
              resolve: { person: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PersonDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load person on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PersonDetailComponent);

      // THEN
      expect(instance.person).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
