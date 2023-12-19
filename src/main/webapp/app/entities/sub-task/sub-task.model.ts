import dayjs from 'dayjs/esm';
import { IStatus } from 'app/entities/status/status.model';
import { IMainTask } from 'app/entities/main-task/main-task.model';
import { IPerson } from 'app/entities/person/person.model';

export interface ISubTask {
  id: number;
  description?: string | null;
  deadline?: dayjs.Dayjs | null;
  creation?: dayjs.Dayjs | null;
  cost?: number | null;
  status?: IStatus | null;
  mainTask?: IMainTask | null;
  personDoer?: IPerson | null;
}

export type NewSubTask = Omit<ISubTask, 'id'> & { id: null };
