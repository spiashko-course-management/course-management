import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import CompletedLesson from './completed-lesson';
import CompletedLessonDetail from './completed-lesson-detail';
import CompletedLessonUpdate from './completed-lesson-update';
import CompletedLessonDeleteDialog from './completed-lesson-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CompletedLessonUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CompletedLessonUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={CompletedLessonDetail} />
      <ErrorBoundaryRoute path={match.url} component={CompletedLesson} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={CompletedLessonDeleteDialog} />
  </>
);

export default Routes;
