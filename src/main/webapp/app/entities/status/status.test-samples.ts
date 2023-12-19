import { IStatus, NewStatus } from './status.model';

export const sampleWithRequiredData: IStatus = {
  id: 10970,
  description: 'cueillir depuis au point que',
};

export const sampleWithPartialData: IStatus = {
  id: 599,
  description: 'au-devant chut',
};

export const sampleWithFullData: IStatus = {
  id: 17385,
  description: 'proche de euh',
};

export const sampleWithNewData: NewStatus = {
  description: 'que derechef',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
