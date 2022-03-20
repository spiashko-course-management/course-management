import { IModule } from 'app/shared/model/module.model';
import { IUser } from 'app/shared/model/user.model';

export interface ICourse {
  id?: number;
  title?: string;
  modules?: IModule[] | null;
  teacher?: IUser;
}

export const defaultValue: Readonly<ICourse> = {};
