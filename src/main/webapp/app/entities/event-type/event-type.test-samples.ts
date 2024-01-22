import { IEventType, NewEventType } from './event-type.model';

export const sampleWithRequiredData: IEventType = {
  id: 4643,
  label: 'aussitôt que lunatique',
};

export const sampleWithPartialData: IEventType = {
  id: 31225,
  label: 'touriste cadre',
};

export const sampleWithFullData: IEventType = {
  id: 14649,
  label: 'dater',
  description: 'envers',
  duration: '18453',
};

export const sampleWithNewData: NewEventType = {
  label: 'recueillir',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
