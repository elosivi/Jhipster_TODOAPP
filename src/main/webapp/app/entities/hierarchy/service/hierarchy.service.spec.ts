import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IHierarchy } from '../hierarchy.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../hierarchy.test-samples';

import { HierarchyService } from './hierarchy.service';

const requireRestSample: IHierarchy = {
  ...sampleWithRequiredData,
};

describe('Hierarchy Service', () => {
  let service: HierarchyService;
  let httpMock: HttpTestingController;
  let expectedResult: IHierarchy | IHierarchy[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(HierarchyService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Hierarchy', () => {
      const hierarchy = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(hierarchy).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Hierarchy', () => {
      const hierarchy = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(hierarchy).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Hierarchy', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Hierarchy', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Hierarchy', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a Hierarchy', () => {
      const queryObject: any = {
        page: 0,
        size: 20,
        query: '',
        sort: [],
      };
      service.search(queryObject).subscribe(() => expectedResult);

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(null, { status: 500, statusText: 'Internal Server Error' });
      expect(expectedResult).toBe(null);
    });

    describe('addHierarchyToCollectionIfMissing', () => {
      it('should add a Hierarchy to an empty array', () => {
        const hierarchy: IHierarchy = sampleWithRequiredData;
        expectedResult = service.addHierarchyToCollectionIfMissing([], hierarchy);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(hierarchy);
      });

      it('should not add a Hierarchy to an array that contains it', () => {
        const hierarchy: IHierarchy = sampleWithRequiredData;
        const hierarchyCollection: IHierarchy[] = [
          {
            ...hierarchy,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addHierarchyToCollectionIfMissing(hierarchyCollection, hierarchy);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Hierarchy to an array that doesn't contain it", () => {
        const hierarchy: IHierarchy = sampleWithRequiredData;
        const hierarchyCollection: IHierarchy[] = [sampleWithPartialData];
        expectedResult = service.addHierarchyToCollectionIfMissing(hierarchyCollection, hierarchy);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(hierarchy);
      });

      it('should add only unique Hierarchy to an array', () => {
        const hierarchyArray: IHierarchy[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const hierarchyCollection: IHierarchy[] = [sampleWithRequiredData];
        expectedResult = service.addHierarchyToCollectionIfMissing(hierarchyCollection, ...hierarchyArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const hierarchy: IHierarchy = sampleWithRequiredData;
        const hierarchy2: IHierarchy = sampleWithPartialData;
        expectedResult = service.addHierarchyToCollectionIfMissing([], hierarchy, hierarchy2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(hierarchy);
        expect(expectedResult).toContain(hierarchy2);
      });

      it('should accept null and undefined values', () => {
        const hierarchy: IHierarchy = sampleWithRequiredData;
        expectedResult = service.addHierarchyToCollectionIfMissing([], null, hierarchy, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(hierarchy);
      });

      it('should return initial array if no Hierarchy is added', () => {
        const hierarchyCollection: IHierarchy[] = [sampleWithRequiredData];
        expectedResult = service.addHierarchyToCollectionIfMissing(hierarchyCollection, undefined, null);
        expect(expectedResult).toEqual(hierarchyCollection);
      });
    });

    describe('compareHierarchy', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareHierarchy(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareHierarchy(entity1, entity2);
        const compareResult2 = service.compareHierarchy(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareHierarchy(entity1, entity2);
        const compareResult2 = service.compareHierarchy(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareHierarchy(entity1, entity2);
        const compareResult2 = service.compareHierarchy(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
