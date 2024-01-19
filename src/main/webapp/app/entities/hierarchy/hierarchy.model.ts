import { IRelEventPerson } from 'app/entities/rel-event-person/rel-event-person.model';

export interface IHierarchy {
  id: number;
  description?: string | null;
  relEventPeople?: IRelEventPerson[] | null;
}

export type NewHierarchy = Omit<IHierarchy, 'id'> & { id: null };
