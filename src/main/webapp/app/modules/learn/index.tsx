import React from 'react';
import {Switch} from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Course from './course';
import Explore from './explore';

const Routes = ({match}) => (
  <div>
    <Switch>
      <ErrorBoundaryRoute path={`${match.url}explore`} component={Explore}/>
      <ErrorBoundaryRoute path={`${match.url}courses`} component={Course}/>
    </Switch>
  </div>
);

export default Routes;
