import { ILesson } from 'app/shared/model/lesson.model';
import { ICourse } from 'app/shared/model/course.model';

export interface IModule {
  id?: number;
  title?: string;
  lessons?: ILesson[] | null;
  course?: ICourse;
}

export const defaultValue: Readonly<IModule> = {};
