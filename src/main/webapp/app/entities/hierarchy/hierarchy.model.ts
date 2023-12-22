import { IPerson } from 'app/entities/person/person.model';

export interface IHierarchy {
  id: number;
  description?: string | null;
  person?: Pick<IPerson, 'id'> | null;
}

export type NewHierarchy = Omit<IHierarchy, 'id'> & { id: null };
