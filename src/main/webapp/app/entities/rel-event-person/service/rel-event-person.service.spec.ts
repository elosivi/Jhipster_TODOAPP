import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRelEventPerson } from '../rel-event-person.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../rel-event-person.test-samples';

import { RelEventPersonService } from './rel-event-person.service';

const requireRestSample: IRelEventPerson = {
  ...sampleWithRequiredData,
};

describe('RelEventPerson Service', () => {
  let service: RelEventPersonService;
  let httpMock: HttpTestingController;
  let expectedResult: IRelEventPerson | IRelEventPerson[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RelEventPersonService);
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

    it('should create a RelEventPerson', () => {
      const relEventPerson = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(relEventPerson).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RelEventPerson', () => {
      const relEventPerson = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(relEventPerson).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a RelEventPerson', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RelEventPerson', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a RelEventPerson', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a RelEventPerson', () => {
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

    describe('addRelEventPersonToCollectionIfMissing', () => {
      it('should add a RelEventPerson to an empty array', () => {
        const relEventPerson: IRelEventPerson = sampleWithRequiredData;
        expectedResult = service.addRelEventPersonToCollectionIfMissing([], relEventPerson);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(relEventPerson);
      });

      it('should not add a RelEventPerson to an array that contains it', () => {
        const relEventPerson: IRelEventPerson = sampleWithRequiredData;
        const relEventPersonCollection: IRelEventPerson[] = [
          {
            ...relEventPerson,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRelEventPersonToCollectionIfMissing(relEventPersonCollection, relEventPerson);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RelEventPerson to an array that doesn't contain it", () => {
        const relEventPerson: IRelEventPerson = sampleWithRequiredData;
        const relEventPersonCollection: IRelEventPerson[] = [sampleWithPartialData];
        expectedResult = service.addRelEventPersonToCollectionIfMissing(relEventPersonCollection, relEventPerson);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(relEventPerson);
      });

      it('should add only unique RelEventPerson to an array', () => {
        const relEventPersonArray: IRelEventPerson[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const relEventPersonCollection: IRelEventPerson[] = [sampleWithRequiredData];
        expectedResult = service.addRelEventPersonToCollectionIfMissing(relEventPersonCollection, ...relEventPersonArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const relEventPerson: IRelEventPerson = sampleWithRequiredData;
        const relEventPerson2: IRelEventPerson = sampleWithPartialData;
        expectedResult = service.addRelEventPersonToCollectionIfMissing([], relEventPerson, relEventPerson2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(relEventPerson);
        expect(expectedResult).toContain(relEventPerson2);
      });

      it('should accept null and undefined values', () => {
        const relEventPerson: IRelEventPerson = sampleWithRequiredData;
        expectedResult = service.addRelEventPersonToCollectionIfMissing([], null, relEventPerson, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(relEventPerson);
      });

      it('should return initial array if no RelEventPerson is added', () => {
        const relEventPersonCollection: IRelEventPerson[] = [sampleWithRequiredData];
        expectedResult = service.addRelEventPersonToCollectionIfMissing(relEventPersonCollection, undefined, null);
        expect(expectedResult).toEqual(relEventPersonCollection);
      });
    });

    describe('compareRelEventPerson', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRelEventPerson(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareRelEventPerson(entity1, entity2);
        const compareResult2 = service.compareRelEventPerson(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareRelEventPerson(entity1, entity2);
        const compareResult2 = service.compareRelEventPerson(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareRelEventPerson(entity1, entity2);
        const compareResult2 = service.compareRelEventPerson(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
