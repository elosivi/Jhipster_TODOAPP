export interface IHierarchy {
  id: number;
  description?: string | null;
}

export type NewHierarchy = Omit<IHierarchy, 'id'> & { id: null };
