import { IUser } from 'app/entities/user/user.model';
import { IEvent } from 'app/entities/event/event.model';
import { IRelEventPerson } from 'app/entities/rel-event-person/rel-event-person.model';

export interface IPerson {
  id: number;
  description?: string | null;
  pseudo?: string | null;
  name?: string | null;
  user?: IUser | null;
  events?: IEvent[] | null;
  relEventPeople?: IRelEventPerson | null;
}

export type NewPerson = Omit<IPerson, 'id'> & { id: null };
