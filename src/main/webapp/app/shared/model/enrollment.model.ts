import { ICompletedLesson } from 'app/shared/model/completed-lesson.model';
import { ICourse } from 'app/shared/model/course.model';
import { IUser } from 'app/shared/model/user.model';

export interface IEnrollment {
  id?: number;
  completedLessons?: ICompletedLesson[] | null;
  course?: ICourse;
  student?: IUser;
}

export const defaultValue: Readonly<IEnrollment> = {};
