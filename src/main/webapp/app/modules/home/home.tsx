import './home.scss';

import React, {useEffect} from 'react';
import {Redirect, RouteComponentProps} from 'react-router-dom';

import {REDIRECT_URL} from 'app/shared/util/url-utils';

export const Home = (props: RouteComponentProps<{ url: string }>) => {

  const redirectURL = localStorage.getItem(REDIRECT_URL);

  useEffect(() => {
    if (redirectURL) {
      localStorage.removeItem(REDIRECT_URL);
    }
  });

  return (
    <Redirect
      to={{
        pathname: redirectURL ? redirectURL : '/explore',
        search: props.location.search,
        state: {from: props.location},
      }}
    />);
};

export default Home;
