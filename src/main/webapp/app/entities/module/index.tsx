import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Module from './module';
import ModuleDetail from './module-detail';
import ModuleUpdate from './module-update';
import ModuleDeleteDialog from './module-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ModuleUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ModuleUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ModuleDetail} />
      <ErrorBoundaryRoute path={match.url} component={Module} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ModuleDeleteDialog} />
  </>
);

export default Routes;
