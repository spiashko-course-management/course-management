import { IUser } from 'app/shared/model/user.model';

export interface IUserDetails {
  id?: string;
  bio?: string | null;
  user?: IUser;
}

export const defaultValue: Readonly<IUserDetails> = {};
