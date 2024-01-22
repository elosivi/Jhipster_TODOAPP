import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ISubTask } from '../sub-task.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../sub-task.test-samples';

import { SubTaskService, RestSubTask } from './sub-task.service';

const requireRestSample: RestSubTask = {
  ...sampleWithRequiredData,
  deadline: sampleWithRequiredData.deadline?.format(DATE_FORMAT),
  creation: sampleWithRequiredData.creation?.format(DATE_FORMAT),
};

describe('SubTask Service', () => {
  let service: SubTaskService;
  let httpMock: HttpTestingController;
  let expectedResult: ISubTask | ISubTask[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SubTaskService);
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

    it('should create a SubTask', () => {
      const subTask = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(subTask).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SubTask', () => {
      const subTask = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(subTask).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SubTask', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SubTask', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SubTask', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a SubTask', () => {
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

    describe('addSubTaskToCollectionIfMissing', () => {
      it('should add a SubTask to an empty array', () => {
        const subTask: ISubTask = sampleWithRequiredData;
        expectedResult = service.addSubTaskToCollectionIfMissing([], subTask);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(subTask);
      });

      it('should not add a SubTask to an array that contains it', () => {
        const subTask: ISubTask = sampleWithRequiredData;
        const subTaskCollection: ISubTask[] = [
          {
            ...subTask,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSubTaskToCollectionIfMissing(subTaskCollection, subTask);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SubTask to an array that doesn't contain it", () => {
        const subTask: ISubTask = sampleWithRequiredData;
        const subTaskCollection: ISubTask[] = [sampleWithPartialData];
        expectedResult = service.addSubTaskToCollectionIfMissing(subTaskCollection, subTask);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(subTask);
      });

      it('should add only unique SubTask to an array', () => {
        const subTaskArray: ISubTask[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const subTaskCollection: ISubTask[] = [sampleWithRequiredData];
        expectedResult = service.addSubTaskToCollectionIfMissing(subTaskCollection, ...subTaskArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const subTask: ISubTask = sampleWithRequiredData;
        const subTask2: ISubTask = sampleWithPartialData;
        expectedResult = service.addSubTaskToCollectionIfMissing([], subTask, subTask2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(subTask);
        expect(expectedResult).toContain(subTask2);
      });

      it('should accept null and undefined values', () => {
        const subTask: ISubTask = sampleWithRequiredData;
        expectedResult = service.addSubTaskToCollectionIfMissing([], null, subTask, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(subTask);
      });

      it('should return initial array if no SubTask is added', () => {
        const subTaskCollection: ISubTask[] = [sampleWithRequiredData];
        expectedResult = service.addSubTaskToCollectionIfMissing(subTaskCollection, undefined, null);
        expect(expectedResult).toEqual(subTaskCollection);
      });
    });

    describe('compareSubTask', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSubTask(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSubTask(entity1, entity2);
        const compareResult2 = service.compareSubTask(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSubTask(entity1, entity2);
        const compareResult2 = service.compareSubTask(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSubTask(entity1, entity2);
        const compareResult2 = service.compareSubTask(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
