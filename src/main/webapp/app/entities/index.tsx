import React from 'react';
import { Switch } from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Course from './course';
import CourseDetails from './course-details';
import Module from './module';
import Lesson from './lesson';
import LessonDetails from './lesson-details';
import CompletedLesson from './completed-lesson';
import Enrollment from './enrollment';
import UserDetails from './user-details';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}course`} component={Course} />
      <ErrorBoundaryRoute path={`${match.url}course-details`} component={CourseDetails} />
      <ErrorBoundaryRoute path={`${match.url}module`} component={Module} />
      <ErrorBoundaryRoute path={`${match.url}lesson`} component={Lesson} />
      <ErrorBoundaryRoute path={`${match.url}lesson-details`} component={LessonDetails} />
      <ErrorBoundaryRoute path={`${match.url}completed-lesson`} component={CompletedLesson} />
      <ErrorBoundaryRoute path={`${match.url}enrollment`} component={Enrollment} />
      <ErrorBoundaryRoute path={`${match.url}user-details`} component={UserDetails} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
