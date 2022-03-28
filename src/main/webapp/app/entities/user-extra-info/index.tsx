import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import UserExtraInfo from './user-extra-info';
import UserExtraInfoDetail from './user-extra-info-detail';
import UserExtraInfoUpdate from './user-extra-info-update';
import UserExtraInfoDeleteDialog from './user-extra-info-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={UserExtraInfoUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={UserExtraInfoUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={UserExtraInfoDetail} />
      <ErrorBoundaryRoute path={match.url} component={UserExtraInfo} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={UserExtraInfoDeleteDialog} />
  </>
);

export default Routes;
