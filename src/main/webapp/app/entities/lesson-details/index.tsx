import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import LessonDetails from './lesson-details';
import LessonDetailsDetail from './lesson-details-detail';
import LessonDetailsUpdate from './lesson-details-update';
import LessonDetailsDeleteDialog from './lesson-details-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={LessonDetailsUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={LessonDetailsUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={LessonDetailsDetail} />
      <ErrorBoundaryRoute path={match.url} component={LessonDetails} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={LessonDetailsDeleteDialog} />
  </>
);

export default Routes;
