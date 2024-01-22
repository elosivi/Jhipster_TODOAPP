import { IMainTask } from 'app/entities/main-task/main-task.model';

export interface ICategory {
  id: number;
  label?: string | null;
  description?: string | null;
  mainTasks?: Pick<IMainTask, 'id'>[] | null;
}

export type NewCategory = Omit<ICategory, 'id'> & { id: null };
