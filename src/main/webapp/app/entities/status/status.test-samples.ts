import { IStatus, NewStatus } from './status.model';

export const sampleWithRequiredData: IStatus = {
  id: 12956,
  description: 'espiègle dehors innombrable',
};

export const sampleWithPartialData: IStatus = {
  id: 21841,
  description: 'conseil municipal du moment que',
};

export const sampleWithFullData: IStatus = {
  id: 8254,
  description: 'cuicui',
};

export const sampleWithNewData: NewStatus = {
  description: 'selon circulaire revivre',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
