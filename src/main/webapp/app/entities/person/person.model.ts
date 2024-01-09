import { IUser } from 'app/entities/user/user.model';
import { IEvent } from 'app/entities/event/event.model';

export interface IPerson {
  id: number;
  description?: string | null;
  pseudo?: string | null;
  name?: string | null;
  user?: IUser | null;
  events?: Pick<IEvent, 'id'>[] | null;
}

export type NewPerson = Omit<IPerson, 'id'> & { id: null };
