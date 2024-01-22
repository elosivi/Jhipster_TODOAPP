import dayjs from 'dayjs/esm';
import { ICategory } from 'app/entities/category/category.model';
import { IPerson } from 'app/entities/person/person.model';
import { IStatus } from 'app/entities/status/status.model';
import { ISubTask } from 'app/entities/sub-task/sub-task.model';

export interface IMainTask {
  id: number;
  description?: string | null;
  deadline?: dayjs.Dayjs | null;
  creation?: dayjs.Dayjs | null;
  cost?: number | null;
  category?: Pick<ICategory, 'id'> | null;
  personOwner?: IPerson | null;
  status?: Pick<IStatus, 'id'> | null;
  subTasks?: Pick<ISubTask, 'id'>[] | null;
}

export type NewMainTask = Omit<IMainTask, 'id'> & { id: null };
