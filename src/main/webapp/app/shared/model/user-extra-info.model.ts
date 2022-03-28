import { IUser } from 'app/shared/model/user.model';

export interface IUserExtraInfo {
  id?: string;
  bio?: string | null;
  user?: IUser;
}

export const defaultValue: Readonly<IUserExtraInfo> = {};
