import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CategoryDetailComponent } from './category-detail.component';

describe('Category Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: CategoryDetailComponent,
              resolve: { category: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CategoryDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load category on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CategoryDetailComponent);

      // THEN
      expect(instance.category).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
