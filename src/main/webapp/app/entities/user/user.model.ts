export interface IUser {
  id: number;
  login?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  activated?: boolean;
  imageUrl?: string;
}

export class User implements IUser {
  constructor(
    public id: number,
    public login: string,
    public firstName: string,
    public lastName: string,
    public email: string,
    public activated: boolean,
    public imageUrl: string,
  ) {}
}

export function getUserIdentifier(user: IUser): number {
  return user.id;
}
