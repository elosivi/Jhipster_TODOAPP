import { IEvent } from 'app/entities/event/event.model';

export interface IEventType {
  id: number;
  label?: string | null;
  description?: string | null;
  duration?: string | null;
  events?: Pick<IEvent, 'id'>[] | null;
}

export type NewEventType = Omit<IEventType, 'id'> & { id: null };
