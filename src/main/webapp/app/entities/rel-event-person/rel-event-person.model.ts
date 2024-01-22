import { IEvent } from 'app/entities/event/event.model';
import { IPerson } from 'app/entities/person/person.model';
import { IHierarchy } from 'app/entities/hierarchy/hierarchy.model';
import { IUser } from '../user/user.model';
import { IEventType } from '../event-type/event-type.model';

export interface IRelEventPerson {
  id: number;
  participation?: string | null;
  event?: IEvent | null;
  people?: IPerson[] | null;
  person?: IPerson | null;
  user?: IUser | null;
  eventType?: IEventType | null;
  hierarchy?: IHierarchy | null;
}

export type NewRelEventPerson = Omit<IRelEventPerson, 'id'> & { id: null };
