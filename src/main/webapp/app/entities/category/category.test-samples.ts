import { ICategory, NewCategory } from './category.model';

export const sampleWithRequiredData: ICategory = {
  id: 20939,
  label: 'Kcjziq2',
};

export const sampleWithPartialData: ICategory = {
  id: 14553,
  label: 'Ozqw5',
  description: 'solitaire',
};

export const sampleWithFullData: ICategory = {
  id: 28965,
  label: 'Wdwe3',
  description: 'circulaire figurer',
};

export const sampleWithNewData: NewCategory = {
  label: 'Tuztw9',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
