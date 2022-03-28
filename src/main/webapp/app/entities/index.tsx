import React from 'react';
import { Switch } from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Course from './course';
import CourseExtraInfo from './course-extra-info';
import Module from './module';
import Lesson from './lesson';
import LessonExtraInfo from './lesson-extra-info';
import CompletedLesson from './completed-lesson';
import Enrollment from './enrollment';
import UserExtraInfo from './user-extra-info';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}course`} component={Course} />
      <ErrorBoundaryRoute path={`${match.url}course-extra-info`} component={CourseExtraInfo} />
      <ErrorBoundaryRoute path={`${match.url}module`} component={Module} />
      <ErrorBoundaryRoute path={`${match.url}lesson`} component={Lesson} />
      <ErrorBoundaryRoute path={`${match.url}lesson-extra-info`} component={LessonExtraInfo} />
      <ErrorBoundaryRoute path={`${match.url}completed-lesson`} component={CompletedLesson} />
      <ErrorBoundaryRoute path={`${match.url}enrollment`} component={Enrollment} />
      <ErrorBoundaryRoute path={`${match.url}user-extra-info`} component={UserExtraInfo} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
