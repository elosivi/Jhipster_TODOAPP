import { IRelEventPerson, NewRelEventPerson } from './rel-event-person.model';

export const sampleWithRequiredData: IRelEventPerson = {
  id: 21438,
};

export const sampleWithPartialData: IRelEventPerson = {
  id: 605,
};

export const sampleWithFullData: IRelEventPerson = {
  id: 12673,
  participation: 'peut-être',
};

export const sampleWithNewData: NewRelEventPerson = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
