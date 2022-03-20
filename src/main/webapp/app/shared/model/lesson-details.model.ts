import { ILesson } from 'app/shared/model/lesson.model';

export interface ILessonDetails {
  id?: number;
  content?: string;
  lesson?: ILesson;
}

export const defaultValue: Readonly<ILessonDetails> = {};
