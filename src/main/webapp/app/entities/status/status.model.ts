import { IMainTask } from 'app/entities/main-task/main-task.model';
import { ISubTask } from 'app/entities/sub-task/sub-task.model';

export interface IStatus {
  id: number;
  description?: string | null;
  mainTasks?: Pick<IMainTask, 'id'>[] | null;
  subTasks?: Pick<ISubTask, 'id'>[] | null;
}

export type NewStatus = Omit<IStatus, 'id'> & { id: null };
