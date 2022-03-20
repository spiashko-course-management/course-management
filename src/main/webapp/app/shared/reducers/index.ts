import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import authentication from './authentication';
import applicationProfile from './application-profile';

import administration from 'app/modules/administration/administration.reducer';
import userManagement from './user-management';
// prettier-ignore
import course from 'app/entities/course/course.reducer';
// prettier-ignore
import courseDetails from 'app/entities/course-details/course-details.reducer';
// prettier-ignore
import module from 'app/entities/module/module.reducer';
// prettier-ignore
import lesson from 'app/entities/lesson/lesson.reducer';
// prettier-ignore
import lessonDetails from 'app/entities/lesson-details/lesson-details.reducer';
// prettier-ignore
import completedLesson from 'app/entities/completed-lesson/completed-lesson.reducer';
// prettier-ignore
import enrollment from 'app/entities/enrollment/enrollment.reducer';
// prettier-ignore
import userDetails from 'app/entities/user-details/user-details.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const rootReducer = {
  authentication,
  applicationProfile,
  administration,
  userManagement,
  course,
  courseDetails,
  module,
  lesson,
  lessonDetails,
  completedLesson,
  enrollment,
  userDetails,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar,
};

export default rootReducer;
