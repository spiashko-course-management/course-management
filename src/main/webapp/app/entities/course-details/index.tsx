import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import CourseDetails from './course-details';
import CourseDetailsDetail from './course-details-detail';
import CourseDetailsUpdate from './course-details-update';
import CourseDetailsDeleteDialog from './course-details-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CourseDetailsUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CourseDetailsUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={CourseDetailsDetail} />
      <ErrorBoundaryRoute path={match.url} component={CourseDetails} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={CourseDetailsDeleteDialog} />
  </>
);

export default Routes;
