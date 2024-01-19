import { IPerson, NewPerson } from './person.model';

export const sampleWithRequiredData: IPerson = {
  id: 27082,
};

export const sampleWithPartialData: IPerson = {
  id: 25977,
  description: 'dans impromptu durer',
};

export const sampleWithFullData: IPerson = {
  id: 20384,
  description: 'parlementaire du fait que',
  pseudo: 'd’autant que commander',
  name: 'jadis ensemble',
};

export const sampleWithNewData: NewPerson = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
