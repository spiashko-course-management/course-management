import { ILesson } from 'app/shared/model/lesson.model';
import { IEnrollment } from 'app/shared/model/enrollment.model';

export interface ICompletedLesson {
  id?: number;
  lesson?: ILesson;
  enrollment?: IEnrollment;
}

export const defaultValue: Readonly<ICompletedLesson> = {};
