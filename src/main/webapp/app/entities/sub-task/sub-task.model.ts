import dayjs from 'dayjs/esm';
import { IMainTask } from 'app/entities/main-task/main-task.model';
import { IPerson } from 'app/entities/person/person.model';
import { IStatus } from 'app/entities/status/status.model';

export interface ISubTask {
  id: number;
  description?: string | null;
  deadline?: dayjs.Dayjs | null;
  creation?: dayjs.Dayjs | null;
  cost?: number | null;
  mainTask?: Pick<IMainTask, 'id'> | null;
  personDoer?: IPerson | null;
  status?: Pick<IStatus, 'id'> | null;
}

export type NewSubTask = Omit<ISubTask, 'id'> & { id: null };
