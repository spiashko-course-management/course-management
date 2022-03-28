import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import CourseExtraInfo from './course-extra-info';
import CourseExtraInfoDetail from './course-extra-info-detail';
import CourseExtraInfoUpdate from './course-extra-info-update';
import CourseExtraInfoDeleteDialog from './course-extra-info-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CourseExtraInfoUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CourseExtraInfoUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={CourseExtraInfoDetail} />
      <ErrorBoundaryRoute path={match.url} component={CourseExtraInfo} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={CourseExtraInfoDeleteDialog} />
  </>
);

export default Routes;
