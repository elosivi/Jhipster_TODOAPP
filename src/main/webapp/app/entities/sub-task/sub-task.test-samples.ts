import dayjs from 'dayjs/esm';

import { ISubTask, NewSubTask } from './sub-task.model';

export const sampleWithRequiredData: ISubTask = {
  id: 31821,
  description: 'du fait que',
  deadline: dayjs('2023-12-17'),
};

export const sampleWithPartialData: ISubTask = {
  id: 32423,
  description: 'dans la mesure où accompagner',
  deadline: dayjs('2023-12-17'),
  creation: dayjs('2023-12-18'),
  cost: 28739.47,
};

export const sampleWithFullData: ISubTask = {
  id: 5001,
  description: 'bien que commis',
  deadline: dayjs('2023-12-17'),
  creation: dayjs('2023-12-18'),
  cost: 16421.39,
};

export const sampleWithNewData: NewSubTask = {
  description: 'électorat',
  deadline: dayjs('2023-12-17'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
