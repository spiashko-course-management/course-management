import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';

import { NavDropdown } from './menu-components';

export const EntitiesMenu = props => (
  <NavDropdown icon="th-list" name="Entities" id="entity-menu" data-cy="entity" style={{ maxHeight: '80vh', overflow: 'auto' }}>
    <>{/* to avoid warnings when empty */}</>
    <MenuItem icon="asterisk" to="/entities/course">
      Course
    </MenuItem>
    <MenuItem icon="asterisk" to="/entities/course-extra-info">
      Course Extra Info
    </MenuItem>
    <MenuItem icon="asterisk" to="/entities/module">
      Module
    </MenuItem>
    <MenuItem icon="asterisk" to="/entities/lesson">
      Lesson
    </MenuItem>
    <MenuItem icon="asterisk" to="/entities/lesson-extra-info">
      Lesson Extra Info
    </MenuItem>
    <MenuItem icon="asterisk" to="/entities/completed-lesson">
      Completed Lesson
    </MenuItem>
    <MenuItem icon="asterisk" to="/entities/enrollment">
      Enrollment
    </MenuItem>
    <MenuItem icon="asterisk" to="/entities/user-extra-info">
      User Extra Info
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
