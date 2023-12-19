import dayjs from 'dayjs/esm';
import { IStatus } from 'app/entities/status/status.model';
import { ICategory } from 'app/entities/category/category.model';
import { IPerson } from 'app/entities/person/person.model';
import { ISubTask } from 'app/entities/sub-task/sub-task.model';

export interface IMainTask {
  id: number;
  description?: string | null;
  deadline?: dayjs.Dayjs | null;
  creation?: dayjs.Dayjs | null;
  cost?: number | null;
  status?: IStatus | null;
  category?: ICategory | null;
  personOwner?: IPerson | null;
  subTasks?: ISubTask[] | null;
}

export type NewMainTask = Omit<IMainTask, 'id'> & { id: null };
