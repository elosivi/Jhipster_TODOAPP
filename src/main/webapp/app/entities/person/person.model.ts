import { IUser } from 'app/entities/user/user.model';
import { IHierarchy } from 'app/entities/hierarchy/hierarchy.model';
import { IEvent } from 'app/entities/event/event.model';

export interface IPerson {
  id: number;
  description?: string | null;
  user?: Pick<IUser, 'id'> | null;
  hierarchy?: Pick<IHierarchy, 'id'> | null;
  events?: Pick<IEvent, 'id'>[] | null;
}

export type NewPerson = Omit<IPerson, 'id'> & { id: null };
