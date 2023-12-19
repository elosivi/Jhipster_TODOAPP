import { IHierarchy, NewHierarchy } from './hierarchy.model';

export const sampleWithRequiredData: IHierarchy = {
  id: 28953,
  description: 'Vfwznl4',
};

export const sampleWithPartialData: IHierarchy = {
  id: 6932,
  description: 'Rn0',
};

export const sampleWithFullData: IHierarchy = {
  id: 25100,
  description: 'Otgjqf6',
};

export const sampleWithNewData: NewHierarchy = {
  description: 'Gd0',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
