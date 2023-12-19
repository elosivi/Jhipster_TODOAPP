import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IEventType } from '../event-type.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../event-type.test-samples';

import { EventTypeService } from './event-type.service';

const requireRestSample: IEventType = {
  ...sampleWithRequiredData,
};

describe('EventType Service', () => {
  let service: EventTypeService;
  let httpMock: HttpTestingController;
  let expectedResult: IEventType | IEventType[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EventTypeService);
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

    it('should create a EventType', () => {
      const eventType = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(eventType).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a EventType', () => {
      const eventType = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(eventType).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a EventType', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of EventType', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a EventType', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a EventType', () => {
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

    describe('addEventTypeToCollectionIfMissing', () => {
      it('should add a EventType to an empty array', () => {
        const eventType: IEventType = sampleWithRequiredData;
        expectedResult = service.addEventTypeToCollectionIfMissing([], eventType);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(eventType);
      });

      it('should not add a EventType to an array that contains it', () => {
        const eventType: IEventType = sampleWithRequiredData;
        const eventTypeCollection: IEventType[] = [
          {
            ...eventType,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEventTypeToCollectionIfMissing(eventTypeCollection, eventType);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a EventType to an array that doesn't contain it", () => {
        const eventType: IEventType = sampleWithRequiredData;
        const eventTypeCollection: IEventType[] = [sampleWithPartialData];
        expectedResult = service.addEventTypeToCollectionIfMissing(eventTypeCollection, eventType);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(eventType);
      });

      it('should add only unique EventType to an array', () => {
        const eventTypeArray: IEventType[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const eventTypeCollection: IEventType[] = [sampleWithRequiredData];
        expectedResult = service.addEventTypeToCollectionIfMissing(eventTypeCollection, ...eventTypeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const eventType: IEventType = sampleWithRequiredData;
        const eventType2: IEventType = sampleWithPartialData;
        expectedResult = service.addEventTypeToCollectionIfMissing([], eventType, eventType2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(eventType);
        expect(expectedResult).toContain(eventType2);
      });

      it('should accept null and undefined values', () => {
        const eventType: IEventType = sampleWithRequiredData;
        expectedResult = service.addEventTypeToCollectionIfMissing([], null, eventType, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(eventType);
      });

      it('should return initial array if no EventType is added', () => {
        const eventTypeCollection: IEventType[] = [sampleWithRequiredData];
        expectedResult = service.addEventTypeToCollectionIfMissing(eventTypeCollection, undefined, null);
        expect(expectedResult).toEqual(eventTypeCollection);
      });
    });

    describe('compareEventType', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEventType(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareEventType(entity1, entity2);
        const compareResult2 = service.compareEventType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareEventType(entity1, entity2);
        const compareResult2 = service.compareEventType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareEventType(entity1, entity2);
        const compareResult2 = service.compareEventType(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
