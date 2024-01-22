import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { HierarchyDetailComponent } from './hierarchy-detail.component';

describe('Hierarchy Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HierarchyDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: HierarchyDetailComponent,
              resolve: { hierarchy: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(HierarchyDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load hierarchy on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', HierarchyDetailComponent);

      // THEN
      expect(instance.hierarchy).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
