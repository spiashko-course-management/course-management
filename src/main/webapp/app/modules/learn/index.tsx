import React from 'react';
import {Switch} from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Course from './course';
import Explore from './explore';

const Routes = ({match}) => (
  <div>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}explore`} component={Explore}/>
      <ErrorBoundaryRoute exact path={`${match.url}courses/:id`} component={Course}/>
    </Switch>
  </div>
);

export default Routes;
