import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import authentication from './authentication';
import applicationProfile from './application-profile';

import administration from 'app/modules/administration/administration.reducer';
import userManagement from './user-management';
// prettier-ignore
import course from 'app/entities/course/course.reducer';
// prettier-ignore
import courseExtraInfo from 'app/entities/course-extra-info.reducer';
// prettier-ignore
import module from 'app/entities/module.reducer';
// prettier-ignore
import lesson from 'app/entities/lesson.reducer';
// prettier-ignore
import lessonExtraInfo from 'app/entities/lesson-extra-info.reducer';
// prettier-ignore
import completedLesson from 'app/entities/completed-lesson.reducer';
// prettier-ignore
import enrollment from 'app/entities/enrollment.reducer';
// prettier-ignore
import userExtraInfo from 'app/entities/user-extra-info.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const rootReducer = {
  authentication,
  applicationProfile,
  administration,
  userManagement,
  course,
  courseExtraInfo,
  module,
  lesson,
  lessonExtraInfo,
  completedLesson,
  enrollment,
  userExtraInfo,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar,
};

export default rootReducer;
