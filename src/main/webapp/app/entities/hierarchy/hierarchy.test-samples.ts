import { IHierarchy, NewHierarchy } from './hierarchy.model';

export const sampleWithRequiredData: IHierarchy = {
  id: 15299,
  description: "à l'entour de prout",
};

export const sampleWithPartialData: IHierarchy = {
  id: 20560,
  description: 'assez éliminer de peur de',
};

export const sampleWithFullData: IHierarchy = {
  id: 29588,
  description: 'actionnaire',
};

export const sampleWithNewData: NewHierarchy = {
  description: 'aXX',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
