import { ICourse } from 'app/shared/model/course.model';

export interface ICourseDetails {
  id?: number;
  summary?: string;
  course?: ICourse;
}

export const defaultValue: Readonly<ICourseDetails> = {};
