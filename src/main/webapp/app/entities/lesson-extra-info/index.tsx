import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import LessonExtraInfo from './lesson-extra-info';
import LessonExtraInfoDetail from './lesson-extra-info-detail';
import LessonExtraInfoUpdate from './lesson-extra-info-update';
import LessonExtraInfoDeleteDialog from './lesson-extra-info-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={LessonExtraInfoUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={LessonExtraInfoUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={LessonExtraInfoDetail} />
      <ErrorBoundaryRoute path={match.url} component={LessonExtraInfo} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={LessonExtraInfoDeleteDialog} />
  </>
);

export default Routes;
