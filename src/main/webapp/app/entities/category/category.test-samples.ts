import { ICategory, NewCategory } from './category.model';

export const sampleWithRequiredData: ICategory = {
  id: 14317,
  label: 'responsable',
};

export const sampleWithPartialData: ICategory = {
  id: 5621,
  label: 'oups à peu près siffler',
};

export const sampleWithFullData: ICategory = {
  id: 24931,
  label: 'coupable sage',
  description: 'subito du moment que vlan',
};

export const sampleWithNewData: NewCategory = {
  label: "d'entre entièrement après que",
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
