import { IModule } from 'app/shared/model/module.model';
import { LessonType } from 'app/shared/model/enumerations/lesson-type.model';

export interface ILesson {
  id?: number;
  order?: number;
  title?: string;
  type?: LessonType;
  module?: IModule;
}

export const defaultValue: Readonly<ILesson> = {};
