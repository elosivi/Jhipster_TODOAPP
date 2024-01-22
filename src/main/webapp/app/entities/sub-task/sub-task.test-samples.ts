import dayjs from 'dayjs/esm';

import { ISubTask, NewSubTask } from './sub-task.model';

export const sampleWithRequiredData: ISubTask = {
  id: 3815,
  description: 'partout',
  deadline: dayjs('2023-12-17'),
};

export const sampleWithPartialData: ISubTask = {
  id: 936,
  description: 'jeune après que paf',
  deadline: dayjs('2023-12-18'),
  cost: 16133.18,
};

export const sampleWithFullData: ISubTask = {
  id: 26211,
  description: 'commis de cuisine neutre',
  deadline: dayjs('2023-12-18'),
  creation: dayjs('2023-12-18'),
  cost: 24758.5,
};

export const sampleWithNewData: NewSubTask = {
  description: 'nager',
  deadline: dayjs('2023-12-18'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
