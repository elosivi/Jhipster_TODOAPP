import { IPerson, NewPerson } from './person.model';

export const sampleWithRequiredData: IPerson = {
  id: 21983,
};

export const sampleWithPartialData: IPerson = {
  id: 2359,
  description: 'avex à bas de présidence',
  name: 'boum',
};

export const sampleWithFullData: IPerson = {
  id: 8113,
  description: 'juriste cot cot',
  pseudo: 'inventer',
  name: 'envers',
};

export const sampleWithNewData: NewPerson = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
