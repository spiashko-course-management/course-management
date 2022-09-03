import React from 'react';

import { NavItem, NavLink, NavbarBrand } from 'reactstrap';
import { NavLink as Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export const BrandIcon = props => (
  <div {...props} className="brand-icon">
    <img src="content/images/logo-jhipster.png" alt="Logo" />
  </div>
);

export const Brand = () => (
  <NavbarBrand tag={Link} to="/" className="brand-logo">
    <BrandIcon />
    <span className="brand-title">Course_management</span>
    <span className="navbar-version">{VERSION}</span>
  </NavbarBrand>
);

export const Explore = () => (
  <NavItem>
    <NavLink tag={Link} to="/explore" className="d-flex align-items-center">
      <FontAwesomeIcon icon="magnifying-glass" />
      <span>Explore</span>
    </NavLink>
  </NavItem>
);

export const MyEnrollments = () => (
  <NavItem>
    <NavLink tag={Link} to="/learn" className="d-flex align-items-center">
      <FontAwesomeIcon icon="graduation-cap" />
      <span>MyEnrollments</span>
    </NavLink>
  </NavItem>
);
