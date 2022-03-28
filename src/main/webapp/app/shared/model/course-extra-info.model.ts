import { ICourse } from 'app/shared/model/course.model';

export interface ICourseExtraInfo {
  id?: number;
  summary?: string;
  course?: ICourse;
}

export const defaultValue: Readonly<ICourseExtraInfo> = {};
