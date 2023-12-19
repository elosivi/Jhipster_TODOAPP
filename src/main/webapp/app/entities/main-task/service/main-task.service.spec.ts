import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IMainTask } from '../main-task.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../main-task.test-samples';

import { MainTaskService, RestMainTask } from './main-task.service';

const requireRestSample: RestMainTask = {
  ...sampleWithRequiredData,
  deadline: sampleWithRequiredData.deadline?.format(DATE_FORMAT),
  creation: sampleWithRequiredData.creation?.format(DATE_FORMAT),
};

describe('MainTask Service', () => {
  let service: MainTaskService;
  let httpMock: HttpTestingController;
  let expectedResult: IMainTask | IMainTask[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MainTaskService);
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

    it('should create a MainTask', () => {
      const mainTask = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(mainTask).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MainTask', () => {
      const mainTask = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(mainTask).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MainTask', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MainTask', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MainTask', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a MainTask', () => {
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

    describe('addMainTaskToCollectionIfMissing', () => {
      it('should add a MainTask to an empty array', () => {
        const mainTask: IMainTask = sampleWithRequiredData;
        expectedResult = service.addMainTaskToCollectionIfMissing([], mainTask);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(mainTask);
      });

      it('should not add a MainTask to an array that contains it', () => {
        const mainTask: IMainTask = sampleWithRequiredData;
        const mainTaskCollection: IMainTask[] = [
          {
            ...mainTask,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMainTaskToCollectionIfMissing(mainTaskCollection, mainTask);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MainTask to an array that doesn't contain it", () => {
        const mainTask: IMainTask = sampleWithRequiredData;
        const mainTaskCollection: IMainTask[] = [sampleWithPartialData];
        expectedResult = service.addMainTaskToCollectionIfMissing(mainTaskCollection, mainTask);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(mainTask);
      });

      it('should add only unique MainTask to an array', () => {
        const mainTaskArray: IMainTask[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const mainTaskCollection: IMainTask[] = [sampleWithRequiredData];
        expectedResult = service.addMainTaskToCollectionIfMissing(mainTaskCollection, ...mainTaskArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const mainTask: IMainTask = sampleWithRequiredData;
        const mainTask2: IMainTask = sampleWithPartialData;
        expectedResult = service.addMainTaskToCollectionIfMissing([], mainTask, mainTask2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(mainTask);
        expect(expectedResult).toContain(mainTask2);
      });

      it('should accept null and undefined values', () => {
        const mainTask: IMainTask = sampleWithRequiredData;
        expectedResult = service.addMainTaskToCollectionIfMissing([], null, mainTask, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(mainTask);
      });

      it('should return initial array if no MainTask is added', () => {
        const mainTaskCollection: IMainTask[] = [sampleWithRequiredData];
        expectedResult = service.addMainTaskToCollectionIfMissing(mainTaskCollection, undefined, null);
        expect(expectedResult).toEqual(mainTaskCollection);
      });
    });

    describe('compareMainTask', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMainTask(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareMainTask(entity1, entity2);
        const compareResult2 = service.compareMainTask(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareMainTask(entity1, entity2);
        const compareResult2 = service.compareMainTask(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareMainTask(entity1, entity2);
        const compareResult2 = service.compareMainTask(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
