import React from 'react';
import {DropdownItem} from 'reactstrap';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {getLoginUrl, REDIRECT_URL} from 'app/shared/util/url-utils';
import {NavDropdown} from './menu-components';

const rememberRedirect = () => {
  localStorage.setItem(REDIRECT_URL, window.location.pathname);
};

const accountMenuItemsAuthenticated = () => (
  <>
    <DropdownItem id="logout-item" tag="a" onClick={rememberRedirect} href="/logout" data-cy="logout">
      <FontAwesomeIcon icon="sign-out-alt"/> Sign out
    </DropdownItem>
  </>
);

const accountMenuItems = () => (
  <>
    <DropdownItem id="login-item" tag="a" onClick={rememberRedirect} href={getLoginUrl()} data-cy="login">
      <FontAwesomeIcon icon="sign-in-alt"/> Sign in
    </DropdownItem>
  </>
);

export const AccountMenu = ({isAuthenticated = false}) => (
  <NavDropdown icon="user" name="Account" id="account-menu" data-cy="accountMenu">
    {isAuthenticated ? accountMenuItemsAuthenticated() : accountMenuItems()}
  </NavDropdown>
);

export default AccountMenu;
