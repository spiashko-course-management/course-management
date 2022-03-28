import { ILesson } from 'app/shared/model/lesson.model';

export interface ILessonExtraInfo {
  id?: number;
  content?: string;
  lesson?: ILesson;
}

export const defaultValue: Readonly<ILessonExtraInfo> = {};
