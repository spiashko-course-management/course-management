import React from 'react';
import {Switch} from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Instructor from "app/modules/explore/instructor";
import Enrollments from "app/modules/learn/enrollments";

const Routes = ({match}) => (
  <div>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}`} component={Enrollments}/>
    </Switch>
  </div>
);

export default Routes;
