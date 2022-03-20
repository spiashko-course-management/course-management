import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';

import { NavDropdown } from './menu-components';

export const EntitiesMenu = props => (
  <NavDropdown icon="th-list" name="Entities" id="entity-menu" data-cy="entity" style={{ maxHeight: '80vh', overflow: 'auto' }}>
    <>{/* to avoid warnings when empty */}</>
    <MenuItem icon="asterisk" to="/course">
      Course
    </MenuItem>
    <MenuItem icon="asterisk" to="/course-details">
      Course Details
    </MenuItem>
    <MenuItem icon="asterisk" to="/module">
      Module
    </MenuItem>
    <MenuItem icon="asterisk" to="/lesson">
      Lesson
    </MenuItem>
    <MenuItem icon="asterisk" to="/lesson-details">
      Lesson Details
    </MenuItem>
    <MenuItem icon="asterisk" to="/completed-lesson">
      Completed Lesson
    </MenuItem>
    <MenuItem icon="asterisk" to="/enrollment">
      Enrollment
    </MenuItem>
    <MenuItem icon="asterisk" to="/user-details">
      User Details
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
