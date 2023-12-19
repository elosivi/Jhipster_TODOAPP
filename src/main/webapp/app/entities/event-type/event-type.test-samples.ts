import { IEventType, NewEventType } from './event-type.model';

export const sampleWithRequiredData: IEventType = {
  id: 19293,
  label: 'Xzcnv6',
};

export const sampleWithPartialData: IEventType = {
  id: 23228,
  label: 'Frxib4',
  description: 'dans projeter à défaut de ',
  duration: '23432',
};

export const sampleWithFullData: IEventType = {
  id: 10255,
  label: 'Zcyzj1',
  description: 'clientèle',
  duration: '22215',
};

export const sampleWithNewData: NewEventType = {
  label: 'Sdhxm2',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
