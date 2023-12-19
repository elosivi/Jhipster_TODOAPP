import { IMainTask } from 'app/entities/main-task/main-task.model';
import { ISubTask } from 'app/entities/sub-task/sub-task.model';

export interface IStatus {
  id: number;
  description?: string | null;
  mainTask?: IMainTask | null;
  subTask?: ISubTask | null;
}

export type NewStatus = Omit<IStatus, 'id'> & { id: null };
