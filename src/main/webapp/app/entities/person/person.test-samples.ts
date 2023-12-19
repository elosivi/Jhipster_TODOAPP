import { IPerson, NewPerson } from './person.model';

export const sampleWithRequiredData: IPerson = {
  id: 5737,
};

export const sampleWithPartialData: IPerson = {
  id: 21983,
};

export const sampleWithFullData: IPerson = {
  id: 15371,
  description: 'tout à fait responsable commissionnaire',
};

export const sampleWithNewData: NewPerson = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
