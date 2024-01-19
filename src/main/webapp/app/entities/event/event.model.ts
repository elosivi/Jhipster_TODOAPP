import dayjs from 'dayjs/esm';
import { IEventType } from 'app/entities/event-type/event-type.model';
import { IPerson } from 'app/entities/person/person.model';
import { IRelEventPerson } from 'app/entities/rel-event-person/rel-event-person.model';

export interface IEvent {
  id: number;
  label?: string | null;
  description?: string | null;
  theme?: string | null;
  dateStart?: dayjs.Dayjs | null;
  dateEnd?: dayjs.Dayjs | null;
  place?: string | null;
  placeDetails?: string | null;
  adress?: string | null;
  note?: string | null;
  eventType?: IEventType | null;
  people?: IPerson[] | null;
  relEventPeople?: IRelEventPerson[] | null;
}

export type NewEvent = Omit<IEvent, 'id'> & { id: null };
