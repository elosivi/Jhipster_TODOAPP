import dayjs from 'dayjs/esm';

import { IMainTask, NewMainTask } from './main-task.model';

export const sampleWithRequiredData: IMainTask = {
  id: 22150,
  deadline: dayjs('2023-12-17'),
};

export const sampleWithPartialData: IMainTask = {
  id: 27645,
  deadline: dayjs('2023-12-18'),
  creation: dayjs('2023-12-18'),
  cost: 32723.59,
};

export const sampleWithFullData: IMainTask = {
  id: 16442,
  description: 'glouglou si énorme',
  deadline: dayjs('2023-12-18'),
  creation: dayjs('2023-12-18'),
  cost: 23538.69,
};

export const sampleWithNewData: NewMainTask = {
  deadline: dayjs('2023-12-18'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
