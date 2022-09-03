import React from 'react';
import {Switch} from 'react-router-dom';
import Loadable from 'react-loadable';

import Home from 'app/modules/home/home';
import PrivateRoute from 'app/shared/auth/private-route';
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';
import PageNotFound from 'app/shared/error/page-not-found';
import {AUTHORITIES} from 'app/config/constants';
import Explore from "app/modules/explore";
import Learn from "app/modules/learn";
import Course from "app/entities/course";

const Admin = Loadable({
  loader: () => import(/* webpackChunkName: "administration" */ 'app/modules/administration'),
  loading: () => <div>loading ...</div>,
});

const Routes = () => {
  return (
    <div className="view-routes">
      <Switch>
        <PrivateRoute path="/admin" component={Admin} hasAnyAuthorities={[AUTHORITIES.ADMIN]}/>
        <PrivateRoute path="/entities/course" component={Course} hasAnyAuthorities={[AUTHORITIES.USER]}/>
        <ErrorBoundaryRoute path="/" exact component={Home}/>
        <ErrorBoundaryRoute path="/explore" component={Explore}/>
        <PrivateRoute path="/learn" component={Learn}/>
        <ErrorBoundaryRoute component={PageNotFound}/>
      </Switch>
    </div>
  );
};

export default Routes;
