import dayjs from 'dayjs/esm';
import { IEventType } from 'app/entities/event-type/event-type.model';
import { IPerson } from 'app/entities/person/person.model';

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
  eventType?: Pick<IEventType, 'id'> | null;
  people?: Pick<IPerson, 'id'>[] | null;
}

export type NewEvent = Omit<IEvent, 'id'> & { id: null };
