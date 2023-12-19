import dayjs from 'dayjs/esm';

import { IMainTask, NewMainTask } from './main-task.model';

export const sampleWithRequiredData: IMainTask = {
  id: 11769,
  deadline: dayjs('2023-12-18'),
};

export const sampleWithPartialData: IMainTask = {
  id: 32427,
  description: 'sans que adorable snif',
  deadline: dayjs('2023-12-18'),
  creation: dayjs('2023-12-17'),
  cost: 9406.19,
};

export const sampleWithFullData: IMainTask = {
  id: 17296,
  description: 'conseil d’administration',
  deadline: dayjs('2023-12-18'),
  creation: dayjs('2023-12-18'),
  cost: 24386.79,
};

export const sampleWithNewData: NewMainTask = {
  deadline: dayjs('2023-12-18'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
